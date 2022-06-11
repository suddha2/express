package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.bean.BusRouteLight;

@Path("/admin/busRouteLight")
public class BusRouteLightService extends LightEntityService<BusRouteLight> {

	public BusRouteLightService() {
		super(BusRouteLight.class);
	}
}
