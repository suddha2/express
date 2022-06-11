package lk.express.admin.service.rest;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import lk.express.admin.UserLogin;
import lk.express.bean.Entity;

@Path("/admin/userLogin")
public class UserLoginService extends EntityService<UserLogin> {

	public UserLoginService() {
		super(UserLogin.class);
	}

	@Override
	protected <T extends Entity> Response updateEntity(T t) {
		return Response.status(Status.FORBIDDEN).build();
	}

	@Override
	protected <T extends Entity> Response deleteEntity(Class<T> clazz, String idString) {
		return Response.status(Status.FORBIDDEN).build();
	}
}
