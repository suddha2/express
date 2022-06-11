package lk.express;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "PaymentMethodCriteria", namespace = "http://express.lk")
public class PaymentMethodCriteria {

	@XmlType(name = "PaymentMethod", namespace = "http://express.lk")
	public enum PaymentMethod {
		Deferred, CashAtBus
	}

	private PaymentMethod paymentMethod;
	private Date holdUntil;

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public Date getHoldUntil() {
		return holdUntil;
	}

	public void setHoldUntil(Date holdUntil) {
		this.holdUntil = holdUntil;
	}
}
