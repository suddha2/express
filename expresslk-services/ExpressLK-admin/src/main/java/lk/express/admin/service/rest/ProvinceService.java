package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.bean.Province;

@Path("/admin/province")
public class ProvinceService extends HasCodeEntityService<Province> {

	public ProvinceService() {
		super(Province.class);
	}
}
