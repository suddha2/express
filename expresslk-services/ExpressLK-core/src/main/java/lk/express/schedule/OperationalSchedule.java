package lk.express.schedule;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.HasDepartureArrival;
import lk.express.bean.HasAllowedDivisions;

@Entity
@Table(name = "operational_schedule")
@XmlType(name = "OperationalSchedule", namespace = "http://schedule.express.lk")
@XmlRootElement
public class OperationalSchedule extends lk.express.bean.Entity implements HasDepartureArrival, HasAllowedDivisions {

	private static final long serialVersionUID = 1L;

	// schedule
	@Id
	@Column(name = "schedule_id")
	private Integer id;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "terminal_in")
	private Date arrivalTime;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "terminal_out")
	private Date departureTime;
	@Column(name = "stage")
	private String stage;

	// route
	@Column(name = "route_id")
	private Integer routeId;
	@Column(name = "from_city_id")
	private Integer fromCityId;
	@Column(name = "route_name")
	private String routeName;
	@Column(name = "route_number")
	private String routeNumber;
	@Column(name = "route_number_name")
	private String routeNumberName;

	// bus
	@Column(name = "plate_number")
	private String plateNumber;

	// driver, conductor
	@Column(name = "conductor_name")
	private String conductorName;
	@Column(name = "conductor_mobile")
	private String conductorMobile;
	@Column(name = "driver_name")
	private String driverName;
	@Column(name = "driver_mobile")
	private String driverMobile;
	@Column(name = "bus_mobile")
	private String busMobile;

	// derived
	@Column(name = "seat_reservations")
	private Integer seatReservations;
	@Column(name = "total_cost")
	private Double totalCost;

	// seating profile
	@Column(name = "seating_profile_id")
	private Integer seatingProfileId;

	@Column(name = "division_id")
	private Integer divisionId;
	@Column(name = "allowed_divisions")
	private BigInteger allowedDivisions;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public Date getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	@Override
	public Date getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = departureTime;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public Integer getRouteId() {
		return routeId;
	}

	public void setRouteId(Integer routeId) {
		this.routeId = routeId;
	}

	public Integer getFromCityId() {
		return fromCityId;
	}

	public void setFromCityId(Integer fromCityId) {
		this.fromCityId = fromCityId;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getRouteNumber() {
		return routeNumber;
	}

	public void setRouteNumber(String routeNumber) {
		this.routeNumber = routeNumber;
	}

	public String getRouteNumberName() {
		return routeNumberName;
	}

	public void setRouteNumberName(String routeNumberName) {
		this.routeNumberName = routeNumberName;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	public String getConductorName() {
		return conductorName;
	}

	public void setConductorName(String conductorName) {
		this.conductorName = conductorName;
	}

	public String getConductorMobile() {
		return conductorMobile;
	}

	public void setConductorMobile(String conductorMobile) {
		this.conductorMobile = conductorMobile;
	}

	public String getBusMobile() {
		return busMobile;
	}

	public void setBusMobile(String busMobile) {
		this.busMobile = busMobile;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getDriverMobile() {
		return driverMobile;
	}

	public void setDriverMobile(String driverMobile) {
		this.driverMobile = driverMobile;
	}

	public Integer getSeatReservations() {
		return seatReservations;
	}

	public void setSeatReservations(Integer seatReservations) {
		this.seatReservations = seatReservations;
	}

	public Double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}

	public Integer getSeatingProfileId() {
		return seatingProfileId;
	}

	public void setSeatingProfileId(Integer seatingProfileId) {
		this.seatingProfileId = seatingProfileId;
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Override
	public BigInteger getAllowedDivisions() {
		return allowedDivisions;
	}

	@Override
	public void setAllowedDivisions(BigInteger allowedDivisions) {
		this.allowedDivisions = allowedDivisions;
	}
}
