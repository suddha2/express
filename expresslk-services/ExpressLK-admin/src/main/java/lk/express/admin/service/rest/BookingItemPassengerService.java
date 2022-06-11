package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.reservation.BookingItemPassenger;

@Path("/admin/bookingItemPassenger")
public class BookingItemPassengerService extends EntityService<BookingItemPassenger> {

	public BookingItemPassengerService() {
		super(BookingItemPassenger.class);
	}

}
