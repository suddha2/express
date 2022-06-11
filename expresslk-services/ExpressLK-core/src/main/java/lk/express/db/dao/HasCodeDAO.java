package lk.express.db.dao;

import lk.express.bean.HasNameCode;

public interface HasCodeDAO<T extends HasNameCode> extends GenericDAO<T> {

	/**
	 * Retrieves an entity by code.
	 * 
	 * @param code
	 *            entity code
	 * @return the entity
	 */
	T get(String code);
}
