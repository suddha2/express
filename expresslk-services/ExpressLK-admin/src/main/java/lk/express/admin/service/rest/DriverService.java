package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.supplier.Driver;

@Path("/admin/driver")
public class DriverService extends DriverConductorService<Driver> {

	public DriverService() {
		super(Driver.class);
	}
}
