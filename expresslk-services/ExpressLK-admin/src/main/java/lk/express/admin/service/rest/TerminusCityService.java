package lk.express.admin.service.rest;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import lk.express.CRUD;
import lk.express.auth.rest.RESTAuthenticationHandler;
import lk.express.bean.City;
import lk.express.db.HibernateUtil;
import lk.express.service.RestService;
import lk.express.suggest.CitySuggester;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/admin/terminusCity")
public class TerminusCityService extends RestService {

	private static final Logger logger = LoggerFactory.getLogger(TerminusCityService.class);

	protected RESTAuthenticationHandler authHandler;
	protected @Context HttpHeaders httpHeaders;
	protected CitySuggester suggester;

	public TerminusCityService() {
		authHandler = new RESTAuthenticationHandler(City.class);
		suggester = new CitySuggester();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getDepartureCities(@QueryParam("q") String q, @QueryParam(PAGE_START) String s,
			@QueryParam(PAGE_ROWS) String r) {
		if (authHandler.isAllowed(httpHeaders, CRUD.Read)) {
			return getDepartureCitiesLocal(q, s, r);
		} else {
			return authHandler.buildErrorResponse(httpHeaders, CRUD.Read);
		}
	}

	protected Response getDepartureCitiesLocal(String q, String s, String r) {
		try {
			Integer start = getStart(s);
			Integer rows = getLimit(r, DEFAULT_PAGE_ROWS);
			List<City> cities = suggester.suggestDepartures(q, start, rows);
			return Response.status(OK).entity(cities).build();
		} catch (IllegalArgumentException e) {
			return Response.status(BAD_REQUEST).build();
		} catch (Exception e) {
			logger.error("Error while retrieving departure cities", e);
			HibernateUtil.rollback();
			return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
					.build();
		}
	}

	@GET
	@Path("/{origin}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getDestinationCities(@PathParam("origin") String originString, @QueryParam("q") String q,
			@QueryParam(PAGE_START) String s, @QueryParam(PAGE_ROWS) String r) {
		if (authHandler.isAllowed(httpHeaders, CRUD.Read)) {
			return getDestinationCitiesLocal(originString, q, s, r);
		} else {
			return authHandler.buildErrorResponse(httpHeaders, CRUD.Read);
		}
	}

	protected Response getDestinationCitiesLocal(String originString, String q, String s, String r) {
		try {
			Integer originId = null;
			try {
				originId = Integer.valueOf(originString);
			} catch (NumberFormatException nfe) {
				throw new IllegalArgumentException();
			}

			Integer start = getStart(s);
			Integer rows = getLimit(r, DEFAULT_PAGE_ROWS);

			List<City> destinations = suggester.suggestDestinations(originId, q, start, rows);
			return Response.status(OK).entity(destinations).build();
		} catch (IllegalArgumentException e) {
			return Response.status(BAD_REQUEST).build();
		} catch (Exception e) {
			logger.error("Error while retrieving destinations for the given origin", e);
			HibernateUtil.rollback();
			return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
					.build();
		}
	}

	private static Integer getStart(String s) throws IllegalArgumentException {
		Integer start = 0;
		if (s != null && !s.isEmpty()) {
			try {
				start = Integer.valueOf(s);
				if (start < 0) {
					throw new IllegalArgumentException();
				}
			} catch (NumberFormatException nfe) {
				throw new IllegalArgumentException();
			}
		}
		return start;
	}

	private static Integer getLimit(String r, int defaultRows) throws IllegalArgumentException {
		Integer rows = defaultRows;
		if (r != null && !r.isEmpty()) {
			try {
				rows = Integer.valueOf(r);
				if (rows == -1) {
					return null;
				}
				if (rows < 0) {
					throw new IllegalArgumentException();
				}
			} catch (NumberFormatException nfe) {
				throw new IllegalArgumentException();
			}
		}
		return rows;
	}
}
