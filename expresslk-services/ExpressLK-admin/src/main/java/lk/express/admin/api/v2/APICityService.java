package lk.express.admin.api.v2;

import javax.ws.rs.Path;

import lk.express.admin.service.rest.CityService;
import lk.express.api.v2.APICity;
import lk.express.bean.City;

@Path("/admin/city")
public class APICityService extends APIEntityService<APICity, City, CityService> {

	public APICityService() {
		super(APICity.class, City.class, new CityService());
	}

	@Override
	protected boolean allowReadAll() {
		return true;
	}
}
