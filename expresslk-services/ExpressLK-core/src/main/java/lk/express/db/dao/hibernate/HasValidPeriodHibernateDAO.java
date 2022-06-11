package lk.express.db.dao.hibernate;

import java.util.Date;
import java.util.List;

import lk.express.bean.Entity;
import lk.express.bean.HasValidPeriod;
import lk.express.db.dao.HasValidPeriodDAO;

import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

public class HasValidPeriodHibernateDAO<T extends Entity & HasValidPeriod> extends GenericHibernateDAO<T> implements
		HasValidPeriodDAO<T> {

	@Override
	public List<T> findValid(Date start, Date end) {
		Disjunction endDisj = Restrictions.disjunction();
		endDisj.add(Restrictions.isNull("endTime"));
		endDisj.add(Restrictions.lt("endTime", end));
		return findByCriteria(Restrictions.gt("startTime", start), endDisj);
	}

	@Override
	public List<T> findValid() {
		Date now = new Date();
		Disjunction endDisj = Restrictions.disjunction();
		endDisj.add(Restrictions.isNull("endTime"));
		endDisj.add(Restrictions.gt("endTime", now));
		return findByCriteria(Restrictions.lt("startTime", now), endDisj);
	}
}
