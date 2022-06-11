package lk.express.api.v2;

import java.util.ArrayList;
import java.util.List;

public class APIBookingItemPassenger extends APIEntity {

	public Integer passengerId;
	public String seatNumber;
	public String status;
	public boolean journeyPerformed;

	public List<APIChange> changes = new ArrayList<>();

	/**
	 * @exclude
	 */
	public APIBookingItemPassenger() {

	}

	/**
	 * @exclude
	 */
	public APIBookingItemPassenger(lk.express.reservation.BookingItemPassenger e) {
		super(e);
		passengerId = e.getPassengerId();
		seatNumber = e.getSeatNumber();
		status = e.getStatus().getCode();
		journeyPerformed = e.isJourneyPerformed();
		for (lk.express.reservation.Change c : e.getChanges()) {
			changes.add(new APIChange(c));
		}
	}
}
