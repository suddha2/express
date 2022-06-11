package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.reservation.Passenger;

@Path("/admin/passenger")
public class PassengerService extends EntityService<Passenger> {

	public PassengerService() {
		super(Passenger.class);
	}
}
