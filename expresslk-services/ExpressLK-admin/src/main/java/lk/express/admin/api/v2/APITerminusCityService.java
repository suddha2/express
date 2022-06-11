package lk.express.admin.api.v2;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import lk.express.admin.service.rest.TerminusCityService;
import lk.express.api.v2.APICity;
import lk.express.auth.rest.RESTAuthenticationHandler;
import lk.express.bean.City;
import lk.express.db.HibernateUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/admin/terminusCity")
public class APITerminusCityService extends TerminusCityService {

	private static final Logger logger = LoggerFactory.getLogger(APITerminusCityService.class);

	public APITerminusCityService() {
		authHandler = new RESTAuthenticationHandler(APITerminusCityService.class);
	}

	@Override
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getDepartureCities(@QueryParam("q") String q, @QueryParam(PAGE_START) String s,
			@QueryParam(PAGE_ROWS) String r) {
		String method = "getDepartureCities";
		if (authHandler.isAllowed(httpHeaders, method)) {
			Response response = getDepartureCitiesLocal(q, s, r);
			return prepareResponse(response);
		} else {
			return authHandler.buildErrorResponse(httpHeaders, method);
		}
	}

	@Override
	@GET
	@Path("/{origin}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getDestinationCities(@PathParam("origin") String originString, @QueryParam("q") String q,
			@QueryParam(PAGE_START) String s, @QueryParam(PAGE_ROWS) String r) {
		String method = "getDestinationCities";
		if (authHandler.isAllowed(httpHeaders, method)) {
			Response response = getDestinationCitiesLocal(originString, q, s, r);
			return prepareResponse(response);
		} else {
			return authHandler.buildErrorResponse(httpHeaders, method);
		}
	}

	private Response prepareResponse(Response response) {
		Object entities = response.getEntity();

		if (entities != null && entities instanceof List<?>) {
			try {
				@SuppressWarnings("unchecked")
				List<City> entityList = (List<City>) entities;
				List<APICity> apiList = new ArrayList<>();
				for (City entity : entityList) {
					APICity apiEntity = new APICity(entity);
					apiList.add(apiEntity);
				}
				return Response.fromResponse(response).entity(apiList).build();
			} catch (Exception e) {
				logger.error("Error while retrieving entities", e);
				HibernateUtil.rollback();
				return Response.status(BAD_REQUEST).build();
			}
		} else {
			return response;
		}
	}
}
