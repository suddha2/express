package lk.express.admin.service.rest;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import lk.express.CRUD;
import lk.express.ClientManager;
import lk.express.bean.Client;
import lk.express.bean.Entity;
import lk.express.db.HibernateUtil;
import lk.express.reservation.Booking;
import lk.express.service.RestService;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/admin/client")
public class ClientService extends EntityService<Client> {

	private static final Logger logger = LoggerFactory.getLogger(ClientService.class);

	public ClientService() {
		super(Client.class);
	}

	@Override
	protected <T extends Entity> Response handleConstraintViolationExceptionOnPut(ConstraintViolationException e, T t) {
		logger.debug(
				"Constraint violation exception while inserting/updating entity: A client entity for the same person already exists!",
				e);
		HibernateUtil.rollback();
		return Response.status(CONFLICT)
				.entity(new RestErrorResponse("A client entity for the same person already exists!")).build();
	}

	@GET
	@Path("/{id}/bookings")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getEntities(@PathParam("id") String idString, @QueryParam(PAGE_START) String position,
			@QueryParam(PAGE_ROWS) String pageSize) {

		if (authHandler.isAllowed(httpHeaders, CRUD.Read)) {
			Integer id = null;
			try {
				try {
					id = Integer.valueOf(idString);
				} catch (NumberFormatException nfe) {
					return Response.status(BAD_REQUEST).entity(new RestErrorResponse(MSG_INVALID_ID)).build();
				}
				int pos = -1;
				try {
					pos = Integer.valueOf(position);
				} catch (NumberFormatException nfe) {
					pos = 0;
				}
				int size = -1;
				try {
					size = Integer.valueOf(pageSize);
				} catch (NumberFormatException nfe) {
					size = RestService.DEFAULT_PAGE_ROWS;
				}
				List<Booking> bookings = ClientManager.getRecentBookings(id, pos, size);
				return Response.status(OK).entity(bookings).build();
			} catch (Exception e) {
				logger.error("Exception while retrieving recent bookings for client: " + id, e);
				HibernateUtil.rollback();
				return Response.status(INTERNAL_SERVER_ERROR).entity(MSG_INTERNAL_SERVER_ERROR).build();
			}
		} else {
			return authHandler.buildErrorResponse(httpHeaders, CRUD.Read);
		}
	}
}
