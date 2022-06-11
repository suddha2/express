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

@Entity
@Table(name = "bus_route_bus_stop")
@XmlType(name = "BusRouteBusStop", namespace = "http://bean.express.lk")
@XmlRootElement
public class BusRouteBusStop extends lk.express.bean.Entity implements Comparable<BusRouteBusStop> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "route_id")
	private Integer routeId;
	@Column(name = "idx")
	private Integer index;
	@ManyToOne
	@JoinColumn(name = "bus_stop_id")
	private BusStop stop;
	@Column(name = "waiting_time")
	@Temporal(TemporalType.TIME)
	private Date waitingTime;
	@Column(name = "travel_time")
	@Temporal(TemporalType.TIME)
	private Date travelTime;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRouteId() {
		return routeId;
	}

	public void setRouteId(Integer routeId) {
		this.routeId = routeId;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public BusStop getStop() {
		return stop;
	}

	public void setStop(BusStop stop) {
		this.stop = stop;
	}

	public Date getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(Date waitingTime) {
		this.waitingTime = waitingTime;
	}

	public Date getTravelTime() {
		return travelTime;
	}

	public void setTravelTime(Date travelTime) {
		this.travelTime = travelTime;
	}

	@Override
	public int compareTo(BusRouteBusStop o) {
		return index.compareTo(o.index);
	}
}
