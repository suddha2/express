package lk.express;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.City;
import lk.express.schedule.BusSchedule;

/**
 * A <code>ResultLeg</code> may consist of one or more <code>ResultSector</code>
 * s which are connected by transits.
 */
@XmlRootElement
@XmlType(name = "ResultSector", namespace = "http://express.lk")
public class ResultSector extends CostPrice implements HasDepartureArrival {

	private static final long serialVersionUID = 1L;

	private BusSchedule schedule;
	private City fromCity;
	private City toCity;

	private Date departureTime;
	private Date arrivalTime;


	public BusSchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(BusSchedule schedule) {
		this.schedule = schedule;
	}

	public City getFromCity() {
		return fromCity;
	}

	public void setFromCity(City fromCity) {
		this.fromCity = fromCity;
	}

	public City getToCity() {
		return toCity;
	}

	public void setToCity(City toCity) {
		this.toCity = toCity;
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
	
}
