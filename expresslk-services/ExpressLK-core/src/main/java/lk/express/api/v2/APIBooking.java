package lk.express.api.v2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class APIBooking extends APIEntity {

	public String reference;
	public Date bookingTime;
	public Date expiryTime;
	public double chargeable;
	public String status;
	public String remarks;

	public APIClient client;
	public APIUser user;
	public APIAgent agent;

	public List<APIBookingItem> bookingItems = new ArrayList<>();
	public List<APIPassenger> passengers = new ArrayList<>();
	public List<APIBookingItemCancellation> cancellationCharges = new ArrayList<>();
	public List<APIPayment> payments = new ArrayList<>();
	public List<APIRefund> refunds = new ArrayList<>();
	public List<APIChange> changes = new ArrayList<>();

	/**
	 * @exclude
	 */
	public APIBooking() {

	}

	/**
	 * @exclude
	 */
	public APIBooking(lk.express.reservation.Booking e) {
		super(e);
		reference = e.getReference();
		bookingTime = e.getBookingTime();
		expiryTime = e.getExpiryTime();
		chargeable = e.getChargeable();
		status = e.getStatus().getCode();
		remarks = e.getRemarks();

		client = new APIClient(e.getClient());
		user = new APIUser(e.getUser());
		if (e.getAgent() != null) {
			agent = new APIAgent(e.getAgent());
		}

		for (lk.express.reservation.BookingItem b : e.getBookingItems()) {
			bookingItems.add(new APIBookingItem(b));
		}
		for (lk.express.reservation.Passenger p : e.getPassengers()) {
			passengers.add(new APIPassenger(p));
		}
		for (lk.express.cnx.BookingItemCancellation c : e.getCancellations()) {
			cancellationCharges.add(new APIBookingItemCancellation(c));
		}
		for (lk.express.bean.Payment p : e.getPayments()) {
			payments.add(new APIPayment(p));
		}
		for (lk.express.bean.Refund r : e.getRefunds()) {
			refunds.add(new APIRefund(r));
		}
		for (lk.express.reservation.Change c : e.getChanges()) {
			changes.add(new APIChange(c));
		}
	}
}
