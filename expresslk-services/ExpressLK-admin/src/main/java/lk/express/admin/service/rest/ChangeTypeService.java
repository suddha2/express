package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.reservation.ChangeType;

@Path("/admin/changeType")
public class ChangeTypeService extends EntityService<ChangeType> {

	public ChangeTypeService() {
		super(ChangeType.class);
	}
}
