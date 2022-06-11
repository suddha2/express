package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.reservation.BookingStatus;

@Path("/admin/bookingStatus")
public class BookingStatusService extends HasCodeEntityService<BookingStatus> {

	public BookingStatusService() {
		super(BookingStatus.class);
	}
}
