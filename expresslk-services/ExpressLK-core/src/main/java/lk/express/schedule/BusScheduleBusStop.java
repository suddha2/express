package lk.express.schedule;

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

import lk.express.HasDepartureArrival;
import lk.express.bean.BusStop;

@Entity
@Table(name = "bus_schedule_bus_stop")
@XmlType(name = "BusScheduleBusStop", namespace = "http://schedule.express.lk")
@XmlRootElement
public class BusScheduleBusStop extends lk.express.bean.Entity implements HasDepartureArrival,
		Comparable<BusScheduleBusStop> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "schedule_id")
	private Integer scheduleId;
	@Column(name = "idx")
	private Integer idx;
	@ManyToOne
	@JoinColumn(name = "bus_stop_id")
	private BusStop stop;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "arrival_time")
	private Date arrivalTime;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "departure_time")
	private Date departureTime;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Integer scheduleId) {
		this.scheduleId = scheduleId;
	}

	public Integer getIdx() {
		return idx;
	}

	public void setIdx(Integer idx) {
		this.idx = idx;
	}

	public BusStop getStop() {
		return stop;
	}

	public void setStop(BusStop stop) {
		this.stop = stop;
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

	@Override
	public int compareTo(BusScheduleBusStop o) {
		return idx.compareTo(o.idx);
	}
}
