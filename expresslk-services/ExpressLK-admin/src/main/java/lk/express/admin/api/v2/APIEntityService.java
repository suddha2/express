package lk.express.admin.api.v2;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import lk.express.CRUD;
import lk.express.admin.service.rest.EntityService;
import lk.express.api.v2.APIEntity;
import lk.express.auth.rest.RESTAuthenticationHandler;
import lk.express.bean.Entity;
import lk.express.db.HibernateUtil;
import lk.express.service.RestService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class APIEntityService<T extends APIEntity, U extends Entity, V extends EntityService<U>> {

	private static final Logger logger = LoggerFactory.getLogger(APIEntityService.class);

	protected Class<T> apiClazz;
	protected RESTAuthenticationHandler authHandler;
	protected @Context HttpHeaders httpHeaders;

	protected Class<U> clazz;
	protected EntityService<U> delegate;

	public APIEntityService(Class<T> apiClazz, Class<U> clazz, EntityService<U> delegate) {
		this.apiClazz = apiClazz;
		this.clazz = clazz;
		this.delegate = delegate;
		this.authHandler = new RESTAuthenticationHandler(getClass());
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getEntities(@Context UriInfo uriInfo) {
		if (authHandler.isAllowed(httpHeaders, CRUD.Read.getPermission())) {
			Map<String, List<String>> paramsMap = delegate.getQueryParameters(uriInfo);
			moderateQueryParameters(paramsMap);
			Response response = delegate.getEntities(clazz, paramsMap);
			Object entities = response.getEntity();

			if (entities != null && entities instanceof List<?>) {
				try {
					Constructor<T> constructor = apiClazz.getConstructor(clazz);
					List<?> entityList = (List<?>) entities;
					List<Object> apiList = new ArrayList<>();
					for (Object entity : entityList) {
						Object apiEntity = constructor.newInstance(entity);
						apiList.add(apiEntity);
					}
					return Response.fromResponse(response).entity(apiList).build();
				} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException e) {
					logger.error("Error while retrieving entities", e);
					HibernateUtil.rollback();
					return Response.status(BAD_REQUEST).build();
				}
			} else {
				return response;
			}
		} else {
			return authHandler.buildErrorResponse(httpHeaders, CRUD.Read.getPermission());
		}
	}

	protected void moderateQueryParameters(Map<String, List<String>> queryParams) {
		if (!allowReadAll()) {
			queryParams.remove(RestService.PAGE_ROWS);
		}
	}

	protected boolean allowReadAll() {
		return false;
	}

	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getEntity(@PathParam("id") String id) {
		if (authHandler.isAllowed(httpHeaders, CRUD.Read.getPermission())) {
			Response response = delegate.getEntity(clazz, id);
			Object entity = response.getEntity();

			if (entity != null && clazz.isInstance(entity)) {
				try {
					Constructor<T> constructor = apiClazz.getConstructor(clazz);
					Object apiEntity = constructor.newInstance(entity);
					return Response.fromResponse(response).entity(apiEntity).build();
				} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException e) {
					logger.error("Error while retrieving entity", e);
					HibernateUtil.rollback();
					return Response.status(BAD_REQUEST).build();
				}
			} else {
				return response;
			}
		} else {
			return authHandler.buildErrorResponse(httpHeaders, CRUD.Read.getPermission());
		}
	}

	@DELETE
	@Path("/{id}")
	public Response deleteEntity(@PathParam("id") String id) {
		return Response.status(Status.FORBIDDEN).build();
	}

	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response putEntity(U t) {
		return Response.status(Status.FORBIDDEN).build();
	}
}
