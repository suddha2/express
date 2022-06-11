package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.bean.SeatingProfile;

@Path("/admin/seatingProfile")
public class SeatingProfileService extends EntityService<SeatingProfile> {

	public SeatingProfileService() {
		super(SeatingProfile.class);
	}
}
