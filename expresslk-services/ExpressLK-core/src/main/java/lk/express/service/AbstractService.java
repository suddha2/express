package lk.express.service;

import java.util.List;

import lk.express.bean.Entity;
import lk.express.bean.HasNameCode;
import lk.express.db.HibernateUtil;
import lk.express.db.dao.DAOFactory;
import lk.express.db.dao.GenericDAO;
import lk.express.db.dao.HasCodeDAO;

import org.hibernate.Session;

public abstract class AbstractService implements IService {

	/**
	 * Save an {@link Entity} with the system.
	 * 
	 * @param t
	 *            entity to be saved
	 * @return entity with assigned id
	 */
	@Override
	public <T extends Entity> T save(T t) {
		@SuppressWarnings("unchecked")
		GenericDAO<T> dao = getDAO((Class<T>) t.getClass());
		dao.persist(t);
		return t;
	}

	/**
	 * Save a list of entities with the system.
	 * 
	 * @param list
	 *            list of entity to be saved
	 * @return list of entities with assigned ids
	 */
	@Override
	public <T extends Entity> T[] saveList(T[] list) {
		for (T t : list) {
			@SuppressWarnings("unchecked")
			GenericDAO<T> dao = getDAO((Class<T>) t.getClass());
			dao.persist(t);
		}
		return list;
	}

	/**
	 * Checks the health of the service
	 * 
	 * @return {@code true} if healthy, {@code false} otherwise
	 */
	@Override
	public boolean heartBeat() {
		Session session = HibernateUtil.getCurrentSession();
		Object obj = session.createSQLQuery("SELECT 1").uniqueResult();
		return obj != null;
	}

	/**
	 * Loads a particular {@link Entity} of given class identified by the auto
	 * incremented unique id.
	 * 
	 * @param clazz
	 *            class of the entity
	 * @param id
	 *            auto incremented unique id
	 * @return the loaded entity
	 */
	protected <T extends Entity> T load(Class<T> clazz, Integer id) {
		GenericDAO<T> dao = getDAO(clazz);
		T t = dao.get(id);
		return t;
	}

	/**
	 * Loads a particular {@link Entity} of given class identified by the code.
	 * 
	 * @param clazz
	 *            class of the entity
	 * @param code
	 *            code
	 * @return the loaded entity
	 */
	protected <T extends Entity & HasNameCode> T load(Class<T> clazz, String code) {
		HasCodeDAO<T> dao = getHasCodeDAO(clazz);
		T t = dao.get(code);
		return t;
	}

	/**
	 * Delete a particular {@link Entity} of given class identified by the auto
	 * incremented unique id.
	 * 
	 * @param clazz
	 *            class of the entity
	 * @param id
	 *            auto incremented unique id
	 * @return void
	 */
	protected <T extends Entity> void delete(T t) {
		@SuppressWarnings("unchecked")
		GenericDAO<T> dao = getDAO((Class<T>) t.getClass());
		dao.delete(t);
	}

	/**
	 * List all the entities of a particular class saved in the database.
	 * 
	 * @param clazz
	 *            class of the entities
	 * @return list all the entities of a particular class saved in the database
	 */
	protected <T extends Entity> List<T> list(Class<T> clazz) {
		GenericDAO<T> dao = getDAO(clazz);
		List<T> list = dao.list();
		return list;
	}

	protected <T extends Entity> GenericDAO<T> getDAO(Class<T> clazz) {
		DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
		GenericDAO<T> dao = factory.getGenericDAO(clazz);
		return dao;
	}

	protected <T extends Entity & HasNameCode> HasCodeDAO<T> getHasCodeDAO(Class<T> clazz) {
		DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
		HasCodeDAO<T> dao = factory.getHasCodeDAO(clazz);
		return dao;
	}

	protected <T extends HasNameCode> HasCodeDAO<T> getHasCodeDAOWorkaround(Class<T> clazz) {
		DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
		HasCodeDAO<T> dao = factory.getHasCodeDAOWorkaround(clazz);
		return dao;
	}
}
