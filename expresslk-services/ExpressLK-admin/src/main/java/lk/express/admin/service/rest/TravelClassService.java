package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.bean.TravelClass;

@Path("/admin/travelClass")
public class TravelClassService extends EntityService<TravelClass> {

	public TravelClassService() {
		super(TravelClass.class);
	}
}
