package lk.express.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import lk.express.Reservation;
import lk.express.schedule.BusSchedule;

public class DummyAvailabilityChecker extends AvailabilityCheckerImpl {

	private static final long monthMillis = 30L * 24 * 60 * 60 * 1000;

	public DummyAvailabilityChecker(int scheduleId, int fromStopId, int toStopId, boolean withHeldItems,
			boolean ignoreSeatingProfile) {
		super(scheduleId, fromStopId, toStopId, withHeldItems, ignoreSeatingProfile);
	}

	@Override
	public List<Reservation> getReservedSeats() {

		Set<Reservation> reserved = new HashSet<Reservation>(super.getReservedSeats());
		List<String> seats = getAvailableSeats();

		BusSchedule schedule = daoFac.getBusScheduleDAO().get(scheduleId);
		// This is the fraction of seats expected to be booked just before
		// departure
		float loadFactor = schedule.getLoadFactor();
		if (loadFactor < 0) {
			loadFactor = 0.0f;
		}
		if (loadFactor > 1) {
			loadFactor = 1.0f;
		}

		// Time in milliseconds since a month before departure
		long timeDiff = (new Date()).getTime() - (schedule.getDepartureTime().getTime() - monthMillis);
		float timeFactor = (float) timeDiff / monthMillis;

		if (timeFactor < 0) {
			timeFactor = 0.0f;
		}
		if (timeFactor > 1) {
			timeFactor = 1.0f;
		}

		Random random = new Random(scheduleId);
		int dummyBookings = (int) (seats.size() * loadFactor * timeFactor);

		// Add a little randomness to the number of bookings
		int randomFactor = (int) (seats.size() * loadFactor * 0.2f);
		int randomNumber = 0;
		if (randomFactor > 0) {
			randomNumber = random.nextInt(randomFactor * 2) - randomFactor;
		}

		dummyBookings += randomNumber;
		if (dummyBookings < 0) {
			dummyBookings = 0;
		}
		if (dummyBookings > seats.size()) {
			dummyBookings = seats.size();
		}

		List<String> reservedSeats = getSeatNumbers(reserved);
		Set<Integer> generated = new HashSet<Integer>();
		while (generated.size() < dummyBookings) {
			Integer next = random.nextInt(seats.size());
			String nextSeat = seats.get(next);
			if (generated.add(next) && !reservedSeats.contains(nextSeat)) {
				reserved.add(createDummyReservation(nextSeat));
			}
		}

		return new ArrayList<Reservation>(reserved);
	}

	private Reservation createDummyReservation(String seatNumber) {
		Reservation reservation = new Reservation();
		reservation.setDummy(true);
		reservation.setSeatNumber(seatNumber);
		reservation.setScheduleId(scheduleId);
		return reservation;
	}
}
