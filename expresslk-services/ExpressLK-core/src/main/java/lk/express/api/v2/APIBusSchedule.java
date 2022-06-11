package lk.express.api.v2;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class APIBusSchedule extends APIEntity {

	public APIBus bus;
	public APIBusRoute busRoute;
	public Date terminalInTime;
	public Date departureTime;
	public Date arrivalTime;
	public boolean active;
	public Set<APIBusScheduleBusStop> scheduleStops = new HashSet<>();

	/**
	 * @exclude
	 */
	public APIBusSchedule() {

	}

	/**
	 * @exclude
	 */
	public APIBusSchedule(lk.express.schedule.BusSchedule e) {
		super(e);

		bus = new APIBus(e.getBus());
		busRoute = new APIBusRoute(e.getBusRoute());
		terminalInTime = e.getTerminalInTime();
		departureTime = e.getDepartureTime();
		arrivalTime = e.getArrivalTime();
		active = e.isActive();

		for (lk.express.schedule.BusScheduleBusStop s : e.getScheduleStops()) {
			scheduleStops.add(new APIBusScheduleBusStop(s));
		}
	}
}
