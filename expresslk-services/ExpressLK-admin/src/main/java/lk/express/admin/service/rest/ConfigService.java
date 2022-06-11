package lk.express.admin.service.rest;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;

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
import lk.express.config.Configuration;
import lk.express.config.ConfigurationManager;
import lk.express.db.HibernateUtil;
import lk.express.service.RestService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/admin/config")
public class ConfigService extends RestService {

	private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);
	private static final Configuration config = ConfigurationManager.getConfiguration();

	private final RESTAuthenticationHandler authHandler;
	private @Context HttpHeaders httpHeaders;

	public ConfigService() {
		authHandler = new RESTAuthenticationHandler(Configuration.class);
	}

	@GET
	@Path("/{config}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getConfigValue(@PathParam("config") String configName) {
		if (authHandler.isAllowed(httpHeaders, CRUD.Read)) {
			try {
				String configValue = config.getString(configName);
				logger.debug("Config " + configName + ": " + configValue);
				return Response.status(OK).entity(configValue).build();
			} catch (Exception e) {
				logger.error("Error while retrieving config value", e);
				HibernateUtil.rollback();
				return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
						.build();
			}
		} else {
			return authHandler.buildErrorResponse(httpHeaders, CRUD.Read);
		}
	}
}
