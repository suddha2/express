package lk.express.schedule;

import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.HasDepartureArrival;
import lk.express.bean.Bus;
import lk.express.bean.BusRoute;
import lk.express.bean.HasAllowedDivisions;
import lk.express.bean.IllegalPropertyValueException;
import lk.express.bean.OperationalStage;
import lk.express.bean.SeatingProfile;
import lk.express.supplier.Conductor;
import lk.express.supplier.Driver;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "bus_schedule")
@XmlType(name = "BusSchedule", namespace = "http://schedule.express.lk")
@XmlRootElement
public class BusSchedule extends lk.express.bean.Entity implements HasDepartureArrival, HasAllowedDivisions {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "bus_id")
	private Bus bus;
	@ManyToOne
	@JoinColumn(name = "bus_route_id")
	private BusRoute busRoute;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "terminal_in_time")
	private Date terminalInTime;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "departure_time")
	private Date departureTime;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "arrival_time")
	private Date arrivalTime;
	@ManyToOne
	// Default state set by the DB
	@JoinColumn(name = "stage_id", nullable = false, insertable = false)
	private OperationalStage stage;
	@Column(name = "active", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean active = true;
	@Column(name = "booking_allowed", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean bookingAllowed = true;

	@ManyToOne
	@JoinColumn(name = "driver_id")
	private Driver driver;
	@ManyToOne
	@JoinColumn(name = "conductor_id")
	private Conductor conductor;

	@Column(name = "load_factor")
	private Float loadFactor;
	@ManyToOne
	@JoinColumn(name = "seating_profile_id")
	private SeatingProfile seatingProfile;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "tb_booking_end_time")
	private Date tbBookingEndTime;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "web_booking_end_time")
	private Date webBookingEndTime;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ticketing_active_time")
	private Date ticketingActiveTime;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "scheduleId", cascade = CascadeType.ALL)
	@OrderBy("idx ASC")
	private Set<BusScheduleBusStop> scheduleStops;

	@Column(name = "allowed_divisions")
	private BigInteger allowedDivisions;

	@Override
	public void validate() throws IllegalPropertyValueException {
		super.validate();
		if (bus.getPermitExpiryDate() != null && departureTime.after(bus.getPermitExpiryDate())) {
			throw new IllegalPropertyValueException("Route permit expires before departure date!");
		}

		if (driver != null && driver.getDrivingLicenceExpiryDate() != null
				&& departureTime.after(driver.getDrivingLicenceExpiryDate())) {
			throw new IllegalPropertyValueException("Driver licence expires before departure date!");
		}
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Bus getBus() {
		return bus;
	}

	public void setBus(Bus bus) {
		this.bus = bus;
	}

	public BusRoute getBusRoute() {
		return busRoute;
	}

	public void setBusRoute(BusRoute busRoute) {
		this.busRoute = busRoute;
	}

	public Date getTerminalInTime() {
		return terminalInTime;
	}

	public void setTerminalInTime(Date terminalInTime) {
		this.terminalInTime = terminalInTime;
	}

	@Override
	public Date getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = departureTime;
	}

	@Override
	public Date getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public OperationalStage getStage() {
		return stage;
	}

	public void setStage(OperationalStage stage) {
		this.stage = stage;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isBookingAllowed() {
		return bookingAllowed;
	}

	public void setBookingAllowed(boolean bookingAllowed) {
		this.bookingAllowed = bookingAllowed;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public Conductor getConductor() {
		return conductor;
	}

	public void setConductor(Conductor conductor) {
		this.conductor = conductor;
	}

	public Float getLoadFactor() {
		return loadFactor;
	}

	public void setLoadFactor(Float loadFactor) {
		this.loadFactor = loadFactor;
	}

	public SeatingProfile getSeatingProfile() {
		return seatingProfile;
	}

	public void setSeatingProfile(SeatingProfile seatingProfile) {
		this.seatingProfile = seatingProfile;
	}

	public Date getTbBookingEndTime() {
		return tbBookingEndTime;
	}

	public void setTbBookingEndTime(Date tbBookingEndTime) {
		this.tbBookingEndTime = tbBookingEndTime;
	}

	public Date getWebBookingEndTime() {
		return webBookingEndTime;
	}

	public void setWebBookingEndTime(Date webBookingEndTime) {
		this.webBookingEndTime = webBookingEndTime;
	}

	public Date getTicketingActiveTime() {
		return ticketingActiveTime;
	}

	public void setTicketingActiveTime(Date ticketingActiveTime) {
		this.ticketingActiveTime = ticketingActiveTime;
	}

	public Set<BusScheduleBusStop> getScheduleStops() {
		return scheduleStops;
	}

	public void setScheduleStops(Set<BusScheduleBusStop> scheduleStops) {
		this.scheduleStops = scheduleStops;
	}

	@Override
	public BigInteger getAllowedDivisions() {
		return allowedDivisions;
	}

	@Override
	public void setAllowedDivisions(BigInteger allowedDivisions) {
		this.allowedDivisions = allowedDivisions;
	}

//	@Override
//	public void populateAllowedDivisions() {
//		if (getAllowedDivisions() == null) {
//			setAllowedDivisions(bus.getDivision().getBitmask());
//		}
//		if (getWriteAllowedDivisions() == null) {
//			setWriteAllowedDivisions(bus.getDivision().getBitmask());
//		}
//	}
}
