package lk.express.admin.service.rest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriInfo;

import lk.express.Context;
import lk.express.admin.User;
import lk.express.admin.UserGroup;

@Path("/admin/userGroup")
public class UserGroupService extends HasCodeEntityService<UserGroup> {
	public UserGroupService() {
		super(UserGroup.class);
	}

	@Override
	public Map<String, List<String>> getQueryParameters(UriInfo uriInfo) {
		Map<String, List<String>> queryParams = super.getQueryParameters(uriInfo);
		User user = Context.getSessionData().getUser();
		queryParams.put("division", Arrays.asList(String.valueOf(user.getDivision().getId())));
		if (user.getAgent() != null) {
			queryParams.put("agent", Arrays.asList(String.valueOf(user.getAgent().getId())));
		}
		return queryParams;
	}
}
