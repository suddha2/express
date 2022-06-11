package lk.express.search.api.v2;

import lk.express.PaymentCriteria;
import lk.express.bean.PaymentRefund.PaymentRefundType;

/**
 * Add payment criteria
 *
 * @version v2
 */
public class APIPaymentCriteria extends APIPaymentRefundCriteria {

	/**
	 * @exclude
	 */
	public PaymentCriteria getCriteria() {
		PaymentCriteria criteria = new PaymentCriteria();
		criteria.setType(PaymentRefundType.Payment);
		fillCriteria(criteria);
		return criteria;
	}

}
