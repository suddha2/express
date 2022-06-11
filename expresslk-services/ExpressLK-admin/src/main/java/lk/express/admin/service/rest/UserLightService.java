package lk.express.admin.service.rest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import lk.express.Context;
import lk.express.admin.User;
import lk.express.admin.UserLight;
import lk.express.bean.Entity;

@Path("/admin/userLight")
public class UserLightService extends LightEntityService<UserLight> {

	public UserLightService() {
		super(UserLight.class);
	}

	@Override
	public Map<String, List<String>> getQueryParameters(UriInfo uriInfo) {
		Map<String, List<String>> queryParams = super.getQueryParameters(uriInfo);
		addAgentRestriction(queryParams);
		return queryParams;
	}

	@Override
	public <T extends Entity> Response getEntity(Class<T> clazz, String idString) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put("id", Arrays.asList(idString));
		addAgentRestriction(queryParams);

		Response response = getEntities(clazz, queryParams);
		Object entities = response.getEntity();
		if (entities != null && entities instanceof List<?>) {
			Object entity = null;
			List<?> list = (List<?>) entities;
			if (!list.isEmpty()) {
				entity = list.get(0);
			}
			return Response.fromResponse(response).entity(entity).build();
		} else {
			return response;
		}
	}

	private void addAgentRestriction(Map<String, List<String>> queryParams) {
		User user = Context.getSessionData().getUser();
		if (user.getAgent() != null) {
			queryParams.put("agent", Arrays.asList(String.valueOf(user.getAgent().getId())));
		}
	}
}
