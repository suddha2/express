package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.reservation.BookingItemCharge;

@Path("/admin/bookingItemCharge")
public class BookingItemChargeService extends EntityService<BookingItemCharge> {

	public BookingItemChargeService() {
		super(BookingItemCharge.class);
	}
}
