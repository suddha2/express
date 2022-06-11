package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.bean.BusStop;

@Path("/admin/busStop")
public class BusStopService extends EntityService<BusStop> {

	public BusStopService() {
		super(BusStop.class);
	}
}
