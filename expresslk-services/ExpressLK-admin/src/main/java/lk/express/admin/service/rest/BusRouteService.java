package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.bean.BusRoute;

@Path("/admin/busRoute")
public class BusRouteService extends EntityService<BusRoute> {

	public BusRouteService() {
		super(BusRoute.class);
	}
}
