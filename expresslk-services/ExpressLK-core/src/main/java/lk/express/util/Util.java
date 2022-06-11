package lk.express.util;

import lk.express.bean.Entity;
import lk.express.db.HibernateUtil;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class Util {

	/**
	 * Generates a 6 character alpha numeric unique reference that has not been
	 * used previously.
	 * 
	 * @param beanClass
	 *            bean class which includes the unique column
	 * @param columnName
	 *            name of the unique column
	 * @return a unique reference
	 */
	public static String generateUniqueReference(Class<? extends Entity> beanClass, String columnName) {
		String reference = null;
		while (reference == null || !isUniqueReference(reference, beanClass, columnName)) {
			// generate random string, convert to upper case
			String r = Long.toHexString(Double.doubleToLongBits(Math.random())).toUpperCase();
			// remove some not allowed chars
			r = r.replace("O", "").replace("0", "");
			if (r.length() >= 6) {
				// take last 6 chars
				reference = r.substring(r.length() - 6);
			} else {
				reference = null;
			}
		}
		return reference;
	}

	/**
	 * Checks whether a potential reference has been used before.
	 * 
	 * @param reference
	 *            reference to be checked
	 * @param beanClass
	 *            bean class which includes the unique column
	 * @param columnName
	 *            name of the unique column
	 * 
	 * @return whether is has not been used
	 */
	public static boolean isUniqueReference(String reference, Class<? extends Entity> beanClass, String columnName) {
		Session session = HibernateUtil.getCurrentSession();
		Long count = (Long) session.createCriteria(beanClass).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.add(Restrictions.eq(columnName, reference)).setProjection(Projections.rowCount()).uniqueResult();
		return count == 0;
	}
}
