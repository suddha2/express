package lk.express.db.dao.hibernate;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import lk.express.bean.Entity;
import lk.express.db.HibernateUtil;
import lk.express.db.TriggerUpdatable;
import lk.express.db.dao.GenericDAO;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;

public class GenericHibernateDAO<T extends Entity> implements GenericDAO<T> {

	protected Class<T> persistentClass;
	protected Session session;

	@SuppressWarnings("unchecked")
	public GenericHibernateDAO() {
		this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
	}

	public GenericHibernateDAO(Class<T> persistentClass) {
		this.persistentClass = persistentClass;
	}

	public void setSession(Session s) {
		this.session = s;
	}

	protected Session getSession() {
		if (session == null) {
			throw new IllegalStateException("Session has not been set on DAO before usage");
		}
		return session;
	}

	public void flush() {
		getSession().flush();
	}

	public void clear() {
		getSession().clear();
	}

	public Class<T> getPersistentClass() {
		return persistentClass;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get(Integer id) {
		if (HibernateUtil.isLimitedVisibility(getPersistentClass())) {
			List<T> list = findByCriteria(Restrictions.eq("id", id));
			if (!list.isEmpty()) {
				return list.get(0);
			}
			return null;
		} else {
			return (T) getSession().get(getPersistentClass(), id);
		}
	}

	@Override
	public List<T> list() {
		return findByCriteria();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> find(T exampleInstance) {
		return getCriteriaForExample(exampleInstance, new String[0]).list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public T findUnique(T exampleInstance) {
		return (T) getCriteriaForExample(exampleInstance, new String[0]).uniqueResult();
	}

	protected Criteria getCriteriaForExample(T exampleInstance, String[] excludeProperty) {
		Criteria crit = HibernateUtil.createCriteria(getSession(), getPersistentClass());
		Example example = Example.create(exampleInstance);
		for (String exclude : excludeProperty) {
			example.excludeProperty(exclude);
		}
		crit.add(example);
		return crit;
	}

	@Override
	public T persist(T entity) {

		entity.validate();

		Session sess = getSession();
		sess.saveOrUpdate(entity);
		if (entity instanceof TriggerUpdatable) {
			sess.flush(); // force the SQL INSERT or UPDATE
			sess.refresh(entity); // re-read the state (after the trigger
									// executes)
		}
		return entity;
	}

	@Override
	public void delete(T entity) {
		getSession().delete(entity);
	}

	/**
	 * Use these inside subclasses as convenience methods.
	 */
	@SuppressWarnings("unchecked")
	public List<T> findByCriteria(Criterion... criterion) {
		Criteria crit = HibernateUtil.createCriteria(getSession(), getPersistentClass());
		for (Criterion c : criterion) {
			crit.add(c);
		}
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	public T findUniqueByCriteria(Criterion... criterion) {
		Criteria crit = HibernateUtil.createCriteria(getSession(), getPersistentClass());
		for (Criterion c : criterion) {
			crit.add(c);
		}
		return (T) crit.uniqueResult();
	}

}
