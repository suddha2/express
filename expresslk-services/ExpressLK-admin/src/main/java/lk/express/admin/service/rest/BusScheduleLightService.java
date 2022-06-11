package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.schedule.BusScheduleLight;

@Path("/admin/busScheduleLight")
public class BusScheduleLightService extends HasDepartureArrivalService<BusScheduleLight> {

	public BusScheduleLightService() {
		super(BusScheduleLight.class);
	}
}
