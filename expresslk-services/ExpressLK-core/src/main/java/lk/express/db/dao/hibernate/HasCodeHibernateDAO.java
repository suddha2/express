package lk.express.db.dao.hibernate;

import lk.express.bean.Entity;
import lk.express.bean.HasNameCode;
import lk.express.db.dao.HasCodeDAO;

import org.hibernate.criterion.Restrictions;

public class HasCodeHibernateDAO<T extends Entity & HasNameCode> extends GenericHibernateDAO<T> implements
		HasCodeDAO<T> {

	public HasCodeHibernateDAO() {
	}

	public HasCodeHibernateDAO(Class<T> clazz) {
		super(clazz);
	}

	@Override
	public T get(String code) {
		return findUniqueByCriteria(Restrictions.like("code", code));
	}
}
