package lk.express.admin.service.rest;

import static javax.ws.rs.core.Response.Status.CONFLICT;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import lk.express.bean.Bus;
import lk.express.bean.Entity;
import lk.express.db.HibernateUtil;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/admin/bus")
public class BusService extends EntityService<Bus> {

	private static final Logger logger = LoggerFactory.getLogger(BusService.class);

	public BusService() {
		super(Bus.class);
	}

	@Override
	protected <T extends Entity> Response handleConstraintViolationExceptionOnPut(ConstraintViolationException e, T t) {
		logger.debug(
				"Constraint violation exception while inserting/updating entity: A bus with the same plate number exists!",
				e);
		HibernateUtil.rollback();
		return Response.status(CONFLICT).entity(new RestErrorResponse("A bus with the same plate number exists!"))
				.build();
	}
}
