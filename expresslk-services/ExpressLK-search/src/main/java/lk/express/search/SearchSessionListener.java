package lk.express.search;

import java.util.List;

import lk.express.SessionEvent;
import lk.express.SessionListener;
import lk.express.bean.BusHeldItem;
import lk.express.db.HibernateUtil;
import lk.express.db.dao.DAOFactory;
import lk.express.db.dao.GenericDAO;

import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchSessionListener implements SessionListener {

	private static final Logger logger = LoggerFactory.getLogger(SearchSessionListener.class);

	private static final DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);

	@Override
	public void sessionCreated(SessionEvent event) {
	}

	@Override
	public void sessionDestroyed(SessionEvent event) {

		// Remove items held by the destroyed session
		String sessionId = event.getSession().getId();

		try {
			logger.info("Releasing held items for desroyed session: {}", new Object[] { sessionId });

			HibernateUtil.beginTransaction();
			@SuppressWarnings("unchecked")
			List<BusHeldItem> heldItems = HibernateUtil.getCurrentSession().createCriteria(BusHeldItem.class)
					.add(Restrictions.eq("sessionId", sessionId)).list();

			GenericDAO<BusHeldItem> heldBusItemDAO = daoFac.getHeldBusItemDAO();
			for (BusHeldItem heldItem : heldItems) {
				heldBusItemDAO.delete(heldItem);
			}

			/**
			 * TODO Decide whether you need to cancel the tentative bookings for
			 * this session. To do that you need to have a column in `booking`
			 * table for session id
			 */

			HibernateUtil.commit();
		} catch (Exception e) {
			logger.error("Exception while releasing held items for desroyed session: " + sessionId, e);
			HibernateUtil.rollback();
		}
	}
}
