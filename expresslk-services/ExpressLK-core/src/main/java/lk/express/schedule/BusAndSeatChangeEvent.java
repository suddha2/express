package lk.express.schedule;

import java.util.List;

import lk.express.reservation.BookingItem;

public class BusAndSeatChangeEvent extends ScheduleChangeEvent {

	private List<String> seatNumbers;
	private BookingItem bookingItem;

	public BusAndSeatChangeEvent() {
		super();
	}

	public BusAndSeatChangeEvent(BusSchedule schedule) {
		super(schedule);
	}

	public List<String> getSeatNumbers() {
		return seatNumbers;
	}

	public void setSeatNumbers(List<String> seatNumbers) {
		this.seatNumbers = seatNumbers;
	}

	public BookingItem getBookingItem() {
		return bookingItem;
	}

	public void setBookingItem(BookingItem bookingItem) {
		this.bookingItem = bookingItem;
	}
}
