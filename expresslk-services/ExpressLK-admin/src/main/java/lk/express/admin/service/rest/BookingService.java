package lk.express.admin.service.rest;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import lk.express.Context;
import lk.express.admin.User;
import lk.express.bean.Entity;
import lk.express.db.HibernateUtil;
import lk.express.reservation.Booking;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/admin/booking")
public class BookingService extends EntityService<Booking> {

	private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

	public BookingService() {
		super(Booking.class);
	}

	@Override
	public Map<String, List<String>> getQueryParameters(UriInfo uriInfo) {
		Map<String, List<String>> queryParams = super.getQueryParameters(uriInfo);
		addAgentRestriction(queryParams);
		return queryParams;
	}

	@Override
	public <T extends Entity> Response getEntity(Class<T> clazz, String idString) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put("id", Arrays.asList(idString));
		addAgentRestriction(queryParams);

		Response response = getEntities(clazz, queryParams);
		Object entities = response.getEntity();
		if (entities != null && entities instanceof List<?>) {
			Object entity = null;
			List<?> list = (List<?>) entities;
			if (!list.isEmpty()) {
				entity = list.get(0);
			}
			return Response.fromResponse(response).entity(entity).build();
		} else {
			return response;
		}
	}

	private void addAgentRestriction(Map<String, List<String>> queryParams) {
		User user = Context.getSessionData().getUser();
		if (user.getAgent() != null) {
			queryParams.put("agent", Arrays.asList(String.valueOf(user.getAgent().getId())));
		}
	}

	@GET
	@Path("/bySchedule/{scheduleId}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getBySchedule(@PathParam("scheduleId") String scheduleIdString) {
		Integer scheduleId = null;
		try {
			scheduleId = Integer.valueOf(scheduleIdString);
		} catch (NumberFormatException nfe) {
			return Response.status(BAD_REQUEST).entity(new RestErrorResponse(MSG_INVALID_ID)).build();
		}
		try {
			String hql = "SELECT DISTINCT booking FROM Booking AS booking"
					+ " INNER JOIN booking.bookingItems AS bookingItem INNER JOIN bookingItem.schedule AS schedule"
					+ " INNER JOIN schedule.scheduleStops AS scheduleStop"
					+ " WHERE bookingItem.schedule.id = :scheduleId AND scheduleStop.stop = bookingItem.fromBusStop"
					+ " ORDER BY scheduleStop.idx";
			Query query = HibernateUtil.getCurrentSession().createQuery(hql);
			query.setParameter("scheduleId", scheduleId);
			@SuppressWarnings("unchecked")
			List<Booking> bookings = query.list();
			return Response.status(OK).entity(bookings).build();
		} catch (Exception e) {
			logger.error("Error while retrieving bookings by the schedule", e);
			HibernateUtil.rollback();
			return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
					.build();
		}
	}

	@Override
	protected <T extends Entity> Response insertEntity(T t) {
		return Response.status(Status.FORBIDDEN).build();
	}
}
