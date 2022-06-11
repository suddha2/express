package lk.express.reservation;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lk.express.HasDepartureArrival;
import lk.express.admin.Agent;
import lk.express.admin.Company;
import lk.express.admin.Division;
import lk.express.admin.User;
import lk.express.bean.BelongsToDivision;
import lk.express.bean.Client;
import lk.express.bean.HasAllowedDivisions;
import lk.express.bean.Payment;
import lk.express.bean.Refund;
import lk.express.cnx.BookingItemCancellation;
import lk.express.cnx.CancellationCause;
import lk.express.util.CurrencyUtil;

@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity
@Table(name = "booking")
@XmlType(name = "Booking", namespace = "http://bean.express.lk")
@XmlRootElement
public class Booking extends lk.express.bean.Entity
		implements HasDepartureArrival, HasAllowedDivisions, BelongsToDivision {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;

	@Column(name = "reference")
	private String reference;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "booking_time", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date bookingTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "expiry_time")
	private Date expiryTime;

	@Column(name = "chargeable")
	private double chargeable;

	@ManyToOne
	@JoinColumn(name = "status_code", referencedColumnName = "code")
	private BookingStatus status;

	@Column(name = "cancellation_cause")
	@Enumerated(EnumType.STRING)
	private CancellationCause cancellationCause;

	@ManyToOne
	@JoinColumn(name = "client_id")
	private Client client;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "agent_id")
	private Agent agent;

	@Column(name = "remarks", columnDefinition = "text")
	private String remarks;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
	private List<BookingItem> bookingItems;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
	private List<Passenger> passengers;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "bookingId", cascade = CascadeType.ALL)
	private List<BookingItemCancellation> cancellations;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "bookingId", cascade = CascadeType.ALL)
	private List<Payment> payments;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "bookingId", cascade = CascadeType.ALL)
	private List<Refund> refunds;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "bookingId", cascade = CascadeType.ALL)
	private List<Change> changes;

	@ManyToOne
	@JoinColumn(name = "division_id")
	private Division division;

	@Column(name = "allowed_divisions")
	private BigInteger allowedDivisions;

	@Column(name = "write_allowed_divisions")
	private BigInteger writeAllowedDivisions;

	// Verification Code added for manual ticket validation by conductor.
	@Column(name = "verification_code")
	private String verficationCode;

	/**
	 * Flag to check if booking payment is Cash/Warrant/Pass/PayAtBus/Card.
	 * Warrant/Pass/PayAtBus will have Cash + Warrant/Pass/PayAtBus Records in
	 * table. So when the mode is any of Warrant/Pass/PayAtBus we set this as
	 * payment option for the booking
	 */
	@Transient
	private String bookingPayOption;

	public String getBookingPayOption() {
		return bookingPayOption;
	}

	public void setBookingPayOption(String bookingPayOption) {
		this.bookingPayOption = bookingPayOption;
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<BookingItem> getBookingItems() {
		if (bookingItems == null) {
			bookingItems = new ArrayList<>();
		}
		return bookingItems;
	}

	public void setBookingItems(List<BookingItem> bookingItems) {
		for (BookingItem bookingItem : bookingItems) {
			addBookingItem(bookingItem);
		}
	}

	public void addBookingItem(BookingItem bookingItem) {
		getBookingItems();
		bookingItem.setBooking(this);
		bookingItems.add(bookingItem);
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<Passenger> getPassengers() {
		if (passengers == null) {
			passengers = new ArrayList<>();
		}
		return passengers;
	}

	public Passenger getPassengerByIndex(int index) {
		for (Passenger p : getPassengers()) {
			if (p.getIndex() == index) {
				return p;
			}
		}
		return null;
	}

	public void setPassengers(List<Passenger> passengers) {
		for (Passenger passenger : passengers) {
			addPassenger(passenger);
		}
	}

	public void addPassenger(Passenger passenger) {
		getPassengers();
		passenger.setBooking(this);
		passengers.add(passenger);
	}

	public Date getBookingTime() {
		return bookingTime;
	}

	public void setBookingTime(Date bookingTime) {
		this.bookingTime = bookingTime;
	}

	public Date getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(Date expiryTime) {
		this.expiryTime = expiryTime;
	}

	public double getChargeable() {
		return chargeable;
	}

	public void setChargeable(double chargeable) {
		this.chargeable = chargeable;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
	}

	public CancellationCause getCancellationCause() {
		return cancellationCause;
	}

	public void setCancellationCause(CancellationCause cancellationCause) {
		this.cancellationCause = cancellationCause;
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

	public List<BookingItemCancellation> getCancellations() {
		if (cancellations == null) {
			cancellations = new ArrayList<>();
		}
		return cancellations;
	}

	public void setCancellations(List<BookingItemCancellation> cancellations) {
		this.cancellations = cancellations;
	}

	public List<Payment> getPayments() {
		if (payments == null) {
			payments = new ArrayList<>();
		}
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}

	public List<Refund> getRefunds() {
		if (refunds == null) {
			refunds = new ArrayList<>();
		}
		return refunds;
	}

	public void setRefunds(List<Refund> refunds) {
		this.refunds = refunds;
	}

	@Override
	public Division getDivision() {
		return division;
	}

	@Override
	public void setDivision(Division division) {
		this.division = division;
	}

	@Override
	public BigInteger getAllowedDivisions() {
		return allowedDivisions;
	}

	@Override
	public void setAllowedDivisions(BigInteger allowedDivisions) {
		this.allowedDivisions = allowedDivisions;
	}

	@Override
	public BigInteger getWriteAllowedDivisions() {
		return writeAllowedDivisions;
	}

	@Override
	public void setWriteAllowedDivisions(BigInteger writeAllowedDivisions) {
		this.writeAllowedDivisions = writeAllowedDivisions;
	}

	@JsonIgnore
	public double getTotalPayment() {
		double tot = 0d;
		for (Payment p : getPayments()) {
			tot += p.getAmount();
		}
		return tot;
	}

	@Override
	@JsonIgnore
	public Date getDepartureTime() {
		Date earliest = null;
		for (BookingItem item : bookingItems) {
			if (item.isActive()) {
				Date dep = item.getDepartureTime();
				if (earliest == null || dep.before(earliest)) {
					earliest = dep;
				}
			}
		}
		return earliest;
	}

	@Override
	@JsonIgnore
	public Date getArrivalTime() {
		Date latest = null;
		for (BookingItem item : bookingItems) {
			if (item.isActive()) {
				Date arr = item.getArrivalTime();
				if (latest == null || arr.after(latest)) {
					latest = arr;
				}
			}
		}
		return latest;
	}

	@JsonIgnore
	public boolean isActive() {
		return !status.getCode().equals(BookingStatus.CANCELLED);
	}

	@JsonIgnore
	public boolean isAccountable() {
		return Company.BBK.equals(this.division.getCompany().getCode()) || this.user.getAccountable();
	}

	@JsonIgnore
	public boolean isReservationOnly() { // AKA Pay-at-bus
		return CurrencyUtil.isZero(chargeable);
	}

	public String getVerficationCode() {
		return verficationCode;
	}

	public void setVerficationCode(String verficationCode) {
		this.verficationCode = verficationCode;
	}
}
