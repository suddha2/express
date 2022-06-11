package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.reservation.BookingItemTax;

@Path("/admin/bookingItemTax")
public class BookingItemTaxService extends EntityService<BookingItemTax> {

	public BookingItemTaxService() {
		super(BookingItemTax.class);
	}
}
