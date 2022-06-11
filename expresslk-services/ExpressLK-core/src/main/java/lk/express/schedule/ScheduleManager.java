package lk.express.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lk.express.bean.Bus;
import lk.express.bean.BusSeat;
import lk.express.db.HibernateUtil;
import lk.express.db.dao.DAOFactory;
import lk.express.db.dao.GenericDAO;
import lk.express.reservation.BookingItem;
import lk.express.reservation.BookingItemPassenger;
import lk.express.reservation.BookingStatus;
import lk.express.schedule.ScheduleChangeEvent.ScheduleChangeType;
import lk.express.supplier.Conductor;
import lk.express.supplier.Driver;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduleManager {

	private static final DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);
	private static final Logger logger = LoggerFactory.getLogger(ScheduleManager.class);

	private GenericDAO<BusSchedule> scheduleDAO = daoFac.getBusScheduleDAO();
	private BusSchedule schedule;
	private ISeatAssignmentStrategy assignmentStarategy;

	public ScheduleManager(BusSchedule schedule) {
		this.schedule = schedule;
		this.assignmentStarategy = new NoChangeStraregy();
	}

	/**
	 * Activate/deactivate a schedule.
	 * <p>
	 * However, this does not handle transferring booking items made on this
	 * schedule. If this is required, it should be handled manually.
	 * 
	 * @param activate
	 *            whether to activate
	 */
	public void activateSchedule(boolean activate) {
		schedule.setActive(activate);
		scheduleDAO.persist(schedule);

		ScheduleChangeEvent e = new ScheduleChangeEvent(schedule);
		e.setChangeType(ScheduleChangeType.ACTIVE_STATUS);

		ScheduleMatrix.getInstance().fireScheduleChangeEvent(e);
	}

	/**
	 * Reschedule a schedule and notifies the listeners.
	 * <p>
	 * Arrival times at each of the stops are adjusted based on their offset
	 * with the departure time
	 *
	 * @param departureTime
	 *            new departure time
	 */
	public void reschedule(Date departureTime) {
		Date oldDepTime = schedule.getDepartureTime();
		int timeDiff = (int) ((double) departureTime.getTime() - oldDepTime.getTime()) / 60000;

		// update departure and arrival times
		schedule.setDepartureTime(departureTime);
		schedule.setArrivalTime(addMinutes(schedule.getArrivalTime(), timeDiff));

		// update schedule stop times
		for (BusScheduleBusStop stop : schedule.getScheduleStops()) {
			stop.setDepartureTime(addMinutes(stop.getDepartureTime(), timeDiff));
			stop.setArrivalTime(addMinutes(stop.getArrivalTime(), timeDiff));
		}

		scheduleDAO.persist(schedule);

		ScheduleChangeEvent e = new ScheduleChangeEvent(schedule);
		e.setChangeType(ScheduleChangeType.RESCHEDULE);

		ScheduleMatrix.getInstance().fireScheduleChangeEvent(e);
	}

	private static Date addMinutes(Date date, int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes);
		return cal.getTime();
	}

	/**
	 * Change driver and notifies that listeners
	 * 
	 * @param driver
	 *            new driver
	 */
	public void changeDriver(Driver driver) {
		schedule.setDriver(driver);
		scheduleDAO.persist(schedule);

		ScheduleChangeEvent e = new ScheduleChangeEvent(schedule);
		e.setChangeType(ScheduleChangeType.DRIVER_CHANGE);

		ScheduleMatrix.getInstance().fireScheduleChangeEvent(e);
	}

	/**
	 * Change conductor and notifies that listeners
	 * 
	 * @param conductor
	 *            new conductor
	 */
	public void changeConductor(Conductor conductor) {
		schedule.setConductor(conductor);
		scheduleDAO.persist(schedule);

		ScheduleChangeEvent e = new ScheduleChangeEvent(schedule);
		e.setChangeType(ScheduleChangeType.CONDUCTOR_CHANGE);

		ScheduleMatrix.getInstance().fireScheduleChangeEvent(e);
	}

	/**
	 * Changes the bus of the schedule.
	 * <p>
	 * Passenger seats are allocated by an internal algorithm to the new bus. In
	 * case this allocation is not possible (For example, if the new bus have
	 * less capacity) {@link IncompatibleScheduleChangeException} is thrown.
	 * 
	 * @param bus
	 *            new bus
	 * @throws IncompatibleScheduleChangeException
	 */
	public void changeBus(Bus bus) throws IncompatibleScheduleChangeException {

		boolean typeChange = !schedule.getBus().getBusType().equals(bus.getBusType());

		Map<String, String> seatMapping = null;
		if (typeChange) {
			seatMapping = assignmentStarategy.suggestSeatMapping(bus);
			logger.debug("Seat Mapping: " + seatMapping);
		}

		schedule.setBus(bus);
		scheduleDAO.persist(schedule);

		if (typeChange) {
			Session session = HibernateUtil.getCurrentSession();
			@SuppressWarnings("unchecked")
			List<BookingItem> items = session
					.createQuery("FROM BookingItem WHERE schedule = :schedule AND status.code != :canc")
					.setParameter("schedule", schedule).setParameter("canc", BookingStatus.CANCELLED).list();

			for (BookingItem item : items) {
				boolean changed = false;
				List<String> newSeatNumbers = new ArrayList<String>();
				for (BookingItemPassenger passenger : item.getPassengers()) {
					if (!BookingStatus.CANCELLED.equals(passenger.getStatus().getCode())) {
						String seatNumber = passenger.getSeatNumber();
						String newSeatNumber = seatMapping.get(seatNumber);
						assert newSeatNumber != null : "New seat number cannot be null";

						if (!seatNumber.equalsIgnoreCase(newSeatNumber)) {
							passenger.setSeatNumber(newSeatNumber);
							changed = true;
						}

						newSeatNumbers.add(newSeatNumber);
					}
				}

				if (changed) {
					GenericDAO<BookingItem> bookingItemDAO = daoFac.getBookingItemDAO();
					bookingItemDAO.persist(item);

					BusAndSeatChangeEvent e = new BusAndSeatChangeEvent(schedule);
					e.setChangeType(ScheduleChangeType.SEAT_CHANGE);
					e.setBookingItem(item);
					e.setSeatNumbers(newSeatNumbers);
					ScheduleMatrix.getInstance().fireScheduleChangeEvent(e);
				}
			}
		}

		ScheduleChangeEvent e = new ScheduleChangeEvent(schedule);
		e.setChangeType(ScheduleChangeType.BUS_CHANGE);
		ScheduleMatrix.getInstance().fireScheduleChangeEvent(e);
	}

	public interface ISeatAssignmentStrategy {

		/**
		 * Suggests a seat mapping to the new bus for the seats already reserved
		 * 
		 * @param bus
		 *            new bus
		 * 
		 * @return a map of new seat numbers against the old seat numbers
		 */
		Map<String, String> suggestSeatMapping(Bus bus) throws IncompatibleScheduleChangeException;
	}

	public abstract class SeatAssignmentStrategy implements ISeatAssignmentStrategy {

		protected BusSeat getSeatByNumber(List<BusSeat> list, String seatNumber) {
			for (BusSeat seat : list) {
				if (seatNumber.equals(seat.getNumber())) {
					return seat;
				}
			}
			return null;
		}

		protected Map<Integer, BusSeat> getSeatsByRow(List<BusSeat> list, int x) {
			Map<Integer, BusSeat> seats = new HashMap<Integer, BusSeat>();
			for (BusSeat seat : list) {
				if (x == seat.getX()) {
					seats.put(seat.getY(), seat);
				}
			}
			return seats;
		}

		protected int[] getMinMaxAisle(Collection<BusSeat> rowSeats) {
			int minAisle = Integer.MAX_VALUE, maxAisle = Integer.MIN_VALUE;
			for (BusSeat seat : rowSeats) {
				if (BusSeat.SEAT_TYPE_AISLE.equals(seat.getSeatType())) {
					int y = seat.getY();
					if (y > maxAisle) {
						maxAisle = y;
					}
					if (y < minAisle) {
						minAisle = y;
					}
				}
			}
			return new int[] { minAisle, maxAisle };
		}

		protected Map<String, List<BusSeat>> getRemainingSeatMap(List<BusSeat> newSeats) {
			Map<String, List<BusSeat>> seatMap = new HashMap<String, List<BusSeat>>();
			for (BusSeat newSeat : newSeats) {
				List<BusSeat> seats = seatMap.get(newSeat.getSeatType());
				if (seats == null) {
					seats = new ArrayList<BusSeat>();
					seatMap.put(newSeat.getSeatType(), seats);
				}
				seats.add(newSeat);
			}
			return seatMap;

		}
	}

	public class NoChangeStraregy extends SeatAssignmentStrategy {

		@Override
		public Map<String, String> suggestSeatMapping(Bus bus) throws IncompatibleScheduleChangeException {

			Session session = HibernateUtil.getCurrentSession();
			@SuppressWarnings("unchecked")
			List<BookingItem> items = session
					.createQuery("FROM BookingItem WHERE schedule = :schedule AND status.code != :canc ")
					.setParameter("schedule", schedule).setParameter("canc", BookingStatus.CANCELLED)
					
					.list();
			int bookedSeatCount = 0;
			for (BookingItem item : items) {
				bookedSeatCount += item.getPassengers().stream()
						.filter(p -> !BookingStatus.CANCELLED.equals(p.getStatus().getCode())).count();
			}

			if (bookedSeatCount > bus.getBusType().getSeatingCapacity()) {
				throw new IncompatibleScheduleChangeException("Insufficient capacity in the new bus");
			}

			GenericDAO<BusSeat> busSetaDAO = daoFac.getGenericDAO(BusSeat.class);
			BusSeat newSeatExample = new BusSeat();
			newSeatExample.setBusTypeId(bus.getBusType().getId());
			List<BusSeat> newSeats = busSetaDAO.find(newSeatExample);
			System.out.println("New Bus Seats "+newSeats.toString());

			List<String> toAssign = new ArrayList<>();
			Map<String, String> suggestions = new HashMap<String, String>();
			for (BookingItem item : items) {
				for (BookingItemPassenger passenger : item.getPassengers()) {
					if (!BookingStatus.CANCELLED.equals(passenger.getStatus().getCode())) {
						boolean found = false;
						for (BusSeat newSeat : newSeats) {
							
							//NOTE: Seat numbers in newSeat object are of 01,..09,10
							// in database its stored as 1,2..9,10..20 
							// below we add string format to convert database value to match desired pattern.
							
							if (newSeat.getNumber().equals(String.format("%2s",passenger.getSeatNumber()).replace(' ','0'))) {
								suggestions.put(passenger.getSeatNumber(), newSeat.getNumber());
								found = true;
							}
						}
						if (!found) {
							System.out.println(passenger.getSeatNumber());
							toAssign.add(passenger.getSeatNumber());
						}
					}
				}
			}
			if (!toAssign.isEmpty()) {
				throw new IncompatibleScheduleChangeException("Seat re-allocation required! Unavailable seats: "
						+ toAssign);
			}

			return suggestions;
		}
	}

	public class SeatNumberMatchingStratey extends SeatAssignmentStrategy {

		@Override
		public Map<String, String> suggestSeatMapping(Bus bus) throws IncompatibleScheduleChangeException {

			Session session = HibernateUtil.getCurrentSession();
			@SuppressWarnings("unchecked")
			List<BookingItem> items = session
					.createQuery("FROM BookingItem WHERE schedule = :schedule AND status.code != :canc")
					.setParameter("schedule", schedule).setParameter("canc", BookingStatus.CANCELLED).list();

			int bookedSeatCount = 0;
			for (BookingItem item : items) {
				bookedSeatCount += item.getPassengers().stream()
						.filter(p -> !BookingStatus.CANCELLED.equals(p.getStatus().getCode())).count();
			}

			if (bookedSeatCount > bus.getBusType().getSeatingCapacity()) {
				throw new IncompatibleScheduleChangeException("Insufficient capacity in the new bus");
			}

			GenericDAO<BusSeat> busSetaDAO = daoFac.getGenericDAO(BusSeat.class);
			BusSeat newSeatExample = new BusSeat();
			newSeatExample.setBusTypeId(bus.getBusType().getId());
			List<BusSeat> newSeats = busSetaDAO.find(newSeatExample);

			Map<String, String> suggestions = new HashMap<String, String>();
			boolean failed = false;
			out: for (BookingItem item : items) {
				for (BookingItemPassenger passenger : item.getPassengers()) {
					if (!BookingStatus.CANCELLED.equals(passenger.getStatus().getCode())) {
						String oldSeatNumber = passenger.getSeatNumber();
						boolean found = false;
						for (BusSeat newSeat : newSeats) {
							if (oldSeatNumber.equals(newSeat.getNumber())) {
								suggestions.put(oldSeatNumber, oldSeatNumber);
								found = true;
							}
						}
						if (!found) {
							failed = true;
							break out;
						}
					}
				}
			}

			if (!failed) {
				ISeatAssignmentStrategy backupStrategy = new SeatTypeMatchingStrategy();
				suggestions = backupStrategy.suggestSeatMapping(bus);
			}

			return suggestions;
		}
	}

	public class SeatTypeMatchingStrategy extends SeatAssignmentStrategy {

		@Override
		public Map<String, String> suggestSeatMapping(Bus bus) throws IncompatibleScheduleChangeException {

			Session session = HibernateUtil.getCurrentSession();
			@SuppressWarnings("unchecked")
			List<BookingItem> items = session
					.createQuery("FROM BookingItem WHERE schedule = :schedule AND status.code != :canc")
					.setParameter("schedule", schedule).setParameter("canc", BookingStatus.CANCELLED).list();

			int bookedSeatCount = 0;
			for (BookingItem item : items) {
				bookedSeatCount += item.getPassengers().stream()
						.filter(p -> !BookingStatus.CANCELLED.equals(p.getStatus().getCode())).count();
			}

			if (bookedSeatCount > bus.getBusType().getSeatingCapacity()) {
				throw new IncompatibleScheduleChangeException("Insufficient capacity in the new bus");
			}

			GenericDAO<BusSeat> busSetaDAO = daoFac.getGenericDAO(BusSeat.class);

			BusSeat oldSeatExample = new BusSeat();
			oldSeatExample.setBusTypeId(schedule.getBus().getBusType().getId());
			List<BusSeat> oldSeats = busSetaDAO.find(oldSeatExample);

			BusSeat newSeatExample = new BusSeat();
			newSeatExample.setBusTypeId(bus.getBusType().getId());
			List<BusSeat> newSeats = busSetaDAO.find(newSeatExample);

			Comparator<BusSeat> xComparator = Comparator.comparingInt(BusSeat::getX);
			int oldMaxX = oldSeats.stream().max(xComparator).get().getX();
			int newMaxX = newSeats.stream().max(xComparator).get().getX();

			List<BusSeat> oldBookedSeats = new ArrayList<>();
			for (BookingItem item : items) {
				for (BookingItemPassenger passenger : item.getPassengers()) {
					if (!BookingStatus.CANCELLED.equals(passenger.getStatus().getCode())) {
						String oldSeatNumber = passenger.getSeatNumber();
						BusSeat oldSeat = getSeatByNumber(oldSeats, oldSeatNumber);
						oldBookedSeats.add(oldSeat);
					}
				}
			}
			// sort by the descending order of x, so we have seats from last row
			// first
			oldBookedSeats.sort((BusSeat b1, BusSeat b2) -> b2.getX().compareTo(b1.getX()));

			Map<String, String> suggestions = new HashMap<String, String>();
			List<BusSeat> toAssign = new ArrayList<BusSeat>();
			for (BusSeat oldSeat : oldBookedSeats) {
				BusSeat newSeat = suggestSeatSimple(oldSeat, newSeats, oldSeats, oldMaxX, newMaxX);
				if (newSeat != null) {
					newSeats.remove(newSeat);
					suggestions.put(oldSeat.getNumber(), newSeat.getNumber());
				} else {
					toAssign.add(oldSeat);
				}
			}

			Map<String, List<BusSeat>> remainingSeatMap = getRemainingSeatMap(newSeats);

			// assign seats of same type
			Iterator<BusSeat> it = toAssign.iterator();
			while (it.hasNext()) {
				BusSeat oldSeat = it.next();
				String type = oldSeat.getSeatType();
				List<BusSeat> remainingSeats = remainingSeatMap.get(type);
				if (remainingSeats != null && remainingSeats.size() > 0) {
					BusSeat newSeat = remainingSeats.remove(remainingSeats.size() - 1);
					it.remove();
					suggestions.put(oldSeat.getNumber(), newSeat.getNumber());
				}
			}

			// assign any seat
			it = toAssign.iterator();
			loop: while (it.hasNext()) {
				BusSeat oldSeat = it.next();
				for (List<BusSeat> remainingSeats : remainingSeatMap.values()) {
					if (remainingSeats.size() > 0) {
						BusSeat newSeat = remainingSeats.remove(remainingSeats.size() - 1);
						it.remove();
						suggestions.put(oldSeat.getNumber(), newSeat.getNumber());
						continue loop; // done with this seat. Move to the next
										// seat
										// to assign
					}
				}
			}

			if (toAssign.size() == 0) {
				return suggestions;
			} else {
				logger.error("There are more seats to assign! This usually indicates an issue in the seat assigning algorithm...");
				logger.error("Partial seat map: " + suggestions);
				logger.error("More to assign: " + toAssign);
				logger.error("Remaining seats: " + remainingSeatMap);
				return null;
			}
		}

		private BusSeat suggestSeatSimple(BusSeat oldSeat, List<BusSeat> newSeats, List<BusSeat> oldSeats, int oldMaxX,
				int newMaxX) {

			int oldX = oldSeat.getX();
			int oldY = oldSeat.getY();
			String oldType = oldSeat.getSeatType();

			Map<Integer, BusSeat> newRowSeats;
			if (oldX == oldMaxX) { // last row; assign from last row
				newRowSeats = getSeatsByRow(newSeats, newMaxX);
			} else {
				newRowSeats = getSeatsByRow(newSeats, oldX);
			}

			if (newRowSeats.isEmpty()) { // this row doesn't exist
				return null;
			}
			if (BusSeat.SEAT_TYPE_WINDOW.equals(oldType)) {
				if (oldY == 0) { // left side window seat
					BusSeat newSeat = newRowSeats.get(0);
					if (newSeat != null && BusSeat.SEAT_TYPE_WINDOW.equals(newSeat.getSeatType())) {
						return newSeat;
					}
				} else { // right side window seat
					for (BusSeat newSeat : newRowSeats.values()) {
						if (newSeat.getY() != 0 && BusSeat.SEAT_TYPE_WINDOW.equals(newSeat.getSeatType())) {
							return newSeat;
						}
					}
				}
			} else if (BusSeat.SEAT_TYPE_AISLE.equals(oldType)) {
				// assumption, single aisle
				Map<Integer, BusSeat> oldRowSeats = getSeatsByRow(oldSeats, oldX);
				int[] oldAisle = getMinMaxAisle(oldRowSeats.values());
				int[] newAisle = getMinMaxAisle(newRowSeats.values());

				if (oldY == oldAisle[0]) { // left aisle
					return newRowSeats.get(newAisle[0]);
				} else if (oldY == oldAisle[1]) { // right aisle
					return newRowSeats.get(newAisle[1]);
				}
			} else if (BusSeat.SEAT_TYPE_ORDINARY.equals(oldType)) {
				BusSeat newSeat = newRowSeats.get(oldY);
				if (newSeat != null && BusSeat.SEAT_TYPE_ORDINARY.equals(newSeat.getSeatType())) {
					return newSeat;
				}
			}

			return null;
		}
	}
}
