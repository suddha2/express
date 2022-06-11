package lk.express.schedule;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.HasDepartureArrival;
import lk.express.bean.HasAllowedDivisions;
import lk.express.bean.LightEntity;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "bus_schedule_light")
@XmlType(name = "BusScheduleLight", namespace = "http://schedule.express.lk")
@XmlRootElement
public class BusScheduleLight extends LightEntity implements HasDepartureArrival, HasAllowedDivisions {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "bus_id")
	private Integer bus;
	@Column(name = "bus_route_id")
	private Integer busRoute;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "terminal_in_time")
	private Date terminalInTime;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "departure_time")
	private Date departureTime;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "arrival_time")
	private Date arrivalTime;
	@Column(name = "stage_id")
	private Integer stage;
	@Column(name = "active", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean active = true;
	@Column(name = "booking_allowed", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean bookingAllowed = true;

	@Column(name = "driver_id")
	private Integer driver;
	@Column(name = "conductor_id")
	private Integer conductor;

	@Column(name = "load_factor")
	private Float loadFactor;
	@Column(name = "seating_profile_id")
	private Integer seatingProfile;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "tb_booking_end_time")
	private Date tbBookingEndTime;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "web_booking_end_time")
	private Date webBookingEndTime;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ticketing_active_time")
	private Date ticketingActiveTime;

	@Column(name = "allowed_divisions")
	private BigInteger allowedDivisions;

	@Column(name = "bus_route_name")
	private String busRouteName;
	@Column(name = "bus_plate_number")
	private String busPlateNumber;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBus() {
		return bus;
	}

	public void setBus(Integer bus) {
		this.bus = bus;
	}

	public Integer getBusRoute() {
		return busRoute;
	}

	public void setBusRoute(Integer busRoute) {
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

	public Integer getStage() {
		return stage;
	}

	public void setStage(Integer stage) {
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

	public Integer getDriver() {
		return driver;
	}

	public void setDriver(Integer driver) {
		this.driver = driver;
	}

	public Integer getConductor() {
		return conductor;
	}

	public void setConductor(Integer conductor) {
		this.conductor = conductor;
	}

	public Float getLoadFactor() {
		return loadFactor;
	}

	public void setLoadFactor(Float loadFactor) {
		this.loadFactor = loadFactor;
	}

	public Integer getSeatingProfile() {
		return seatingProfile;
	}

	public void setSeatingProfile(Integer seatingProfile) {
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

	@Override
	public BigInteger getAllowedDivisions() {
		return allowedDivisions;
	}

	@Override
	public void setAllowedDivisions(BigInteger allowedDivisions) {
		this.allowedDivisions = allowedDivisions;
	}

	public String getBusRouteName() {
		return busRouteName;
	}

	public void setBusRouteName(String busRouteName) {
		this.busRouteName = busRouteName;
	}

	public String getBusPlateNumber() {
		return busPlateNumber;
	}

	public void setBusPlateNumber(String busPlateNumber) {
		this.busPlateNumber = busPlateNumber;
	}
}
