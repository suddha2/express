package lk.express.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;

import lk.express.bean.Entity;

public class WSService extends AbstractService {

	/**
	 * Save an {@link Entity} with the system.
	 * 
	 * @param t
	 *            entity to be saved
	 * @return entity with assigned id
	 */
	@Override
	@WebMethod
	public <T extends Entity> T save(@WebParam(name = "entity") T t) {
		return super.save(t);
	}

	/**
	 * Save a list of entities with the system.
	 * 
	 * @param list
	 *            list of entity to be saved
	 * @return list of entities with assigned ids
	 */
	@Override
	@WebMethod
	public <T extends Entity> T[] saveList(@WebParam(name = "list") T[] list) {
		return super.saveList(list);
	}

	/**
	 * Checks the health of the service
	 * 
	 * @return {@code true} if healthy, {@code false} otherwise
	 */
	@Override
	@WebMethod
	public boolean heartBeat() {
		return super.heartBeat();
	}
}
