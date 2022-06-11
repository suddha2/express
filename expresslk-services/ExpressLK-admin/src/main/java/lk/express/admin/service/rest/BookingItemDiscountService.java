package lk.express.admin.service.rest;

import javax.ws.rs.Path;

import lk.express.reservation.BookingItemDiscount;

@Path("/admin/bookingItemDiscount")
public class BookingItemDiscountService extends EntityService<BookingItemDiscount> {

	public BookingItemDiscountService() {
		super(BookingItemDiscount.class);
	}
}
