package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.reservation.BookingItemMarkup;

@Path("/admin/bookingItemMarkup")
public class BookingItemMarkupService extends EntityService<BookingItemMarkup> {

	public BookingItemMarkupService() {
		super(BookingItemMarkup.class);
	}
}
