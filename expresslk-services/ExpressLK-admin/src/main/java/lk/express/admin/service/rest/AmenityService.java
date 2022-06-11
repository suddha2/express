package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.bean.Amenity;

@Path("/admin/amenity")
public class AmenityService extends HasCodeEntityService<Amenity> {

	public AmenityService() {
		super(Amenity.class);
	}
}
