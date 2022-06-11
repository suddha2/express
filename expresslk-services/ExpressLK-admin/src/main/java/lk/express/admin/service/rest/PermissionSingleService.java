package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.admin.PermissionSingle;

@Path("/admin/permissionSingle")
public class PermissionSingleService extends HasCodeEntityService<PermissionSingle> {

	public PermissionSingleService() {
		super(PermissionSingle.class);
	}
}
