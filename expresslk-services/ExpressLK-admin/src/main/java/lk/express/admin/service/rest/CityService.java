package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.bean.City;

@Path("/admin/city")
public class CityService extends HasCodeEntityService<City> {

	public CityService() {
		super(City.class);
	}
}
