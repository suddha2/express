package lk.express.cnx;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.ExpResponse;
import lk.express.reservation.Booking;

@XmlRootElement
@XmlType(name = "CancellationResponse", namespace = "http://cnx.express.lk")
public class CancellationResponse extends ExpResponse<Booking> {

	public CancellationResponse() {
		super();
	}

	public CancellationResponse(Booking booking) {
		super(booking);
	}

	public CancellationResponse(int status) {
		super(status);
	}

	public CancellationResponse(String errorMessage) {
		super(errorMessage);
	}
}
