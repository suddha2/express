package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.bean.District;

@Path("/admin/district")
public class DistrictService extends HasCodeEntityService<District> {

	public DistrictService() {
		super(District.class);
	}
}
