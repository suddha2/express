package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.cnx.BookingItemCancellation;

@Path("/admin/bookingItemCancellation")
public class BookingItemCancellationService extends EntityService<BookingItemCancellation> {

	public BookingItemCancellationService() {
		super(BookingItemCancellation.class);
	}
}
