package lk.express.api.v2;

import java.util.Date;

public class APIPaymentRefund extends APIEntity {

	public double amount;
	public Date time;
	public Integer userId;
	public String reference;

	/**
	 * @exclude
	 */
	public APIPaymentRefund() {

	}

	/**
	 * @exclude
	 */
	public APIPaymentRefund(lk.express.bean.PaymentRefund e) {
		amount = e.getAmount();
		time = e.getTime();
		userId = e.getId();
		reference = e.getReference();
	}
}
