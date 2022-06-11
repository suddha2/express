package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.bean.BusSector;

@Path("/admin/busSector")
public class BusSectorService extends EntityService<BusSector> {

	public BusSectorService() {
		super(BusSector.class);
	}
}