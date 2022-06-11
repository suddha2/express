package lk.express.reservation;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lk.express.bean.PassengerType;
import lk.express.bean.VendorPaymentRefundMode;

@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity
@Table(name = "booking_item_passenger")
@XmlType(name = "BookingItemPassenger", namespace = "http://bean.express.lk")
@XmlRootElement
public class BookingItemPassenger extends lk.express.bean.Entity {

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

	@ManyToOne()
	@JoinColumn(name = "passenger_id")
	@XmlTransient
	@JsonIgnore
	private Passenger passenger;

	@Column(name = "passenger_id", insertable = false, updatable = false)
	private Integer passengerId;

	@Column(name = "seat_number")
	private String seatNumber;

	@ManyToOne
	@JoinColumn(name = "status_code", referencedColumnName = "code")
	private BookingStatus status;

	@Column(name = "journey_performed", columnDefinition = "bit")
	private Boolean journeyPerformed = false;

	@Column(name = "passenger_type")
	@Enumerated(EnumType.STRING)
	private PassengerType passengerType;

	@Column(name = "seat_fare")
	private double seatFare=0d;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "bookingItemPassengerId", cascade = CascadeType.ALL)
	private List<Change> changes;

	@Column(name = "gross_price")
	private Double grossPrice=0d;

	@Column(name = "price_before_tax")
	private Double priceBeforeTax=0d;

	@Column(name = "price_before_charge")
	private Double priceBeforeCharge=0d;

	@Column(name = "price")
	private Double price=0d;

	@Column(name = "fare_payment_option")
	@Enumerated(EnumType.STRING)
	private VendorPaymentRefundMode farePaymentOption;

	public VendorPaymentRefundMode getFarePaymentOption() {
		return farePaymentOption;
	}

	public void setFarePaymentOption(VendorPaymentRefundMode farePaymentOption) {
		this.farePaymentOption = farePaymentOption;
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

	@XmlTransient
	@JsonIgnore
	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	public Integer getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(Integer passengerId) {
		this.passengerId = passengerId;
	}

	public String getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
	}

	public Boolean isJourneyPerformed() {
		return journeyPerformed;
	}

	public void setJourneyPerformed(Boolean journeyPerformed) {
		this.journeyPerformed = journeyPerformed;
	}

	public List<Change> getChanges() {
		if (changes == null) {
			changes = new ArrayList<>();
		}
		return changes;
	}

	public void setChanges(List<Change> changes) {
		this.changes = changes;
	}

	public PassengerType getPassengerType() {
		return passengerType;
	}

	public void setPassengerType(PassengerType passengerType) {
		this.passengerType = passengerType;
	}

	public double getSeatFare() {
		return seatFare;
	}

	public void setSeatFare(double seatFare) {
		this.seatFare = seatFare;
	}

	public Double getGrossPrice() {
		return grossPrice;
	}

	public void setGrossPrice(Double grossPrice) {
		this.grossPrice = grossPrice;
	}

	public Double getPriceBeforeTax() {
		return priceBeforeTax;
	}

	public void setPriceBeforeTax(Double priceBeforeTax) {
		this.priceBeforeTax = priceBeforeTax;
	}

	public Double getPriceBeforeCharge() {
		return priceBeforeCharge;
	}

	public void setPriceBeforeCharge(Double priceBeforeCharge) {
		this.priceBeforeCharge = priceBeforeCharge;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

}
