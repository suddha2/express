package lk.express.search.api.v2;

import lk.express.reservation.Booking;

public class APIConfirmResult {

	/**
	 * ID of the booking made
	 */
	public int bookingId;
	/**
	 * Human readable reference of the booking made
	 */
	public String reference;
	/**
	 * Status code of the booking<br>
	 * Possible values: TENT - tentative, CONF - confirmed, CANC - cancelled
	 */
	public String status;

	/**
	 * @exclude
	 */
	public APIConfirmResult() {

	}

	/**
	 * @exclude
	 */
	public APIConfirmResult(Booking booking) {
		bookingId = booking.getId();
		reference = booking.getReference();
		status = booking.getStatus().getCode();
	}
}
