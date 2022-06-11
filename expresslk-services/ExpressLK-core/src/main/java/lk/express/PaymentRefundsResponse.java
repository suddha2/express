package lk.express;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.Coupon;
import lk.express.bean.PaymentRefund;

@XmlRootElement
@XmlType(name = "PaymentRefundsResponse", namespace = "http://express.lk")
public class PaymentRefundsResponse extends ExpResponse<PaymentRefund> {

	public static int PAYMENT_FOR_CANCELLED_BOOKING = -100;

	// This is used to send back the coupon for refunds and well as new coupon
	// for the remaining value when payments are made with coupons
	private Coupon coupon;

	public PaymentRefundsResponse() {

	}

	public PaymentRefundsResponse(int status) {
		super(status);
	}

	public PaymentRefundsResponse(String errorMessage) {
		super(errorMessage);
	}

	public PaymentRefundsResponse(PaymentRefund data) {
		super(data);
	}

	public Coupon getCoupon() {
		return coupon;
	}

	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}
}
