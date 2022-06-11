package lk.express.reservation;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

/**
 * Bean class to record changes to the booking, booking item and booking item
 * passenger.
 * 
 * I thought of using a single class to record changes at booking, booking item
 * and booking item passenger level as it will simplify the design.
 * 
 * When a change to the booking is recorded <code>bookingItemId</code> and
 * <code>bookingItemPassengerId</code> will be <code>null</code>. Similarly,
 * when a change to the booking item is recorded
 * <code>bookingItemPassengerId</code> will be <code>null</code>.
 */
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity
@Table(name = "\"change\"")
@XmlType(name = "Change", namespace = "http://bean.express.lk")
@XmlRootElement
public class Change extends lk.express.bean.Entity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "booking_id")
	private Integer bookingId;
	// null for booking level changes
	@Column(name = "booking_item_id")
	private Integer bookingItemId;
	// null for booking or item level changes
	@Column(name = "booking_item_passenger_id")
	private Integer bookingItemPassengerId;
	@ManyToOne
	@JoinColumn(name = "change_type_id")
	private ChangeType type;
	@Column(name = "description", columnDefinition = "text")
	private String description;
	@Column(name = "user_id")
	private Integer userId;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "change_time", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date changeTime;

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

	public void setBookingItemId(Integer itemId) {
		this.bookingItemId = itemId;
	}

	public Integer getBookingItemPassengerId() {
		return bookingItemPassengerId;
	}

	public void setBookingItemPassengerId(Integer bookingItemPassengerId) {
		this.bookingItemPassengerId = bookingItemPassengerId;
	}

	public ChangeType getType() {
		return type;
	}

	public void setType(ChangeType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Date getChangeTime() {
		return changeTime;
	}

	public void setChangeTime(Date changeTime) {
		this.changeTime = changeTime;
	}
}
