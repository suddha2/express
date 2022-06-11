package lk.express;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import lk.express.reservation.Booking;

@XmlRootElement
@XmlType(name = "ConfirmResponse", namespace = "http://express.lk")
@XmlSeeAlso({ Booking.class })
public class ConfirmResponse extends ExpResponse<Booking> {

	private PaymentRefundsResponse paymentResponse;

	public ConfirmResponse() {
	}

	public ConfirmResponse(int status) {
		super(status);
	}

	public ConfirmResponse(Booking booking) {
		super(booking);
	}

	public ConfirmResponse(String errorMessage) {
		super(errorMessage);
	}

	@XmlElement(nillable = true)
	public PaymentRefundsResponse getPaymentResponse() {
		return paymentResponse;
	}

	public void setPaymentResponse(PaymentRefundsResponse paymentResponse) {
		this.paymentResponse = paymentResponse;
	}
}
