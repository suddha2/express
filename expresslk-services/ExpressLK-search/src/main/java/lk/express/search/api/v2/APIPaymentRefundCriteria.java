package lk.express.search.api.v2;

import lk.express.PaymentRefundCriteria;
import lk.express.bean.PaymentRefundMode;

/**
 * Abstract super class of payment and refund criteria
 */
public abstract class APIPaymentRefundCriteria extends APICriteria {

	/**
	 * Booking ID
	 */
	public int bookingId;
	/**
	 * Reference
	 */
	public String reference;
	/**
	 * Payment/refund amount
	 */
	public double amount;

	/**
	 * @exclude
	 */
	@Override
	public String validate() {
		if (bookingId <= 0) {
			return "Wrong [bookingId]";
		}
		if (amount <= 0d) {
			return "Wrong [amount]";
		}
		return null;
	}

	/**
	 * @exclude
	 */
	protected void fillCriteria(PaymentRefundCriteria criteria) {
		criteria.setBookingId(bookingId);
		criteria.setMode(PaymentRefundMode.Vendor);
		criteria.setReference(reference);
		criteria.setAmount(amount);
		criteria.setActualAmount(amount);
		criteria.setActualCurrency("LKR");
	}
}
