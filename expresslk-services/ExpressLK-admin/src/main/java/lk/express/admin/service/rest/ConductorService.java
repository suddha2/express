package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.supplier.Conductor;

@Path("/admin/conductor")
public class ConductorService extends DriverConductorService<Conductor> {

	public ConductorService() {
		super(Conductor.class);
	}
}
