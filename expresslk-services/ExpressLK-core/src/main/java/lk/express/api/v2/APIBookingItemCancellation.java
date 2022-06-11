package lk.express.api.v2;

public class APIBookingItemCancellation extends APIEntity {

	public int cancellationSchemeId;
	public double amount;

	/**
	 * @exclude
	 */
	public APIBookingItemCancellation() {

	}

	/**
	 * @exclude
	 */
	public APIBookingItemCancellation(lk.express.cnx.BookingItemCancellation e) {
		super(e);
		cancellationSchemeId = e.getCancellationSchemeId();
		amount = e.getAmount();
	}
}
