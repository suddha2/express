package lk.express.reservation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.Gender;
import lk.express.bean.PassengerType;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity
@Table(name = "passenger")
@XmlType(name = "Passenger", namespace = "http://bean.express.lk")
@XmlRootElement
public class Passenger extends lk.express.bean.Entity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "booking_id")
	@XmlTransient
	@JsonIgnore
	private Booking booking;
	@Column(name = "booking_id", insertable = false, updatable = false)
	private Integer bookingId;
	@Column(name = "idx")
	private int index;
	@Column(name = "passenger_type")
	@Enumerated(EnumType.STRING)
	private PassengerType passengerType;

	@Column(name = "name")
	private String name;
	@Column(name = "nic")
	private String nic;
	@Column(name = "gender")
	@Enumerated(EnumType.STRING)
	private Gender gender;
	@Column(name = "email")
	private String email;
	@Column(name = "mobile_telephone")
	private String mobileTelephone;

	@Override
	public void updateFromOld(lk.express.bean.Entity old) {
		Passenger oldItem = (Passenger) old;
		booking = oldItem.booking;
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@XmlTransient
	@JsonIgnore
	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
		if (booking != null) {
			bookingId = booking.getId();
		}
	}

	public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public PassengerType getPassengerType() {
		return passengerType;
	}

	public void setPassengerType(PassengerType passengerType) {
		this.passengerType = passengerType;
	}

	public boolean isInfant() {
		return passengerType.equals(PassengerType.Infant);
	}

	public boolean isChild() {
		return passengerType.equals(PassengerType.Child);
	}

	public boolean isAdult() {
		return passengerType.equals(PassengerType.Adult);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNic() {
		return nic;
	}

	public void setNic(String nic) {
		this.nic = nic;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmails(String email) {
		this.email = email;
	}

	public String getMobileTelephone() {
		return mobileTelephone;
	}

	public void setMobileTelephone(String mobile) {
		this.mobileTelephone = mobile;
	}
}
