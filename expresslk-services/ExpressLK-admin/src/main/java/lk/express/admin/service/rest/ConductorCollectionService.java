package lk.express.admin.service.rest;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.CRUD;
import lk.express.admin.Agent;
import lk.express.admin.User;
import lk.express.auth.rest.RESTAuthenticationHandler;
import lk.express.db.HibernateUtil;
import lk.express.reservation.Booking;
import lk.express.reservation.BookingItem;
import lk.express.service.RestService;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/admin/conductorCollection")
public class ConductorCollectionService extends RestService {

	private static final Logger logger = LoggerFactory.getLogger(ConductorCollectionService.class);

	private final RESTAuthenticationHandler authHandler;
	private @Context HttpHeaders httpHeaders;

	public ConductorCollectionService() {
		authHandler = new RESTAuthenticationHandler(Booking.class);
	}

	@GET
	@Path("/{scheduleId}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getConductorCollection(@PathParam("scheduleId") String scheduleIdString) {
		if (authHandler.isAllowed(httpHeaders, CRUD.Read)) {
			Integer scheduleId = null;
			try {
				scheduleId = Integer.valueOf(scheduleIdString);
			} catch (NumberFormatException nfe) {
				return Response.status(BAD_REQUEST).entity(new RestErrorResponse(MSG_INVALID_ID)).build();
			}

			Map<String, ConductorCollection> map = new HashMap<>();

			try {
				List<Booking> bookings = getBySchedule(scheduleId);
				for (Booking booking : bookings) {
					String key, name;
					if (booking.getAgent() != null) {
						Agent agent = booking.getAgent();
						key = "agent_" + agent.getId();
						name = "Agent - " + agent.getName();
					} else if (booking.isReservationOnly()) {
						key = "payAtBus";
						name = "Pay at bus";
					} else if (!booking.getUser().getAccountable()) {
						User user = booking.getUser();
						key = "user_" + user.getId();
						name = user.getFirstName();
					} else {
						key = "web";
						name = "Web";
					}
					ConductorCollection collection = map.get(key);
					if (collection == null) {
						collection = new ConductorCollection(name);
						map.put(key, collection);
					}
					for (BookingItem bookingItem : booking.getBookingItems()) {
						List<String> seatNumbers = bookingItem.getPassengers().stream().map(p -> p.getSeatNumber())
								.collect(Collectors.toList());
						double fare = bookingItem.getFare();
						collection.add(seatNumbers, fare);
					}
				}
				return Response.status(OK).entity(map.values()).build();
			} catch (Exception e) {
				logger.error("Error while calculating conductor collection for the given schedule", e);
				HibernateUtil.rollback();
				return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
						.build();
			}
		} else {
			return authHandler.buildErrorResponse(httpHeaders, CRUD.Read);
		}
	}

	private List<Booking> getBySchedule(Integer scheduleId) {
		String hql = "SELECT booking FROM Booking AS booking INNER JOIN booking.bookingItems AS bookingItem"
				+ " WHERE bookingItem.schedule.id = :scheduleId AND booking.status.code = 'CONF'";
		Query query = HibernateUtil.getCurrentSession().createQuery(hql);
		query.setParameter("scheduleId", scheduleId);
		@SuppressWarnings("unchecked")
		List<Booking> bookings = query.list();
		return bookings;
	}

	@XmlType(name = "ConductorCollection", namespace = "http://bean.express.lk")
	@XmlRootElement
	public static final class ConductorCollection {

		private String name;
		private List<String> seatNumbers = new ArrayList<>();
		private double amountDue;

		public ConductorCollection() {
		}

		public ConductorCollection(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<String> getSeatNumbers() {
			return seatNumbers;
		}

		public void setSeatNumbers(List<String> seatNumbers) {
			this.seatNumbers = seatNumbers;
		}

		public double getAmountDue() {
			return amountDue;
		}

		public void setAmountDue(double amountDue) {
			this.amountDue = amountDue;
		}

		private void add(List<String> seatNumbers, double amountDue) {
			this.seatNumbers.addAll(seatNumbers);
			this.amountDue += amountDue;
		}
	}
}
