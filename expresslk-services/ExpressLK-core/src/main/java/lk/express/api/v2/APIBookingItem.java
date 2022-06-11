package lk.express.api.v2;

import java.util.ArrayList;
import java.util.List;

public class APIBookingItem extends APIEntity {

	public int index;
	public double fare;
	public double price;
	public String remarks;
	public String status;

	public APIBusSchedule schedule;
	public APIBusStop fromBusStop;
	public APIBusStop toBusStop;

	public List<APIBookingItemPassenger> passengers = new ArrayList<>();
	public List<APIBookingItemCancellation> cancellationCharges = new ArrayList<>();
	public List<APIChange> changes = new ArrayList<>();

	/**
	 * @exclude
	 */
	public APIBookingItem() {

	}

	/**
	 * @exclude
	 */
	public APIBookingItem(lk.express.reservation.BookingItem e) {
		super(e);
		index = e.getIndex();
		fare = e.getFare();
		price = e.getPrice();
		remarks = e.getRemarks();
		status = e.getStatus().getCode();

		schedule = new APIBusSchedule(e.getSchedule());
		fromBusStop = new APIBusStop(e.getFromBusStop());
		toBusStop = new APIBusStop(e.getToBusStop());

		for (lk.express.reservation.BookingItemPassenger p : e.getPassengers()) {
			passengers.add(new APIBookingItemPassenger(p));
		}
		for (lk.express.cnx.BookingItemCancellation c : e.getCancellations()) {
			cancellationCharges.add(new APIBookingItemCancellation(c));
		}
		for (lk.express.reservation.Change c : e.getChanges()) {
			changes.add(new APIChange(c));
		}
	}
}
