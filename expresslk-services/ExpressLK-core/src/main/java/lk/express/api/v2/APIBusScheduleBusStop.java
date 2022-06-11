package lk.express.api.v2;

import java.util.Date;

public class APIBusScheduleBusStop extends APIEntity {

	public Integer idx;
	public APIBusStop stop;
	public Date arrivalTime;
	public Date departureTime;

	/**
	 * @exclude
	 */
	public APIBusScheduleBusStop() {

	}

	/**
	 * @exclude
	 */
	public APIBusScheduleBusStop(lk.express.schedule.BusScheduleBusStop e) {
		super(e);

		idx = e.getIdx();
		stop = new APIBusStop(e.getStop());
		stop.id = e.getStop().getId();
		arrivalTime = e.getArrivalTime();
		departureTime = e.getDepartureTime();
	}
}
