package lk.express.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.express.Reservation;
import lk.express.db.HibernateUtil;
import lk.express.db.dao.DAOFactory;
import lk.express.schedule.BusSchedule;
import lk.express.schedule.BusScheduleBusStop;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AvailabilityCheckerImpl implements AvailabilityChecker {

	protected static final DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);

	private static final String heldItemSeatsSql = "SELECT `schedule_id` AS `scheduleId`, `from_bus_stop_id` AS `fromBusStopId`, `to_bus_stop_id` AS `toBusStopId`, `seat_number` AS `seatNumber` FROM `bus_held_item` JOIN `bus_held_item_seat` ON `bus_held_item`.`id` = `bus_held_item_seat`.`bus_held_item_id` WHERE `schedule_id` = :scheduleId";
	private static final String bookingItemSeatsSql = "SELECT "
			+ "`schedule_id` AS `scheduleId`, `from_bus_stop_id` AS `fromBusStopId`, `to_bus_stop_id` AS `toBusStopId`, "
			+ "`seat_number` AS `seatNumber`, `journey_performed` AS `journeyPerformed`, `name`, `nic`, `gender`, `mobile_telephone` AS `mobile`, `email`, "
			+ "`booking_payments`.`Id` AS `bookingId`, `reference` AS `bookingReference`, `booking_payments`.`remarks` AS `bookingRemarks`, `user_id` AS `bookingUserId`, "
			+ "`booking_time` AS `bookingTime`, `booking_payments`.`status_code` AS `bookingStatusCode`, `agent_id` AS `bookingAgentId`, "
			+ "`chargeable` AS `bookingChargeable`, `total_payments` AS `bookingPayments`, `payment_modes` AS `bookingPaymentModes`, `total_refunds` AS `bookingRefunds`, "
			+ "`booking_item_passenger`.`passenger_type` AS `passengerType` "
			+ "FROM `booking_item` JOIN `booking_payments` ON `booking_item`.`booking_id` = `booking_payments`.`id` "
			+ "JOIN `booking_item_passenger` ON `booking_item`.`id` = `booking_item_passenger`.`booking_item_id` "
			+ "JOIN `passenger` ON `passenger`.`id` = `booking_item_passenger`.`passenger_id` "
			+ "WHERE `booking_item_passenger`.`status_code` != 'CANC' AND `schedule_id` = :scheduleId";
	private static final String bookingItemSeatsSql_improved = " select bi.schedule_id as scheduleId,bi.from_bus_stop_id as fromBusStopId, bi.to_bus_stop_id as toBusStopId,bip.seat_number as seatNumber,bip.journey_performed as journeyPerformed,pas.name as name, "
			+ " pas.nic as nic,pas.gender as gender,pas.mobile_telephone as mobile,pas.email as email,bk.id  as bookingId,bk.reference as bookingReference,bk.remarks as bookingRemarks,bk.user_id as bookingUserId, "
			+ " bk.booking_time as bookingTime,bk.status_code as bookingStatusCode, bk.agent_id as bookingAgentId, bk.chargeable as bookingChargeable, bk.chargeable as bookingPayments,   "
			+ " CAST(null as char) as bookingRefunds,bip.passenger_type as passengerType,replace(bk_payment_mode(bk.id),'Vendor','')  as bookingPaymentModes from booking bk "
			+ " join booking_item bi on bk.id=bi.booking_id "
			+ " join booking_item_passenger bip on bi.id=bip.booking_item_id  "
			+ " join passenger pas on bip.passenger_id=pas.id where bi.schedule_id= :scheduleId and bk.status_code!= 'CANC' ";
	private static final String stopIndexSql = "SELECT `bus_stop_id`, `idx` FROM `bus_schedule_bus_stop` WHERE `schedule_id` = :scheduleId";
	private static final String seatsSql = "SELECT `bus_seat`.`seat_number` FROM `bus` JOIN `bus_seat` ON `bus_seat`.`bus_type_id` = `bus`.`bus_type_id` JOIN `bus_schedule` ON `bus_schedule`.`bus_id` = `bus`.`id` WHERE `bus_schedule`.`id` = :scheduleId";

	protected int scheduleId;
	private int fromStopId;
	private int toStopId;
	private boolean withHeldItems;
	private boolean ignoreSeatingProfile;

	private int fromStopIndex;
	private int toStopIndex;
	private List<String> allSeats;

	private static final Logger logger = LoggerFactory.getLogger(AvailabilityCheckerImpl.class);

	/**
	 * @param scheduleId    id of the schedule
	 * @param fromStopId    id of the boarding (bus) stop
	 * @param toStopId      id of the dropping (bus) stop
	 * @param withHeldItems whether to take held items into consideration
	 */
	public AvailabilityCheckerImpl(int scheduleId, int fromStopId, int toStopId, boolean withHeldItems,
			boolean ignoreSeatingProfile) {
		this.scheduleId = scheduleId;
		this.fromStopId = fromStopId;
		this.toStopId = toStopId;
		this.withHeldItems = withHeldItems;
		this.ignoreSeatingProfile = ignoreSeatingProfile;

		this.fromStopIndex = getIndexForStop(this.fromStopId);
		this.toStopIndex = getIndexForStop(this.toStopId);
	}

	private int getIndexForStop(int busStopId) {
		Criteria criteria = HibernateUtil.getCurrentSession().createCriteria(BusScheduleBusStop.class)
				.add(Restrictions.eq("scheduleId", scheduleId)).createAlias("stop", "stop")
				.add(Restrictions.eq("stop.id", busStopId));
		BusScheduleBusStop busScheduleBusStop = (BusScheduleBusStop) criteria.uniqueResult();

		int index = -1;
		if (busScheduleBusStop != null) {
			index = busScheduleBusStop.getIdx();
		}
		return index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.search.AvailabilityChecker#getAllSeats()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllSeats() {
		if (allSeats == null) {
			Session session = HibernateUtil.getCurrentSessionWithTransaction();
			allSeats = session.createSQLQuery(seatsSql).setParameter("scheduleId", scheduleId).list();
		}
		return new ArrayList<String>(allSeats);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.search.AvailabilityChecker#getAvailableSeats()
	 */
	@Override
	public List<String> getAvailableSeats() {
		BusSchedule schedule = daoFac.getBusScheduleDAO().get(scheduleId);
		List<String> seats = null;
		if (!ignoreSeatingProfile && schedule.getSeatingProfile() != null) {
			seats = new ArrayList<String>(schedule.getSeatingProfile().getSeatNumbers());
		} else {
			seats = getAllSeats();
		}
		return seats;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.search.AvailabilityChecker#getUnavailableSeats()
	 */
	@Override
	public List<String> getUnavailableSeats() {
		List<String> seats = getAllSeats();
		List<String> availableSeats = getAvailableSeats();
		seats.removeAll(availableSeats);
		return seats;
	}

	/**
	 * Returns the list of reserved seat numbers.
	 * <p>
	 * This method returns booked seat numbers and if {@link #withHeldItems
	 * withHeldItems} is {@code true}, would also return held seat numbers.
	 * 
	 * @return reserved seat numbers
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Reservation> getReservedSeats() {
		Session session = HibernateUtil.getCurrentSessionWithTransaction();
//		Query bookingItemQuery = session.createSQLQuery(bookingItemSeatsSql).setParameter("scheduleId", scheduleId)
//				.setResultTransformer(Transformers.aliasToBean(Reservation.class));
		
		Query bookingItemQuery = session.createSQLQuery(bookingItemSeatsSql_improved).setParameter("scheduleId", scheduleId)
		.setResultTransformer(Transformers.aliasToBean(Reservation.class));
		List<Reservation> reservations = bookingItemQuery.list();
		if (withHeldItems) {
			Query heldItemQuery = session.createSQLQuery(heldItemSeatsSql).setParameter("scheduleId", scheduleId)
					.setResultTransformer(Transformers.aliasToBean(Reservation.class));
			reservations.addAll(heldItemQuery.list());
		}
		List<List<Object>> stopIndices = session.createSQLQuery(stopIndexSql).setParameter("scheduleId", scheduleId)
				.setResultTransformer(Transformers.TO_LIST).list();
		Map<Integer, Integer> stopIdToIndex = new HashMap<Integer, Integer>();
		for (List<Object> stopIndex : stopIndices) {
			stopIdToIndex.put((Integer) stopIndex.get(0), (Integer) stopIndex.get(1));
		}
		List<Reservation> reservedSeats = new ArrayList<Reservation>();
		for (Reservation reservation : reservations) {
			int fromIndex = stopIdToIndex.get(reservation.getFromBusStopId());
			int toIndex = stopIdToIndex.get(reservation.getToBusStopId());
			if (!(fromIndex >= toStopIndex || toIndex <= fromStopIndex)) {
				reservedSeats.add(reservation);
			}
		}
		return reservedSeats;
	}

	@Override
	public List<String> getSeatNumbers(Collection<Reservation> reservations) {
		List<String> numbers = new ArrayList<>();

		for (Reservation reservation : reservations) {
			numbers.add(reservation.getSeatNumber());
		}
		return numbers;
	}

	/**
	 * Returns the list of vacant seat numbers.
	 * <p>
	 * Unless {@link #withHeldItems withHeldItems} is {@code true}, seats held would
	 * also be returned.
	 * 
	 * @param reservedSeats List of reserved seats if we already have that,
	 *                      {@code null} otherwise
	 * 
	 * @return available seat numbers
	 */
	@Override
	public List<String> getVacantSeats(List<String> reservedSeats) {
		List<String> seats = getAvailableSeats();
		if (reservedSeats == null) {
			reservedSeats = getSeatNumbers(getReservedSeats());
		}
		seats.removeAll(reservedSeats);
		return seats;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.search.AvailabilityChecker#isVacant(java.util.List)
	 */
	@Override
	public boolean isVacant(List<String> seatNumbers) {
		List<String> vacantSeats = getVacantSeats(null);
		return vacantSeats.containsAll(seatNumbers);
	}

}
