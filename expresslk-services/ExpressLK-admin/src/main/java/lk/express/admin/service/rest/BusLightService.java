package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.bean.BusLight;

@Path("/admin/busLight")
public class BusLightService extends LightEntityService<BusLight> {

	public BusLightService() {
		super(BusLight.class);
	}
}
