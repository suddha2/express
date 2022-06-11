package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.reservation.Change;

@Path("/admin/change")
public class ChangeService extends EntityService<Change> {

	public ChangeService() {
		super(Change.class);
	}

}
