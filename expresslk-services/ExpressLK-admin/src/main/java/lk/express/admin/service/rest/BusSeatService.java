package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.bean.BusSeat;

@Path("/admin/busSeat")
public class BusSeatService extends EntityService<BusSeat> {

	public BusSeatService() {
		super(BusSeat.class);
	}
}
