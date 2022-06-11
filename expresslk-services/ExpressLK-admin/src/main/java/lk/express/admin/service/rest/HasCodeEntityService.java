package lk.express.admin.service.rest;

import static javax.ws.rs.core.Response.Status.CONFLICT;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import lk.express.CRUD;
import lk.express.bean.Entity;
import lk.express.bean.HasNameCode;
import lk.express.db.HibernateUtil;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HasCodeEntityService<T extends Entity & HasNameCode> extends EntityService<T> {

	protected static final String MSG_UNIQUE_CODE = "Code has to be unique!";

	private static final Logger logger = LoggerFactory.getLogger(HasCodeEntityService.class);

	public HasCodeEntityService(Class<T> clazz) {
		super(clazz);
	}

	/**
	 * Returns the entity identified by the parameter {@code code}
	 * 
	 * @param code
	 *            code of the entity
	 * @return Response object containing the entity identified by the parameter
	 *         {@code code}
	 */
	@GET
	@Path("/byCode/{code}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getEntityByCode(@PathParam("code") String code) {
		if (authHandler.isAllowed(httpHeaders, CRUD.Read)) {
			return getEntityByCode(clazz, code);
		} else {
			return authHandler.buildErrorResponse(httpHeaders, CRUD.Read);
		}
	}

	/**
	 * Deletes the entity identified by the parameter {@code code}
	 * 
	 * @param code
	 *            code of the entity
	 * @return void
	 */
	@DELETE
	@Path("/byCode/{code}")
	public Response deleteEntityByCode(@PathParam("code") String code) {
		if (authHandler.isAllowed(httpHeaders, CRUD.Delete)) {
			return deleteEntityByCode(clazz, code);
		} else {
			return authHandler.buildErrorResponse(httpHeaders, CRUD.Delete);
		}
	}

	@Override
	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getEntity(@PathParam("id") String idString) {
		try {
			Integer.valueOf(idString);
			return super.getEntity(idString);
		} catch (NumberFormatException nfe) {
			return getEntityByCode(idString);
		}
	}

	@Override
	@DELETE
	@Path("/{id}")
	public Response deleteEntity(@PathParam("id") String idString) {
		try {
			Integer.valueOf(idString);
			return super.deleteEntity(idString);
		} catch (NumberFormatException nfe) {
			return deleteEntityByCode(idString);
		}
	}

	@Override
	@SuppressWarnings("hiding")
	protected <T extends Entity> Response handleConstraintViolationExceptionOnPut(ConstraintViolationException e, T t) {
		logger.debug("Constraint violation exception while inserting/updating entity: code has to be unique", e);
		HibernateUtil.rollback();
		return Response.status(CONFLICT).entity(new RestErrorResponse(MSG_UNIQUE_CODE)).build();
	}
}
