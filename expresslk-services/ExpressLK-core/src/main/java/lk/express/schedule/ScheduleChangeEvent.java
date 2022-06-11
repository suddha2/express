package lk.express.schedule;

public class ScheduleChangeEvent {

	public enum ScheduleChangeType {
		/**
		 * Active status change
		 */
		ACTIVE_STATUS,
		/**
		 * Departure time change
		 */
		RESCHEDULE,
		/**
		 * Change of bus of a particular schedule
		 */
		BUS_CHANGE,
		/**
		 * Seat change due to bus change
		 */
		SEAT_CHANGE,
		/**
		 * Change of driver
		 */
		DRIVER_CHANGE,
		/**
		 * Change of conductor
		 */
		CONDUCTOR_CHANGE
	}

	private BusSchedule schedule;
	private ScheduleChangeType changeType;

	public ScheduleChangeEvent() {

	}

	public ScheduleChangeEvent(BusSchedule schedule) {
		this.schedule = schedule;
	}

	public BusSchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(BusSchedule schedule) {
		this.schedule = schedule;
	}

	public ScheduleChangeType getChangeType() {
		return changeType;
	}

	public void setChangeType(ScheduleChangeType changeType) {
		this.changeType = changeType;
	}
}
