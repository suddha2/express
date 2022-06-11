package lk.express.search.api.v2;

import java.util.ArrayList;
import java.util.List;

import lk.express.AvailabilityResult;
import lk.express.Reservation;
import lk.express.admin.AgentAllocation;
import lk.express.api.v2.APIBusSeat;
import lk.express.bean.BusSeat;

/**
 * Availability the seat layout result
 * 
 * @version v2
 */
public class APIAvailabilityResult {

	/**
	 * List of vacant seat numbers
	 */
	public List<String> vacantSeats = new ArrayList<>();
	/**
	 * List of booked seat numbers
	 */
	public List<String> bookedSeats = new ArrayList<>();
	/**
	 * List of unavailable seat numbers
	 */
	public List<String> unavailableSeats = new ArrayList<>();
	/**
	 * List of all seats
	 */
	public List<APIBusSeat> seats = new ArrayList<>();

	/**
	 * @exclude
	 */
	public APIAvailabilityResult() {

	}

	/**
	 * @exclude
	 */
	public APIAvailabilityResult(AvailabilityResult e) {
		for (BusSeat bs : e.getSeats()) {
			seats.add(new APIBusSeat(bs));
		}

		vacantSeats.addAll(e.getVacantSeats());
		unavailableSeats.addAll(e.getUnavailableSeats());
		for (Reservation r : e.getBookedSeats()) {
			bookedSeats.add(r.getSeatNumber());
		}

		for (AgentAllocation aa : e.getAgentAllocations()) {
			for (String s : aa.getSeatNumbers()) {
				vacantSeats.remove(s);
				if (!bookedSeats.contains(s)) {
					unavailableSeats.add(s);
				}
			}
		}
	}
}
