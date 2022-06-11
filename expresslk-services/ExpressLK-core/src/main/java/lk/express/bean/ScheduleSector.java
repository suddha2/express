package lk.express.bean;

import java.util.Date;

import lk.express.HasDepartureArrival;

/**
 * This is an intermediate data structure to capture the schedule for a sector.
 */
public class ScheduleSector implements HasDepartureArrival {

	private int scheduleId;
	private int fromCityId;
	private int toCityId;
	private Date departureTime;
	private Date arrivalTime;

	public int getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
	}

	public int getFromCityId() {
		return fromCityId;
	}

	public void setFromCityId(int fromCityId) {
		this.fromCityId = fromCityId;
	}

	public int getToCityId() {
		return toCityId;
	}

	public void setToCityId(int toCityId) {
		this.toCityId = toCityId;
	}

	public Date getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = departureTime;
	}

	public Date getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
}
