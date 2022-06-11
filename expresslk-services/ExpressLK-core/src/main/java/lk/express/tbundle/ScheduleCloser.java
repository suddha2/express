package lk.express.tbundle;

import java.util.Date;
import java.util.List;

import lk.express.Context;
import lk.express.SessionManager;
import lk.express.admin.User;
import lk.express.bean.OperationalStage;
import lk.express.db.HibernateUtil;
import lk.express.db.dao.DAOFactory;
import lk.express.db.dao.GenericDAO;
import lk.express.db.dao.HasCodeDAO;
import lk.express.schedule.BusSchedule;

import org.hibernate.criterion.Restrictions;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduleCloser implements Job {

	private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
	private static final DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			HibernateUtil.beginTransaction();
			Context.getSessionData().empty().setUsername(User.SYS_USER);

			@SuppressWarnings("unchecked")
			List<BusSchedule> schedules = HibernateUtil.getCurrentSession().createCriteria(BusSchedule.class)
					.add(Restrictions.lt("webBookingEndTime", new Date())).createAlias("stage", "stage")
					.add(Restrictions.eq("stage.code", OperationalStage.OpenForBooking)).list();

			GenericDAO<BusSchedule> scheduleDAO = daoFac.getBusScheduleDAO();
			HasCodeDAO<OperationalStage> stageDAO = daoFac.getOperationalStageDAO();
			for (BusSchedule schedule : schedules) {
				logger.info("Closing schedule for booking - Schedule {}", new Object[] { schedule.getId() });
				schedule.setStage(stageDAO.get(OperationalStage.ClosedForBooking));
				scheduleDAO.persist(schedule);
			}

			HibernateUtil.commit();
		} catch (Exception e) {
			logger.error("Exception while closing schedule for booking", e);
			HibernateUtil.rollback();
		}
	}
}
