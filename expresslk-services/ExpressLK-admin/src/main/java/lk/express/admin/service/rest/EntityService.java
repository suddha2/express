package lk.express.admin.service.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import lk.express.CRUD;
import lk.express.auth.rest.RESTAuthenticationHandler;
import lk.express.bean.Entity;
import lk.express.service.RestService;

public abstract class EntityService<T extends Entity> extends RestService {

	protected Class<T> clazz;
	protected RESTAuthenticationHandler authHandler;
	protected @Context HttpHeaders httpHeaders;

	public EntityService(Class<T> clazz) {
		this.clazz = clazz;
		authHandler = new RESTAuthenticationHandler(clazz);
	}

	/**
	 * Returns the list of entities
	 * 
	 * @param uriInfo
	 *            URI information
	 * @return Response object containing the list of entities
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getEntities(@Context UriInfo uriInfo) {
		if (authHandler.isAllowed(httpHeaders, CRUD.Read)) {
			Map<String, List<String>> paramsMap = getQueryParameters(uriInfo);
			return getEntities(clazz, paramsMap);
		} else {
			return authHandler.buildErrorResponse(httpHeaders, CRUD.Read);
		}
	}

	public Map<String, List<String>> getQueryParameters(UriInfo uriInfo) {
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		if (queryParams != null && queryParams.size() > 0) {
			return new HashMap<>(queryParams);
		} else {
			return new HashMap<>();
		}
	}

	/**
	 * Returns the entity identified by the parameter id
	 * 
	 * @param id
	 *            id of the entity
	 * @return Response object containing the entity identified by the parameter
	 *         id
	 */
	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getEntity(@PathParam("id") String id) {
		if (authHandler.isAllowed(httpHeaders, CRUD.Read)) {
			return getEntity(clazz, id);
		} else {
			return authHandler.buildErrorResponse(httpHeaders, CRUD.Read);
		}
	}

	/**
	 * Deletes the entity identified by the parameter id
	 * 
	 * @param id
	 *            id of the entity
	 * @return void
	 */
	@DELETE
	@Path("/{id}")
	public Response deleteEntity(@PathParam("id") String id) {
		if (authHandler.isAllowed(httpHeaders, CRUD.Delete)) {
			return deleteEntity(clazz, id);
		} else {
			return authHandler.buildErrorResponse(httpHeaders, CRUD.Delete);
		}
	}

	/**
	 * Adds or updates an entity
	 * 
	 * @param t
	 *            entity
	 * @return Response object containing the new/updated entity
	 */
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response putEntity(T t) {
		CRUD operation = t.getId() == null ? CRUD.Create : CRUD.Update;
		if (!authHandler.isAllowed(httpHeaders, operation)) {
			return authHandler.buildErrorResponse(httpHeaders, operation);
		}
		return operation == CRUD.Create ? insertEntity(t) : updateEntity(t);
	}
}
