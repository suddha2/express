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
import lk.express.suggest.SpellCorrectingCitySuggester;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/admin/suggest")
public class CitySuggestService extends RestService {

	private static final Logger logger = LoggerFactory.getLogger(DestinationService.class);

	private final RESTAuthenticationHandler authHandler;
	private final CitySuggester suggester;
	private @Context HttpHeaders httpHeaders;

	public CitySuggestService() {
		authHandler = new RESTAuthenticationHandler(City.class);
		suggester = new SpellCorrectingCitySuggester();
	}

	@GET
	@Path("city/{text}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response suggestCities(@PathParam("text") String text, @QueryParam(PAGE_START) String s,
			@QueryParam(PAGE_ROWS) String r) {
		if (authHandler.isAllowed(httpHeaders, CRUD.Read)) {
			try {
				Integer start = getStart(s);
				Integer rows = getLimit(r, DEFAULT_PAGE_ROWS);
				List<City> cities = suggester.suggestDepartures(text, start, rows);
				return Response.status(OK).entity(cities).build();
			} catch (Exception e) {
				logger.error("Error while suggesting cities", e);
				HibernateUtil.rollback();
				return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
						.build();
			}
		} else {
			return authHandler.buildErrorResponse(httpHeaders, CRUD.Read);
		}
	}

	@GET
	@Path("destination/{origin}/{text}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response suggestDestinations(@PathParam("origin") String origin, @PathParam("text") String text,
			@QueryParam(PAGE_START) String s, @QueryParam(PAGE_ROWS) String r) {
		if (authHandler.isAllowed(httpHeaders, CRUD.Read)) {
			try {
				Integer start = getStart(s);
				Integer rows = getLimit(r, DEFAULT_PAGE_ROWS);
				List<City> destinations = suggester.suggestDestinations(origin, text, start, rows);
				return Response.status(OK).entity(destinations).build();
			} catch (IllegalArgumentException e) {
				return Response.status(BAD_REQUEST).build();
			} catch (Exception e) {
				logger.error("Error while suggesting destinations", e);
				HibernateUtil.rollback();
				return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
						.build();
			}
		} else {
			return authHandler.buildErrorResponse(httpHeaders, CRUD.Read);
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
