package lk.express.bean;

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

import org.hibernate.annotations.Type;

@Entity
@Table(name = "service")
@XmlType(name = "Service", namespace = "http://bean.express.lk")
@XmlRootElement
public class Service extends lk.express.bean.Entity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "route_id")
	private BusRoute route;
	@Column(name = "monday", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean monday;
	@Column(name = "tuesday", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean tuesday;
	@Column(name = "wednesday", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean wednesday;
	@Column(name = "thursday", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean thursday;
	@Column(name = "friday", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean friday;
	@Column(name = "saturday", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean saturday;
	@Column(name = "sunday", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean sunday;
	@Column(name = "start_date")
	@Temporal(TemporalType.DATE)
	private Date startDate;
	@Column(name = "end_date")
	@Temporal(TemporalType.DATE)
	private Date endDate;

	// Time based services
	@Column(name = "departure_time")
	@Temporal(TemporalType.TIME)
	private Date departureTime;

	// Frequency based services
	@Column(name = "start_time")
	@Temporal(TemporalType.TIME)
	private Date startTime;
	@Column(name = "end_time")
	@Temporal(TemporalType.TIME)
	private Date endTime;
	@Column(name = "frequency")
	private Integer frequency;
	@Column(name = "exact_times", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean exactTimes;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BusRoute getRoute() {
		return route;
	}

	public void setRoute(BusRoute route) {
		this.route = route;
	}

	public Boolean getMonday() {
		return monday;
	}

	public void setMonday(Boolean monday) {
		this.monday = monday;
	}

	public Boolean getTuesday() {
		return tuesday;
	}

	public void setTuesday(Boolean tuesday) {
		this.tuesday = tuesday;
	}

	public Boolean getWednesday() {
		return wednesday;
	}

	public void setWednesday(Boolean wednesday) {
		this.wednesday = wednesday;
	}

	public Boolean getThursday() {
		return thursday;
	}

	public void setThursday(Boolean thursday) {
		this.thursday = thursday;
	}

	public Boolean getFriday() {
		return friday;
	}

	public void setFriday(Boolean friday) {
		this.friday = friday;
	}

	public Boolean getSaturday() {
		return saturday;
	}

	public void setSaturday(Boolean saturday) {
		this.saturday = saturday;
	}

	public Boolean getSunday() {
		return sunday;
	}

	public void setSunday(Boolean sunday) {
		this.sunday = sunday;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = departureTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Integer getFrequency() {
		return frequency;
	}

	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	public Boolean getExactTimes() {
		return exactTimes;
	}

	public void setExactTimes(Boolean exactTimes) {
		this.exactTimes = exactTimes;
	}
}
