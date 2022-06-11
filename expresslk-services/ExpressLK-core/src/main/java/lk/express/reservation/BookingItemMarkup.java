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

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Audited
@Entity
@Table(name = "booking_item_markup")
@XmlType(name = "BookingItemMarkup", namespace = "http://bean.express.lk")
@XmlRootElement
public class BookingItemMarkup extends lk.express.bean.Entity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "booking_item_id")
	@XmlTransient
	@JsonIgnore
	private BookingItem bookingItem;
	@Column(name = "booking_item_id", insertable = false, updatable = false)
	private Integer bookingItemId;
	@Column(name = "markup_scheme_id")
	private int markupSchemeId;
	@Column(name = "amount")
	private double amount;
	@Column(name = "is_margin", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean isMargin;

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

	public Integer getBookingItemId() {
		return bookingItemId;
	}

	public void setBookingItemId(Integer bookingItemId) {
		this.bookingItemId = bookingItemId;
	}

	public int getMarkupSchemeId() {
		return markupSchemeId;
	}

	public void setMarkupSchemeId(int markupSchemeId) {
		this.markupSchemeId = markupSchemeId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Boolean getIsMargin() {
		return isMargin;
	}

	public void setIsMargin(Boolean isMargin) {
		this.isMargin = isMargin;
	}
}
