package lk.express.admin.service.rest;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import lk.express.CRUD;
import lk.express.auth.rest.RESTAuthenticationHandler;
import lk.express.bean.BusSector;
import lk.express.bean.City;
import lk.express.db.HibernateUtil;
import lk.express.service.RestService;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/admin/destination")
public class DestinationService extends RestService {

	private static final Logger logger = LoggerFactory.getLogger(DestinationService.class);

	private final RESTAuthenticationHandler authHandler;
	private @Context HttpHeaders httpHeaders;

	public DestinationService() {
		authHandler = new RESTAuthenticationHandler(City.class);
	}

	@GET
	@Path("/{origin}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getDestinations(@PathParam("origin") String originString) {
		if (authHandler.isAllowed(httpHeaders, CRUD.Read)) {
			Integer originId = null;
			try {
				originId = Integer.valueOf(originString);
			} catch (NumberFormatException nfe) {
				return Response.status(BAD_REQUEST).entity(new RestErrorResponse(MSG_INVALID_ID)).build();
			}

			try {
				List<City> destinations = new ArrayList<City>(getDestinations(originId));
				Collections.sort(destinations, new Comparator<City>() {
					@Override
					public int compare(City city1, City city2) {
						return city1.getName().compareTo(city2.getName());
					}
				});
				return Response.status(OK).entity(new ArrayList<City>(destinations)).build();
			} catch (Exception e) {
				logger.error("Error while retrieving destinations for the given origin", e);
				HibernateUtil.rollback();
				return Response.status(INTERNAL_SERVER_ERROR).entity(MSG_INTERNAL_SERVER_ERROR).build();
			}
		} else {
			return authHandler.buildErrorResponse(httpHeaders, CRUD.Read);
		}
	}

	public Set<City> getDestinations(int originId) {
		String hql = "FROM BusSector sector WHERE sector.active = 1 AND sector.fromCity.id = :originId";
		Session session = HibernateUtil.getCurrentSession();
		Query query = session.createQuery(hql).setParameter("originId", originId);
		@SuppressWarnings("unchecked")
		List<BusSector> sectors = query.list();

		Set<City> destinationSet = new HashSet<City>();
		for (BusSector sector : sectors) {
			destinationSet.add(sector.getToCity());
		}

		return destinationSet;
	}
}
