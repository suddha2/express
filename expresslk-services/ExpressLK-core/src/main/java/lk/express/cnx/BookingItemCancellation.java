package lk.express.cnx;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "booking_item_cancellation")
@XmlRootElement
@XmlType(name = "BookingItemCancellation", namespace = "http://cnx.express.lk")
public class BookingItemCancellation extends lk.express.bean.Entity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "booking_id")
	private Integer bookingId;
	// using Integer here, so we can have null for booking level cancellation
	// charges
	@Column(name = "booking_item_id")
	private Integer bookingItemId;
	@Column(name = "cancellation_scheme_id")
	private int cancellationSchemeId;
	@Column(name = "amount")
	private double amount;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}

	public Integer getBookingItemId() {
		return bookingItemId;
	}

	public void setBookingItemId(Integer bookingItemId) {
		this.bookingItemId = bookingItemId;
	}

	public int getCancellationSchemeId() {
		return cancellationSchemeId;
	}

	public void setCancellationSchemeId(int cancellationSchemeId) {
		this.cancellationSchemeId = cancellationSchemeId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
}
