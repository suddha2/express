package lk.express.reservation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.Criteria;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.criterion.Restrictions;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lk.express.HasDepartureArrival;
import lk.express.bean.BusStop;
import lk.express.bean.HasAllowedDivisions;
import lk.express.cnx.BookingItemCancellation;
import lk.express.cnx.CancellationCause;
import lk.express.db.HibernateUtil;
import lk.express.db.dao.DAOFactory;
import lk.express.schedule.BusSchedule;
import lk.express.schedule.BusScheduleBusStop;

@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity
@Table(name = "booking_item")
@XmlType(name = "BookingItem", namespace = "http://bean.express.lk")
@XmlRootElement
public class BookingItem extends lk.express.bean.Entity implements HasDepartureArrival, HasAllowedDivisions {

	private static final long serialVersionUID = 1L;

	protected static final DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);

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
	@Column(name = "fare")
	private double fare;
	@Column(name = "cost")
	private double cost;
	@Column(name = "gross_price")
	private double grossPrice;
	@Column(name = "price_before_tax")
	private double priceBeforeTax;
	@Column(name = "price_before_charge")
	private double priceBeforeCharge;
	@Column(name = "price")
	private double price;
	@Column(name = "remarks")
	private String remarks;
	@ManyToOne
	@JoinColumn(name = "status_code", referencedColumnName = "code")
	private BookingStatus status;
	@Column(name = "cancellation_cause")
	@Enumerated(EnumType.STRING)
	private CancellationCause cancellationCause;

	@ManyToOne
	@JoinColumn(name = "schedule_id")
	private BusSchedule schedule;
	@ManyToOne
	@JoinColumn(name = "from_bus_stop_id")
	private BusStop fromBusStop;
	@ManyToOne
	@JoinColumn(name = "to_bus_stop_id")
	private BusStop toBusStop;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "bookingItem", cascade = CascadeType.ALL)
	private List<BookingItemMarkup> markups;
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "bookingItem", cascade = CascadeType.ALL)
	private List<BookingItemDiscount> discounts;
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "bookingItem", cascade = CascadeType.ALL)
	private List<BookingItemTax> taxes;
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "bookingItem", cascade = CascadeType.ALL)
	private List<BookingItemCharge> charges;
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "bookingItem", cascade = CascadeType.ALL)
	private List<BookingItemPassenger> passengers;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "bookingItemId", cascade = CascadeType.ALL)
	private List<BookingItemCancellation> cancellations;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "bookingItemId", cascade = CascadeType.ALL)
	private List<Change> changes;

	@Column(name = "allowed_divisions")
	private BigInteger allowedDivisions;
	@Column(name = "write_allowed_divisions")
	private BigInteger writeAllowedDivisions;

	@Override
	public void updateFromOld(lk.express.bean.Entity old) {
		BookingItem oldItem = (BookingItem) old;
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

	public double getFare() {
		return fare;
	}

	public void setFare(double fare) {
		this.fare = fare;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = this.round(cost);
	}

	public double getGrossPrice() {
		return grossPrice;
	}

	public void setGrossPrice(double grossPrice) {
		this.grossPrice = this.round(grossPrice);
	}

	public double getPriceBeforeTax() {
		return priceBeforeTax;
	}

	public void setPriceBeforeTax(double priceBeforeTax) {
		this.priceBeforeTax = this.round(priceBeforeTax);
	}

	public double getPriceBeforeCharge() {
		return priceBeforeCharge;
	}

	public void setPriceBeforeCharge(double priceBeforeCharge) {
		this.priceBeforeCharge = this.round(priceBeforeCharge);
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {

		this.price = this.round(price);
	}

	public List<BookingItemMarkup> getMarkups() {
		return markups;
	}

	public void setMarkups(List<BookingItemMarkup> markups) {
		for (BookingItemMarkup markup : markups) {
			addMarkup(markup);
		}
	}

	public void addMarkup(BookingItemMarkup markup) {
		if (markups == null) {
			markups = new ArrayList<BookingItemMarkup>();
		}
		markup.setBookingItem(this);
		markups.add(markup);
	}

	public List<BookingItemDiscount> getDiscounts() {
		return discounts;
	}

	public void setDiscounts(List<BookingItemDiscount> discounts) {
		for (BookingItemDiscount discount : discounts) {
			addDiscount(discount);
		}
	}

	public void addDiscount(BookingItemDiscount discount) {
		if (discounts == null) {
			discounts = new ArrayList<BookingItemDiscount>();
		}
		discount.setBookingItem(this);
		discounts.add(discount);
	}

	public List<BookingItemTax> getTaxes() {
		return taxes;
	}

	public void setTaxes(List<BookingItemTax> taxes) {
		for (BookingItemTax tax : taxes) {
			addTax(tax);
		}
	}

	public void addTax(BookingItemTax tax) {
		if (taxes == null) {
			taxes = new ArrayList<BookingItemTax>();
		}
		tax.setBookingItem(this);
		taxes.add(tax);
	}

	public List<BookingItemCharge> getCharges() {
		return charges;
	}

	public void setCharges(List<BookingItemCharge> charges) {
		this.charges = charges;
	}

	public void addCharge(BookingItemCharge charge) {
		if (charges == null) {
			charges = new ArrayList<BookingItemCharge>();
		}
		charge.setBookingItem(this);
		charges.add(charge);
	}

	public List<BookingItemPassenger> getPassengers() {
		if (passengers == null) {
			passengers = new ArrayList<>();
		}
		return passengers;
	}

	public void setPassengers(List<BookingItemPassenger> passengers) {
		this.passengers = passengers;
	}

	public void addPassnger(BookingItemPassenger passenger) {
		if (passengers == null) {
			passengers = new ArrayList<BookingItemPassenger>();
		}
		passenger.setBookingItem(this);
		passengers.add(passenger);
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

	public boolean isActive() {
		return !status.getCode().equals(BookingStatus.CANCELLED);
	}

	public BusSchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(BusSchedule schedule) {
		this.schedule = schedule;
	}

	public BusStop getFromBusStop() {
		return fromBusStop;
	}

	public void setFromBusStop(BusStop fromBusStop) {
		this.fromBusStop = fromBusStop;
	}

	public BusStop getToBusStop() {
		return toBusStop;
	}

	public void setToBusStop(BusStop toBusStop) {
		this.toBusStop = toBusStop;
	}

	@Override
	public BigInteger getAllowedDivisions() {
		return allowedDivisions;
	}

	@Override
	public void setAllowedDivisions(BigInteger allowedDivisions) {
		this.allowedDivisions = allowedDivisions;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public BigInteger getWriteAllowedDivisions() {
		return writeAllowedDivisions;
	}

	@Override
	public void setWriteAllowedDivisions(BigInteger writeAllowedDivisions) {
		this.writeAllowedDivisions = writeAllowedDivisions;
	}

	@Override
	@JsonIgnore
	public Date getDepartureTime() {
		Date[] depStopTime = getStopTime(fromBusStop);
		return depStopTime[1];
	}

	@Override
	@JsonIgnore
	public Date getArrivalTime() {
		Date[] arrStopTime = getStopTime(toBusStop);
		return arrStopTime[0];
	}

	private Date[] getStopTime(BusStop stop) {
		Criteria criteria = HibernateUtil.getCurrentSession().createCriteria(BusScheduleBusStop.class)
				.add(Restrictions.eq("scheduleId", schedule.getId())).createAlias("stop", "stop")
				.add(Restrictions.eq("stop.id", stop.getId()));
		BusScheduleBusStop instance = (BusScheduleBusStop) criteria.uniqueResult();
		return new Date[] { instance.getArrivalTime(), instance.getDepartureTime() };
	}

	public String getCode() {
		return booking.getReference() + "." + index;
	}

	private double round(double amout) {
		return new BigDecimal(amout).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}
}
