package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.admin.Module;

@Path("/admin/module")
public class ModuleService extends HasCodeEntityService<Module> {
	public ModuleService() {
		super(Module.class);
	}
}
