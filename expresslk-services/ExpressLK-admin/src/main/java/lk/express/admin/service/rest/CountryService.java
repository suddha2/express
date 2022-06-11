package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.bean.Country;

@Path("/admin/country")
public class CountryService extends HasCodeEntityService<Country> {

	public CountryService() {
		super(Country.class);
	}
}
