package lk.express.reservation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Audited
@Entity
@Table(name = "booking_item_charge")
@XmlType(name = "BookingItemCharge", namespace = "http://bean.express.lk")
@XmlRootElement
public class BookingItemCharge extends lk.express.bean.Entity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "booking_item_id")
	@XmlTransient
	@JsonIgnore
	private BookingItem bookingItem;
	@Column(name = "charge_scheme_id")
	private int chargeSchemeId;
	@Column(name = "amount")
	private double amount;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@XmlTransient
	@JsonIgnore
	public BookingItem getBookingItem() {
		return bookingItem;
	}

	public void setBookingItem(BookingItem bookingItem) {
		this.bookingItem = bookingItem;
	}

	public int getChargeSchemeId() {
		return chargeSchemeId;
	}

	public void setChargeSchemeId(int chargeSchemeId) {
		this.chargeSchemeId = chargeSchemeId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
}
