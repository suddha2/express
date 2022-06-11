package lk.express.cnx;

import java.util.Date;

import lk.express.RuleProperty;
import lk.express.reservation.BookingItem;

public class BookingItemCancellationWrapper extends BookingCancellationWrapper {

	public static final String FARE = "fare";
	public static final String PRICE = "price";

	private BookingItem bookingItem;

	public BookingItemCancellationWrapper(CancellationCriteria criteria, BookingItem bookingItem) {
		super(criteria, bookingItem.getBooking());
		this.bookingItem = bookingItem;
	}

	@Override
	protected Date getDepartureTime() {
		return bookingItem.getDepartureTime();
	}

	@RuleProperty(FARE)
	public double getFare() {
		return bookingItem.getFare();
	}

	@RuleProperty(PRICE)
	public double getPrice() {
		return bookingItem.getPrice();
	}

	@Override
	public void applyCharge(int ruleId, double amount) {
		BookingItemCancellation charge = new BookingItemCancellation();
		charge.setBookingId(booking.getId());
		charge.setBookingItemId(bookingItem.getId());
		charge.setCancellationSchemeId(ruleId);
		charge.setAmount(amount);
		charges.add(charge);
	}
}
