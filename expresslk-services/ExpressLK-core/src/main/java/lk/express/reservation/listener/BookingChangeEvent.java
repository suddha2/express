package lk.express.reservation.listener;

import lk.express.reservation.Booking;

/**
 * @author dilantha
 * 
 */
public class BookingChangeEvent {

	public enum BookingChangeType {

		BOOKING_CANCEL,

		BOOKING_CONFIRM
	}

	private BookingChangeType type;
	private Booking booking;

	public BookingChangeEvent() {

	}

	public BookingChangeEvent(Booking booking, BookingChangeType type) {

		this.booking = booking;
		this.type = type;
	}

	public BookingChangeType getType() {
		return type;
	}

	public void setType(BookingChangeType type) {
		this.type = type;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}
}
