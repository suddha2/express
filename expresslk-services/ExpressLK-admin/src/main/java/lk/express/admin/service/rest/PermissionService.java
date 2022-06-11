package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.admin.Permission;

@Path("/admin/permission")
public class PermissionService extends HasCodeEntityService<Permission> {
	public PermissionService() {
		super(Permission.class);
	}
}
