package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.admin.PermissionGroup;

@Path("/admin/permissionGroup")
public class PermissionGroupService extends HasCodeEntityService<PermissionGroup> {

	public PermissionGroupService() {
		super(PermissionGroup.class);
	}
}
