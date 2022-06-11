package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.bean.BusMobileLocation;

@Path("/admin/busMobileLocation")
public class BusMobileLocationService extends EntityService<BusMobileLocation> {

	public BusMobileLocationService() {
		super(BusMobileLocation.class);
	}

}
