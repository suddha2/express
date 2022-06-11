package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.admin.Division;

@Path("/admin/division")
public class DivisionService extends HasCodeEntityService<Division> {

	public DivisionService() {
		super(Division.class);
	}

}
