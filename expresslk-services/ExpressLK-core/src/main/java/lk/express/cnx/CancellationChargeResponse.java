package lk.express.cnx;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import lk.express.ExpResponse;
import lk.express.cnx.CancellationChargeResponse.BookingItemCancellationData;

@XmlRootElement
@XmlType(name = "CancellationChargeResponse", namespace = "http://cnx.express.lk")
@XmlSeeAlso(BookingItemCancellationData.class)
public class CancellationChargeResponse extends ExpResponse<BookingItemCancellationData> {

	public CancellationChargeResponse() {

	}

	public CancellationChargeResponse(String errorMessage) {
		super(errorMessage);
	}

	public CancellationChargeResponse(BookingItemCancellationData data) {
		super(data);
	}

	public static class BookingItemCancellationData {

		public BookingItemCancellationData() {

		}

		public BookingItemCancellationData(List<BookingItemCancellation> bookingItemCancellation) {
			this.bookingItemCancellation = bookingItemCancellation;
		}

		private List<BookingItemCancellation> bookingItemCancellation;

		public List<BookingItemCancellation> getBookingItemCancellation() {
			return bookingItemCancellation;
		}

		public void setBookingItemCancellation(List<BookingItemCancellation> bookingItemCancellation) {
			this.bookingItemCancellation = bookingItemCancellation;
		}
	}
}
