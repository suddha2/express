package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.admin.Staff;

@Path("/admin/staff")
public class StaffService extends EntityService<Staff> {

	public StaffService() {
		super(Staff.class);
	}
}
