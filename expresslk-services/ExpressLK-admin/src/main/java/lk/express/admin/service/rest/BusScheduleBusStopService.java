package lk.express.admin.service.rest;

import static javax.ws.rs.core.Response.Status.CONFLICT;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import lk.express.bean.Entity;
import lk.express.db.HibernateUtil;
import lk.express.schedule.BusScheduleBusStop;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/admin/busScheduleBusStop")
public class BusScheduleBusStopService extends HasDepartureArrivalService<BusScheduleBusStop> {

	private static final Logger logger = LoggerFactory.getLogger(BusScheduleBusStopService.class);

	public BusScheduleBusStopService() {
		super(BusScheduleBusStop.class);
	}

	@Override
	protected <T extends Entity> Response handleConstraintViolationExceptionOnPut(ConstraintViolationException e, T t) {
		logger.error(
				"Constraint violation exception while inserting/updating entity: A bus stop with same index for this schedule already exists!",
				e);
		HibernateUtil.rollback();
		return Response.status(CONFLICT)
				.entity(new RestErrorResponse("A bus stop with same index for this schedule already exists!")).build();
	}
}
