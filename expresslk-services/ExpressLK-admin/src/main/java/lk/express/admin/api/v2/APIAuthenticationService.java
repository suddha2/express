package lk.express.admin.api.v2;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import lk.express.admin.service.rest.UserService;

@Path("/admin/authentication")
public class APIAuthenticationService {

	private UserService delegate;

	public APIAuthenticationService() {
		delegate = new UserService();
	}

	@POST
	@Path("/getToken")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getToken(@FormParam("username") String username, @FormParam("password") String password) {
		return delegate.getToken(username, password);
	}

	@POST
	@Path("/verifyToken")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response verifyToken(@FormParam("username") String username, @FormParam("token") String token) {
		return delegate.verifyToken(username, token);
	}
}
