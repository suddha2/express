package lk.express.cnx;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "ItemCancellationCriteria", namespace = "http://cnx.express.lk")
public class ItemCancellationCriteria {

	private int bookingItemId;

	public int getBookingItemId() {
		return bookingItemId;
	}

	public void setBookingItemId(int itemId) {
		this.bookingItemId = itemId;
	}
}
