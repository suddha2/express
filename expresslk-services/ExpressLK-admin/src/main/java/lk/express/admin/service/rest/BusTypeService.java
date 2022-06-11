package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.bean.BusType;

@Path("/admin/busType")
public class BusTypeService extends EntityService<BusType> {

	public BusTypeService() {
		super(BusType.class);
	}
}
