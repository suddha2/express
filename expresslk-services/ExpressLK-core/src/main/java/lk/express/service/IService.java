package lk.express.service;

import lk.express.bean.Entity;

public interface IService {

	/**
	 * Save an {@link Entity} with the system.
	 * 
	 * @param t
	 *            entity to be saved
	 * @return entity with assigned id
	 */
	<T extends Entity> T save(T t);

	/**
	 * Save a list of entities with the system.
	 * 
	 * @param list
	 *            list of entity to be saved
	 * @return list of entities with assigned ids
	 */
	<T extends Entity> T[] saveList(T[] list);

	/**
	 * Checks the health of the service
	 * 
	 * @return {@code true} if healthy, {@code false} otherwise
	 */
	boolean heartBeat();
}
