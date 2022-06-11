package lk.express.admin.service.rest;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import lk.express.bean.Entity;
import lk.express.db.HibernateUtil;
import lk.express.schedule.BusSchedule;
import lk.express.schedule.IncompatibleScheduleChangeException;
import lk.express.schedule.ScheduleManager;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/admin/busSchedule")
public class BusScheduleService extends HasDepartureArrivalService<BusSchedule> {

	private static final Logger logger = LoggerFactory.getLogger(BusScheduleService.class);

	public BusScheduleService() {
		super(BusSchedule.class);
	}

	@Override
	protected <T extends Entity> Response insertEntity(T t) {
		BusSchedule schedule = (BusSchedule) t;
		BusSchedule s = (BusSchedule) HibernateUtil.getCurrentSession().createCriteria(BusSchedule.class)
				.add(Restrictions.eq("departureTime", schedule.getDepartureTime())).createAlias("bus", "bus")
				.add(Restrictions.eq("bus.id", schedule.getBus().getId())).uniqueResult();
		if (s != null) {
			logger.error("Constraint violation exception while inserting/updating entity: A schedule with the same departure time for this bus already exists!");
			HibernateUtil.rollback();
			return Response
					.status(CONFLICT)
					.entity(new RestErrorResponse(
							"A schedule with the same departure time for this bus already exists!")).build();
		}
		return super.insertEntity(t);
	}

	@Override
	protected <T extends Entity> Response updateEntity(T t) {
		logger.info("Updating  entity. class: {}, entity: {}", new Object[] { t.getClass().getCanonicalName(), t });
		try {
			BusSchedule schedule = (BusSchedule) t;
			populateEntity(schedule);

			BusSchedule oldSchedule = load(clazz, schedule.getId());
			ScheduleManager scheduleManager = new ScheduleManager(oldSchedule);

			// Perform important schedule changes via schedule manager
			// activation/de-activation
			if (oldSchedule.isActive() != schedule.isActive()) {
				scheduleManager.activateSchedule(schedule.isActive());
			}
			// rescheduling
			if (oldSchedule.getDepartureTime().getTime() != schedule.getDepartureTime().getTime()) {
				scheduleManager.reschedule(schedule.getDepartureTime());
				// replace the arrival time with calculated arrival time
				schedule.setArrivalTime(oldSchedule.getArrivalTime());
			}
			// driver change
			if (oldSchedule.getDriver().getId() != schedule.getDriver().getId()) {
				scheduleManager.changeDriver(schedule.getDriver());
			}
			// conductor change
			if (oldSchedule.getConductor().getId() != schedule.getConductor().getId()) {
				scheduleManager.changeConductor(schedule.getConductor());
			}
			// bus change
			try {
				if (oldSchedule.getBus().getId() != schedule.getBus().getId()) {
					scheduleManager.changeBus(schedule.getBus());
				}
			} catch (IncompatibleScheduleChangeException e) {
				logger.error("Error while changing the bus of the schedule", e);
				HibernateUtil.rollback();
				return Response.status(CONFLICT).entity(new RestErrorResponse(e.getMessage())).build();
			}

			Session session = HibernateUtil.getCurrentSession();
			// merge any other changes
			schedule = (BusSchedule) session.merge(schedule);
			session.flush();

			return Response.status(OK).entity(schedule).build();

		} catch (ConstraintViolationException e) {
			return handleConstraintViolationExceptionOnPut(e, t);
		} catch (Exception e) {
			logger.error("Error while updating schedule", e);
			HibernateUtil.rollback();
			return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
					.build();
		}
	}

	@GET
	@Path("/nextForBus/{busId}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getNextForBus(@PathParam("busId") String busIdString) {
		Integer busId = null;
		try {
			busId = Integer.valueOf(busIdString);
		} catch (NumberFormatException nfe) {
			return Response.status(BAD_REQUEST).entity(new RestErrorResponse(MSG_INVALID_ID)).build();
		}
		try {
			String hql = "FROM BusSchedule schedule"
					+ " WHERE schedule.bus.id = :busId AND schedule.arrivalTime > :now"
					+ " ORDER BY schedule.arrivalTime ASC";
			Query query = HibernateUtil.getCurrentSession().createQuery(hql);
			query.setMaxResults(1); // LIMIT 1
			query.setParameter("busId", busId);
			query.setParameter("now", new Date());
			BusSchedule schedule = (BusSchedule) query.uniqueResult();

			return Response.status(OK).entity(schedule).build();
		} catch (Exception e) {
			logger.error("Error while retrieving next schedule for the bus", e);
			HibernateUtil.rollback();
			return Response.status(INTERNAL_SERVER_ERROR).entity(MSG_INTERNAL_SERVER_ERROR).build();
		}
	}
}
