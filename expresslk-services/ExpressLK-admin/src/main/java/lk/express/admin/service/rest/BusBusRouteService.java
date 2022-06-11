package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.bean.BusBusRoute;

@Path("/admin/busBusRoute")
public class BusBusRouteService extends EntityService<BusBusRoute> {

	public BusBusRouteService() {
		super(BusBusRoute.class);
	}
}
