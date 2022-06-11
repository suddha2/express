package lk.express.tbundle;

import java.util.Calendar;
import java.util.List;

import lk.express.Context;
import lk.express.admin.User;
import lk.express.api.APIToken;
import lk.express.db.HibernateUtil;
import lk.express.db.dao.DAOFactory;
import lk.express.db.dao.GenericDAO;

import org.hibernate.criterion.Restrictions;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenRemover implements Job {

	private static final Logger logger = LoggerFactory.getLogger(TokenRemover.class);
	private static final DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			HibernateUtil.beginTransaction();
			Context.getSessionData().empty().setUsername(User.SYS_USER);

			Calendar cal = Calendar.getInstance();
			@SuppressWarnings("unchecked")
			List<APIToken> tokens = HibernateUtil.getCurrentSession().createCriteria(APIToken.class)
					.add(Restrictions.lt("expiry", cal.getTime())).list();
			GenericDAO<APIToken> tokenDAO = daoFac.getGenericDAO(APIToken.class);
			for (APIToken token : tokens) {
				logger.info("Removing expired token {}", new Object[] { token.getToken() });
				tokenDAO.delete(token);
			}

			HibernateUtil.commit();
		} catch (Exception e) {
			logger.error("Exception while removing expired tokens", e);
			HibernateUtil.rollback();
		}
	}
}
