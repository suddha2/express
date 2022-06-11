package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.bean.BusFare;

@Path("/admin/busFare")
public class BusFareService extends EntityService<BusFare> {

	public BusFareService() {
		super(BusFare.class);
	}
}
