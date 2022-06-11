package lk.express.api.v2;

import java.util.Date;

public class APIChange extends APIEntity {

	public Integer bookingItemId;
	public Integer bookingItemPassengerId;
	public String type;
	public String description;
	public Integer userId;
	public Date changeTime;

	/**
	 * @exclude
	 */
	public APIChange() {

	}

	/**
	 * @exclude
	 */
	public APIChange(lk.express.reservation.Change e) {
		super(e);
		bookingItemId = e.getBookingItemId();
		bookingItemPassengerId = e.getBookingItemPassengerId();
		type = e.getType().getCode();
		description = e.getDescription();
		userId = e.getUserId();
		changeTime = e.getChangeTime();
	}
}
