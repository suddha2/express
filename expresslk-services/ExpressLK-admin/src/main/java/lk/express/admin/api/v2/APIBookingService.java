package lk.express.admin.api.v2;

import javax.ws.rs.Path;

import lk.express.admin.service.rest.BookingService;
import lk.express.api.v2.APIBooking;
import lk.express.reservation.Booking;

@Path("/admin/booking")
public class APIBookingService extends APIEntityService<APIBooking, Booking, BookingService> {

	public APIBookingService() {
		super(APIBooking.class, Booking.class, new BookingService());
	}
}
