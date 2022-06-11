package lk.express.search;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lk.express.AvailabilityCriteria;
import lk.express.AvailabilityResponse;
import lk.express.AvailabilityResult;
import lk.express.BaseCriteria;
import lk.express.ClientDetail;
import lk.express.ConfirmResponse;
import lk.express.ConfirmationCriteria;
import lk.express.Context;
import lk.express.HoldCriteria;
import lk.express.HoldCriteria.SeatCriteria;
import lk.express.HoldResponse;
import lk.express.HoldResult;
import lk.express.HoldResult.HeldItem;
import lk.express.LegCriteria;
import lk.express.LegResult;
import lk.express.PassengerDetail;
import lk.express.PaymentCriteria;
import lk.express.PaymentMethodCriteria;
import lk.express.PaymentMethodCriteria.PaymentMethod;
import lk.express.PaymentRefundsResponse;
import lk.express.ReleaseResponse;
import lk.express.Reservation;
import lk.express.Result;
import lk.express.ResultLeg;
import lk.express.ResultSector;
import lk.express.SearchCriteria;
import lk.express.SearchResponse;
import lk.express.SearchResult;
import lk.express.SeatAllocations;
import lk.express.SeatingInfoCriteria;
import lk.express.SeatingInfoResult;
import lk.express.accounting.AccountingManager;
import lk.express.accounting.CompoundJournalEntry;
import lk.express.accounting.IJournalEntry;
import lk.express.accounting.JournalEntryFragment;
import lk.express.accounting.JournalEntryFragment.CrDr;
import lk.express.accounting.SingleJournalEntry;
import lk.express.admin.Agent;
import lk.express.admin.AgentAllocation;
import lk.express.admin.User;
import lk.express.bean.BusHeldItem;
import lk.express.bean.BusSeat;
import lk.express.bean.BusStop;
import lk.express.bean.Client;
import lk.express.bean.DiscountCode;
import lk.express.bean.OperationalStage;
import lk.express.bean.PassengerType;
import lk.express.bean.ScheduleSector;
import lk.express.bean.VendorPaymentRefundMode;
import lk.express.db.HibernateUtil;
import lk.express.db.dao.GenericDAO;
import lk.express.db.dao.HasCodeDAO;
import lk.express.filter.AmenityFilter;
import lk.express.filter.AvailabilityFilter;
import lk.express.filter.BusFilter;
import lk.express.filter.Filter;
import lk.express.filter.FilterData;
import lk.express.filter.LegFilterData;
import lk.express.filter.ScheduleFilter;
import lk.express.filter.TimeFilter;
import lk.express.filter.TransitFilter;
import lk.express.filter.TravelClassFilter;
import lk.express.reservation.Booking;
import lk.express.reservation.BookingItem;
import lk.express.reservation.BookingItemCharge;
import lk.express.reservation.BookingItemDiscount;
import lk.express.reservation.BookingItemMarkup;
import lk.express.reservation.BookingItemPassenger;
import lk.express.reservation.BookingItemTax;
import lk.express.reservation.BookingStatus;
import lk.express.reservation.Passenger;
import lk.express.reservation.listener.BookingChangeEvent;
import lk.express.reservation.listener.BookingChangeEvent.BookingChangeType;
import lk.express.reservation.listener.BookingMatrix;
import lk.express.schedule.BusSchedule;
import lk.express.schedule.BusScheduleBusStop;
import lk.express.search.util.SearchUtil;

public class BusGateway extends SearchGateway {

	private static final long serialVersionUID = 1L;

	private static final int MAX_RESULTS_COUNT = 1000;
	private static final String SEARCH_SP_CALL = "CALL SearchLeg(:fromCity, :toCity, :depTime, :depEndTime, :resultsCount, :divisionBitmask, :agentId)";
	private static final int DEFAULT_RESOURCE_HELD_DURATION = 10 * 60; // seconds
	private static final String CONFIG_RESOURCE_HELD_DURATION = "RESOURCE_HELD_DURATION";

	private static final Logger logger = LoggerFactory.getLogger(BusGateway.class);

	private static final HoldingLock holdingLock = new HoldingLock();

	private static final List<Filter<ResultLeg>> filterTypes = Arrays.asList(new AmenityFilter(),
			new AvailabilityFilter(), new BusFilter(), new ScheduleFilter(), new TimeFilter(), new TransitFilter(),
			new TravelClassFilter());

	private static final class HoldingLock {

		private Set<Integer> pool = new HashSet<Integer>();

		private void lock(Integer scehduleId) throws InterruptedException {
			synchronized (pool) {
				while (pool.contains(scehduleId)) {
					pool.wait();
				}
				pool.add(scehduleId);
			}
		}

		private void unlock(Integer scehduleId) {
			synchronized (pool) {
				pool.remove(scehduleId);
				pool.notifyAll();
			}
		}
	}

	private final AtomicInteger heldItemIndexSequence = new AtomicInteger(0);

	/**
	 * 'confirmlock' is a independent object, added to facilitate locking based on
	 * common object. This will minimize double booking created with in a single
	 * session
	 */
	// private static final Object confirmLock = new Object();

	public BusGateway() {
		super();
	}

	/**
	 * Search for a given search criteria.
	 * 
	 * @param criteria
	 *            search criteria
	 * @return the search result
	 */
	@Override
	public SearchResponse search(SearchCriteria criteria) {
		try {
			logger.debug("Search criteria: " + criteria);
			fillSearchCriteria(criteria);
			// validate
			LegCriteria[] legCriterion = criteria.getLegCriterion();
			LegCriteria outBoundLeg = legCriterion[0];
			if (outBoundLeg == null) {
				throw new IllegalArgumentException("There must be at least one LegCriteria in the SearchCriteria.");
			}

			Session dbSession = HibernateUtil.getCurrentSessionWithTransaction();

			this.searchCriteria = criteria;
			List<ScheduleSector> outBoundScheduleSectors = null;
			List<ScheduleSector> inBoundScheduleSectors = null;

			BigInteger bitmask = Context.getSessionData().getVisibleDivisionsBitmask();
			Agent agent = Context.getSessionData().getUser().getAgent();
			Integer agentId = null;
			if (agent != null) {
				agentId = agent.getId();
			}
			// perform search
			outBoundScheduleSectors = searchForLeg(dbSession, criteria.getFromCityId(), criteria.getToCityId(),
					outBoundLeg.getDepartureTime(), outBoundLeg.getDepartureTimeEnd(), outBoundLeg.getResultsCount(),
					bitmask, agentId);

			if (legCriterion.length > 1 && legCriterion[1] != null) {
				LegCriteria inBoundLeg = legCriterion[1];
				inBoundScheduleSectors = searchForLeg(dbSession, criteria.getToCityId(), criteria.getFromCityId(),
						inBoundLeg.getDepartureTime(), inBoundLeg.getDepartureTimeEnd(), inBoundLeg.getResultsCount(),
						bitmask, agentId);
			}

			// generate results
			ResultGenerator restultGenerator = new BusResultGenerator(criteria, outBoundScheduleSectors,
					inBoundScheduleSectors, getPricingManager());

			// store results in the gateway
			LegResult outLegResult = restultGenerator.getOutLegResult();
			for (ResultLeg leg : outLegResult.getLegs()) {
				outBoundResults.put(leg.getResultIndex(), leg);
			}
			LegResult inLegResult = restultGenerator.getInLegResult();
			if (inLegResult != null) {
				for (ResultLeg leg : inLegResult.getLegs()) {
					inBoundResults.put(leg.getResultIndex(), leg);
				}
			}

			// filter search results
			SearchResponse searchResponse = filter(criteria);

			// add filter data
			SearchResult result = searchResponse.getData();
			result.setOutBoundLegFilterData(getFilterData(new ArrayList<>(outBoundResults.values())));
			if (inLegResult != null) {
				result.setInBoundLegFilterData(getFilterData(new ArrayList<>(inBoundResults.values())));
			}

			logger.debug("Search response: " + searchResponse);
			return searchResponse;

		} catch (Exception e) {
			logger.error("Exception while searching", e);
			HibernateUtil.rollback();
			return new SearchResponse("Exception while searching");
		}
	}

	private LegFilterData getFilterData(List<ResultLeg> results) {
		LegFilterData data = new LegFilterData();
		for (Filter<ResultLeg> filter : filterTypes) {
			FilterData d = new FilterData();
			d.setType(filter.getClass().getSimpleName());
			d.setValues(filter.getValues(results));
			data.getData().add(d);
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	protected List<ScheduleSector> searchForLeg(Session session, int from, int to, Date dep, Date depEnd, int count,
			BigInteger divisionBitmask, Integer agentId) {

		if (count <= 0) {
			count = MAX_RESULTS_COUNT;
			if (depEnd == null) { // if neither depEnd nor count is set, all
									// results for 24 hours from dep is sent
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(dep);
				calendar.add(Calendar.HOUR, 24);
				depEnd = calendar.getTime();
			}
		}

		Query query = session.createSQLQuery(SEARCH_SP_CALL).setParameter("fromCity", from).setParameter("toCity", to)
				.setParameter("depTime", dep).setParameter("depEndTime", depEnd)
				.setParameter("divisionBitmask", divisionBitmask).setParameter("resultsCount", count)
				.setParameter("agentId", agentId).setResultTransformer(Transformers.aliasToBean(ScheduleSector.class));
		System.out.println(query.getQueryString());
		System.out.println(query.getNamedParameters());
		List<ScheduleSector> scheduleSectors = query.list();

		return scheduleSectors;
	}

	/**
	 * Check availability for a given criteria
	 * 
	 * @param availability
	 *            availability criteria
	 * @return availability details
	 */
	@Override
	public AvailabilityResponse checkAvailability(AvailabilityCriteria criteria) {
		try {
			populateAvailabilityCriteria(criteria);

			AvailabilityResult avlResult = new AvailabilityResult();
			AvailabilityChecker checker = new DummyAvailabilityChecker(criteria.getScheduleId(),
					criteria.getBoardingLocationId(), criteria.getDroppingLocationId(), true,
					BaseCriteria.isConductorApp(searchCriteria));
			SeatingInfoCriteria seatingInfo = new SeatingInfoCriteria();
			seatingInfo.setBusTypeId(criteria.getBusTypeId());
			SeatingInfoResult infoResult = getSeatingPlan(seatingInfo);
			avlResult.setSeats(infoResult.getSeats());
			
			GenericDAO<BusSchedule> scheduleDAO = daoFac.getBusScheduleDAO();
			BusSchedule schedule = scheduleDAO.get(criteria.getScheduleId());
			GenericDAO<AgentAllocation> agentAllocationDAO = daoFac.getGenericDAO(AgentAllocation.class);
			
			AgentAllocation ex = new AgentAllocation();
			ex.setBusRouteId(schedule.getBusRoute().getId());
			ex.setDivisionId(schedule.getBus().getDivision().getId());
			List<AgentAllocation> agentAllocations = agentAllocationDAO.find(ex);
			avlResult.setAgentAllocations(agentAllocations);
			
			logger.debug(" Start Get Reserved Seats ======================");
			List<Reservation> reservedSeats = checker.getReservedSeats();
			logger.debug(" End Get Reserved Seats ======================");
			
			avlResult.setBookedSeats(reservedSeats);
			logger.debug(" Start Get Vacant Seats ======================");
			avlResult.setVacantSeats(checker.getVacantSeats(checker.getSeatNumbers(reservedSeats)));
			logger.debug(" End Get Vacant Seats ======================");
			logger.debug(" Start Get Unavailable Seats ======================");
			avlResult.setUnavailableSeats(checker.getUnavailableSeats());
			logger.debug(" End Get Unavailable Seats ======================");
			AvailabilityResponse avlResponse = new AvailabilityResponse(avlResult);
			
			return avlResponse;

		} catch (Exception e) {
			logger.error("Exception while checking availability", e);
			HibernateUtil.rollback();
			return new AvailabilityResponse("Exception while checking availability");
		}
	}

	private void populateAvailabilityCriteria(AvailabilityCriteria criteria) {
		if (criteria.getResultIndex() != null && criteria.getSectorIndex() >= 0) {

			ResultSector sector = getResultSector(criteria.getResultIndex(), criteria.getSectorIndex());
			BusSchedule schedule = sector.getSchedule();

			if (criteria.getScheduleId() <= 0) {
				criteria.setScheduleId(schedule.getId());
			}

			if (criteria.getBusTypeId() <= 0) {
				criteria.setBusTypeId(schedule.getBus().getBusType().getId());
			}

			if (criteria.getBoardingLocationId() <= 0) {
				criteria.setBoardingLocationId(
						getClosestBoardingPoint(criteria.getResultIndex(), criteria.getSectorIndex()));
			}

			if (criteria.getDroppingLocationId() <= 0) {
				criteria.setDroppingLocationId(
						getFurthestDropoffPoint(criteria.getResultIndex(), criteria.getSectorIndex()));
			}
		}
	}

	private int getClosestBoardingPoint(String resultIndex, int sectorIndex) {
		ResultSector sector = getResultSector(resultIndex, sectorIndex);
		List<BusScheduleBusStop> stops = new ArrayList<>(sector.getSchedule().getScheduleStops());
		stops.sort((s1, s2) -> s1.getIdx().compareTo(s2.getIdx()));

		for (BusScheduleBusStop stop : stops) {
			if (stop.getStop().getCity().getId() == searchCriteria.getFromCityId()) {
				return stop.getStop().getId();
			}
		}
		return -1;
	}

	private int getFurthestDropoffPoint(String resultIndex, int sectorIndex) {
		ResultSector sector = getResultSector(resultIndex, sectorIndex);
		List<BusScheduleBusStop> stops = new ArrayList<>(sector.getSchedule().getScheduleStops());
		stops.sort((s1, s2) -> s1.getIdx().compareTo(s2.getIdx()));

		int dropOffLocationId = -1;
		for (BusScheduleBusStop stop : stops) {
			if (stop.getStop().getCity().getId() == searchCriteria.getToCityId()) {
				dropOffLocationId = stop.getStop().getId();
			}
		}
		return dropOffLocationId;
	}

	/**
	 * Returns the seating plan for given bus type
	 * 
	 * @param criteria
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private SeatingInfoResult getSeatingPlan(SeatingInfoCriteria criteria) throws Exception {

		Session dbSession = HibernateUtil.getCurrentSessionWithTransaction();
		String hql = "FROM BusSeat seat WHERE seat.busTypeId = :busType";
		Query query = dbSession.createQuery(hql).setParameter("busType", criteria.getBusTypeId());
		List<BusSeat> seats = query.list();

		SeatingInfoResult result = new SeatingInfoResult();
		result.setSeats(seats);
		return result;
	}

	/**
	 * Hold the result for a given criteria
	 * 
	 * @param criteria
	 *            holding criteria
	 * @return <code>HoldResult</code> containing <code>AdvisoryNotice</code>s.
	 */
	@Override
	public HoldResponse hold(HoldCriteria criteria) {
		try {
			logger.debug("Hold Criteria: " + criteria);

			ResultLeg resultLeg = getResultLeg(criteria.getResultIndex());

			List<HeldItem> heldItems = new ArrayList<>();
			List<Integer> heldItemsIds = new ArrayList<>();

			// verify whether all schedules are open-for-bookings
			for (SeatCriteria seatCriteria : criteria.getSeatCriterias()) {
				int i = seatCriteria.getSectorNumber();
				ResultSector sector = resultLeg.getSectors().get(i);
				if (!OperationalStage.OpenForBooking.equals(sector.getSchedule().getStage().getCode())) {
					return new HoldResponse("Schedule is not Open-for-bookings (OFB)");
				}
			}

			for (SeatCriteria seatCriteria : criteria.getSeatCriterias()) {
				int i = seatCriteria.getSectorNumber();
				ResultSector sector = resultLeg.getSectors().get(i);

				BusHeldItem heldItem = hold(criteria, sector.getSchedule(), i,
						(i == resultLeg.getSectors().size() - 1));
				if (heldItem != null) {
					HeldItem item = new HeldItem(criteria.getResultIndex(), seatCriteria.getSectorNumber(),
							heldItem.getId());
					heldItems.add(item);
					heldItemsIds.add(heldItem.getId());
				} else {
					HoldResponse response = new HoldResponse(HoldResponse.SEAT_NOT_AVAILABLE);
					response.setMessage("Some of the seats are not available");
					return response;
				}
			}

			HoldResult holdResult = new HoldResult();
			holdResult.setHeldItems(heldItems);
			holdResult.setHeldItemIds(heldItemsIds);

			HoldResponse holdResponse = new HoldResponse(holdResult);
			logger.debug("Hold Response: " + holdResponse);

			return holdResponse;
		} catch (Exception e) {
			logger.error("Exception while holding", e);
			HibernateUtil.rollback();
			return new HoldResponse("Exception while holding");
		}
	}

	@Override
	protected BusHeldItem hold(HoldCriteria criteria, BusSchedule schedule, int sectorIndex, boolean lastSector)
			throws InterruptedException {

		GenericDAO<BusStop> busStopDAO = daoFac.getBusStopDAO();
		GenericDAO<BusHeldItem> heldBusItemDAO = daoFac.getHeldBusItemDAO();

		// get session id from thread local session data
		String sessionId = Context.getSessionData().getSessionId();

		SeatCriteria seats = null;
		for (SeatCriteria seatCriteria : criteria.getSeatCriterias()) {
			if (seatCriteria.getSectorNumber() == sectorIndex) {
				seats = seatCriteria;
				break;
			}
		}

		Session session = HibernateUtil.getCurrentSessionWithTransaction();

		// Check if the same schedule already has seat lock for same seat number
		// This is to stop double booking on non-overlapping sectors.

		Query query = session.createSQLQuery(
				" SELECT * " + 
				" FROM bus_held_item " + 
				" join bus_held_item_seat on bus_held_item.id=bus_held_item_seat.bus_held_item_id " + 
				" where bus_held_item.schedule_id=:scheduleId and bus_held_item_seat.seat_number in (:seatNumbers)")
				.setParameter("scheduleId", schedule.getId())
				.setParameterList("seatNumbers", seats.getSeats());
		@SuppressWarnings("rawtypes")
		List result = query.list();
		

		if (!result.isEmpty()) {
			
			System.out.println("For Schedule : "+schedule.getId() + " Seat : "+seats.getSeats().toString()+ " is locked. "
					+ " Therfore failing the lock process.  ");
			
			return null;
		}

		BusHeldItem heldItem = new BusHeldItem();
		heldItem.setSessionId(sessionId);
		heldItem.setIndex(heldItemIndexSequence.incrementAndGet());
		heldItem.setResultIndex(criteria.getResultIndex());
		heldItem.setSectorIndex(sectorIndex);

		heldItem.setSeatNumbers(seats.getSeats());
		heldItem.setSchedule(schedule);

		if (sectorIndex == 0) {
			int boardingPoint = criteria.getBoardingPoint();
			if (boardingPoint <= 0) {
				boardingPoint = getClosestBoardingPoint(criteria.getResultIndex(), sectorIndex);
			}
			criteria.setBoardingPoint(boardingPoint);
			heldItem.setFromBusStop(busStopDAO.get(boardingPoint));
		}
		if (lastSector) {
			int dropoffPoint = criteria.getDroppingPoint();
			if (dropoffPoint <= 0) {
				dropoffPoint = getFurthestDropoffPoint(criteria.getResultIndex(), sectorIndex);
			}
			criteria.setDroppingPoint(dropoffPoint);
			heldItem.setToBusStop(busStopDAO.get(dropoffPoint));
		}

		Integer scheduleId = schedule.getId();
		try {
			holdingLock.lock(scheduleId);

			AvailabilityChecker checker = new AvailabilityCheckerImpl(scheduleId, criteria.getBoardingPoint(),
					criteria.getDroppingPoint(), true, BaseCriteria.isConductorApp(searchCriteria));
			// verify weather the selected seat is booked or held
			if (checker.isVacant(seats.getSeats())) {
				heldBusItemDAO.persist(heldItem);
				return heldItem;
			} else {
				return null;
			}
		} finally {
			holdingLock.unlock(scheduleId);
		}
	}

	/**
	 * Releases one or more held items
	 * 
	 * @param itemIds
	 *            items IDs of held items
	 */
	@Override
	public ReleaseResponse release(List<Integer> itemIds) {
		GenericDAO<BusHeldItem> heldBusItemDAO = daoFac.getHeldBusItemDAO();
		try {
			logger.debug("Release criteria: " + itemIds);

			for (Integer id : itemIds) {
				BusHeldItem heldItem = heldBusItemDAO.get(id);
				if (heldItem != null) {
					heldBusItemDAO.delete(heldItem);
				}
			}
			return new ReleaseResponse();
		} catch (Exception e) {
			logger.error("Exception while releasing", e);
			return new ReleaseResponse("Exception while releasing");
		}
	}

	/**
	 * Confirms a booking item with the search gateway.
	 * 
	 * @param criteria
	 *            confirmation criteria
	 * @return <code>Booking</code> containing all the confirmed
	 *         <code>BookingItem</code>s.
	 * 
	 *         TODO Refactor the method
	 */
	@Override
	public ConfirmResponse confirm(ConfirmationCriteria criteria) {

		// obtain lock before confirming the booking/reservation.
		//synchronized (confirmLock) {
			try {
				logger.debug("Confirmation Criteria: " + criteria);

				Session session = HibernateUtil.getCurrentSessionWithTransaction();

				// get session id from thread local session data
				String sessionId = Context.getSessionData().getSessionId();

				@SuppressWarnings("unchecked")
				List<BusHeldItem> heldItems = session.createCriteria(BusHeldItem.class)
						.add(Restrictions.eq("sessionId", sessionId))
						.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

				if (heldItems.isEmpty()) {
					return sendConfirmationError("No held items in the session!");
				}
				logger.debug("Before Seat Availablity check ");
				// Check for seats booked for current criteria
				for (BusHeldItem heldItem : heldItems) {

					Query query = session.createSQLQuery(" select bip.id "
							+ " from booking_item  bi join booking_item_passenger  bip on bi.id=bip.booking_item_id "
							+ " where bi.schedule_id=:scheduleId and bi.from_bus_stop_id=:fromStop and bi.to_bus_stop_id=:toStop "
							+ " and bip.seat_number in (:seatNumber) ");
					query.setParameter("scheduleId", heldItem.getSchedule());
					query.setParameter("fromStop", heldItem.getFromBusStop());
					query.setParameter("toStop", heldItem.getToBusStop());
					query.setParameter("seatNumber", heldItem.getSeatNumbers());
					if (query.uniqueResult() != null) {
						return sendConfirmationError("Booking already exists for seat(s) " + heldItem.getSeatNumbers());
					}
				}
				logger.debug("After Seat Availablity check ");
				
				for (SeatAllocations allocations : criteria.getItemSeatAllocations()) {
					// find the corresponding held item
					BusHeldItem heldItem = getHeldItem(allocations, heldItems);
					if (heldItem == null) {
						return sendConfirmationError("No held item for index " + allocations.getHeldItemId());
					}
				}
				logger.debug("Creating Booking Instance ");
				Booking booking = new Booking();
				booking.setRemarks(criteria.getRemarks());
				booking.setDivision(Context.getSessionData().getUser().getDivision());
				BigInteger userBitmask = Context.getSessionData().getDivisionBitmask();
				BigInteger bookingBitmask = userBitmask;

				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.SECOND, config.getInt(CONFIG_RESOURCE_HELD_DURATION, DEFAULT_RESOURCE_HELD_DURATION));
				booking.setExpiryTime(cal.getTime());

				String reference = SearchUtil.generateUniqueBookingReference();
				logger.debug("Booking reference: " + reference);
				booking.setReference(reference);

				// Verification Code for booking.
				Random rand = new Random();

				booking.setVerficationCode(String.format("%04d", rand.nextInt(10000)));

				// booking status
				HasCodeDAO<BookingStatus> bookingStatusDAO = daoFac.getBookingStatusDAO();
				BookingStatus tentative = bookingStatusDAO.get(BookingStatus.TENTATIVE);
				booking.setStatus(tentative);

				// booking client
				ClientDetail clientDetail = criteria.getClient();
				if (clientDetail == null) {
					return sendConfirmationError("Client details cannot be null!");
				}
				logger.debug("Creating Client Instance ");
				Client client = new Client();
				client.setName(clientDetail.getFirstName());
				client.setMobileTelephone(clientDetail.getMobile());
				client.setNic(clientDetail.getNic());
				client.setEmail(clientDetail.getEmail());
				daoFac.getClientDAO().persist(client);
				logger.debug("Persisted Client Instance ");
				booking.setClient(client);

				// booking user
				User user = Context.getSessionData().getUser();
				booking.setUser(user);

				// agent
				Agent agent;
				if (criteria.getAgentId() > 0) {
					agent = daoFac.getAgentDAO().get(criteria.getAgentId());
				} else {
					agent = user.getAgent();
				}
				booking.setAgent(agent);
				logger.debug("Create Passenger Instance ");
				// booking passengers
				List<PassengerDetail> passengers = criteria.getPassengers();
				if (passengers == null) {
					return sendConfirmationError("Passenger details cannot be null!");
				}
				int n = passengers.size();
				for (PassengerDetail passengerDetail : passengers) {
					Passenger passenger = createPassenger(passengerDetail, clientDetail);
					booking.addPassenger(passenger);
				}
				logger.debug("Persisted Passenger Instance ");
				// logger.debug("Booking passengers: " + booking.getPassengers());
				
				PaymentMethodCriteria paymentMethodCriteria = criteria.getPaymentMethodCriteria();

				// Added to compute charges per seat.
				double chargeable = 0d;
				double totalCost = 0d;
				double totalMarkup = 0d;
				double totalDiscounts = 0d;
				double totalTax = 0d;
				double totalCharges = 0d;
				double totalFare = 0d;
				logger.debug("Computing Rates Instance ");
				for (SeatAllocations allocations : criteria.getItemSeatAllocations()) {
					// find the corresponding held item
					BusHeldItem heldItem = getHeldItem(allocations, heldItems);
					if (heldItem == null) {
						return sendConfirmationError("No held item for index " + allocations.getHeldItemId());
					}

					// System.out.println(""+heldItem.);

					BookingItem bookingItem = new BookingItem();
					booking.addBookingItem(bookingItem);

					BigInteger busBitmask = heldItem.getSchedule().getBus().getDivision().getBitmask();
					// visible to all buses and booking user
					bookingBitmask = bookingBitmask.or(busBitmask);
					// visible to bus and booking user
					bookingItem.setAllowedDivisions(userBitmask.or(busBitmask));
					bookingItem.setWriteAllowedDivisions(userBitmask);

					// basic details
					bookingItem.setIndex(heldItem.getIndex());
					bookingItem.setSchedule(heldItem.getSchedule());
					bookingItem.setFromBusStop(heldItem.getFromBusStop());
					bookingItem.setToBusStop(heldItem.getToBusStop());
					bookingItem.setStatus(tentative);

					String resultIndex = heldItem.getResultIndex();
					int sectorIndex = heldItem.getSectorIndex();
					Map<String, ResultLeg> resultsMap = null;
					if (resultIndex.startsWith(Result.OUT_BOUND_PREFIX)) {
						resultsMap = outBoundResults;
					} else if (resultIndex.startsWith(Result.IN_BOUND_PREFIX)) {
						resultsMap = inBoundResults;
					}
					ResultLeg resultLeg = resultsMap.get(resultIndex);
					ResultSector sector = resultLeg.getSectors().get(sectorIndex);

					bookingItem.setFare(n * sector.getFare());
					if (paymentMethodCriteria == null
							|| paymentMethodCriteria.getPaymentMethod() != PaymentMethod.CashAtBus) {

						// cost price details
						bookingItem.setCost(n * sector.getCost());
						bookingItem.setGrossPrice(n * sector.getGrossPrice());
						bookingItem.setPriceBeforeTax(n * sector.getPriceBeforeTax());
						bookingItem.setPriceBeforeCharge(n * sector.getPriceBeforeCharges());
						bookingItem.setPrice(n * sector.getPrice());

						// update total chargeable amount for the booking
						chargeable += bookingItem.getPrice();

						// accounting entries
						String supplierAccount = sector.getSchedule().getBus().getSupplier().getAccountName();
						if (booking.isAccountable()) {
							IJournalEntry invInEntry = new SingleJournalEntry(
									"Cost of the seat for booking item " + bookingItem.getCode(), IJournalEntry.CoS,
									supplierAccount, bookingItem.getCost());
							AccountingManager.getInstance().record(invInEntry);

							List<JournalEntryFragment> fragments = new ArrayList<>();
							fragments.add(new JournalEntryFragment(IJournalEntry.RECEIVABLE, CrDr.Dr,
									bookingItem.getPriceBeforeCharge(),
									"Receivable for booking item " + bookingItem.getCode()));
							fragments.add(new JournalEntryFragment(IJournalEntry.REVENUE, CrDr.Cr,
									bookingItem.getPriceBeforeTax(),
									"Revenue for booking item " + bookingItem.getCode()));
							if (bookingItem.getPriceBeforeCharge() > bookingItem.getPriceBeforeTax()) {
								fragments.add(new JournalEntryFragment(IJournalEntry.SALES_TAX_PAYABLE, CrDr.Cr,
										bookingItem.getPriceBeforeCharge() - bookingItem.getPriceBeforeTax(),
										"Sales tax for booking item " + bookingItem.getCode()));
							}
							IJournalEntry revenueEntry = new CompoundJournalEntry(
									"Booking item " + bookingItem.getCode(),
									fragments.toArray(new JournalEntryFragment[0]));
							AccountingManager.getInstance().record(revenueEntry);

						} else {
							double commission = bookingItem.getPriceBeforeCharge() - bookingItem.getCost();
							if (commission > 0) {
								IJournalEntry commEntry = new SingleJournalEntry(
										"Commission for booking item " + bookingItem.getCode(), supplierAccount,
										IJournalEntry.COMMISSION_REVENUE, commission);
								AccountingManager.getInstance().record(commEntry);
							}
						}

						// markups
						Map<Integer, Boolean> isMargin = sector.getIsMargin();
						for (Entry<Integer, Double> entry : sector.getMarkups().entrySet()) {
							BookingItemMarkup markup = new BookingItemMarkup();
							markup.setMarkupSchemeId(entry.getKey());
							markup.setAmount(n * entry.getValue());
							markup.setIsMargin(isMargin.get(entry.getKey()));
							bookingItem.addMarkup(markup);
							totalMarkup += entry.getValue();
						}

						// discounts
						for (Entry<Integer, Double> entry : sector.getDiscounts().entrySet()) {
							BookingItemDiscount discount = new BookingItemDiscount();
							discount.setDiscountSchemeId(entry.getKey());
							discount.setAmount(n * entry.getValue());
							bookingItem.addDiscount(discount);
							totalDiscounts += entry.getValue();
						}

						// taxes
						for (Entry<Integer, Double> entry : sector.getTaxes().entrySet()) {
							BookingItemTax tax = new BookingItemTax();
							tax.setTaxSchemeId(entry.getKey());
							tax.setAmount(n * entry.getValue());
							bookingItem.addTax(tax);
							totalTax += entry.getValue();
						}

						// charges
						for (Entry<Integer, Double> entry : sector.getCharges().entrySet()) {
							BookingItemCharge charge = new BookingItemCharge();
							charge.setChargeSchemeId(entry.getKey());
							charge.setAmount(n * entry.getValue());
							bookingItem.addCharge(charge);
							totalCharges += entry.getValue();
						}
					}
				}
			
				// logger.debug("Booking items: " + booking.getBookingItems());

				booking.setChargeable(chargeable);
				booking.setAllowedDivisions(bookingBitmask);
				booking.setWriteAllowedDivisions(userBitmask);

				// save booking
				GenericDAO<Booking> bookingDAO = daoFac.getBookingDAO();
				logger.debug("Computed Rates Instance ");
				booking = bookingDAO.persist(booking);
				logger.debug("Persisting Booking Instance ");
				logger.debug("Booking saved. ID: " + booking.getId());

				// update the booking with seat allocations.
				GenericDAO<BookingItem> bookingItemDAO = daoFac.getBookingItemDAO();
				GenericDAO<BusHeldItem> heldBusItemDAO = daoFac.getHeldBusItemDAO();
				logger.debug("Creating BookingItem Instance ");
				for (BookingItem bookingItem : booking.getBookingItems()) {
					// find the corresponding held item
					BusHeldItem heldItem = null;
					for (BusHeldItem item : heldItems) {
						if (bookingItem.getIndex() == item.getIndex()) {
							heldItem = item;
						}
					}

					// seat allocations
					SeatAllocations allocations = criteria.getItemSeatAllocations(heldItem.getId());

					// below is a repeat of 687 - 696
					String resultIndex = heldItem.getResultIndex();
					int sectorIndex = heldItem.getSectorIndex();
					Map<String, ResultLeg> resultsMap = null;
					if (resultIndex.startsWith(Result.OUT_BOUND_PREFIX)) {
						resultsMap = outBoundResults;
					} else if (resultIndex.startsWith(Result.IN_BOUND_PREFIX)) {
						resultsMap = inBoundResults;
					}
					ResultLeg resultLeg = resultsMap.get(resultIndex);
					ResultSector sector = resultLeg.getSectors().get(sectorIndex);
					// End of repeat

					// Markup for all seats

					for (String seatNumber : heldItem.getSeatNumbers()) {
						int passengerIndex = allocations.getPassengerIndex(seatNumber);
						Passenger passenger = booking.getPassengerByIndex(passengerIndex);

						BookingItemPassenger pax = new BookingItemPassenger();
						pax.setPassenger(passenger);
						pax.setBookingItem(bookingItem);
						pax.setSeatNumber(seatNumber);
						pax.setStatus(tentative);
						pax.setPassengerType(passenger.getPassengerType());
						if (passenger.getPassengerType() == PassengerType.Child) {
							if (sector.getChildFare() <= 0) {
								return sendConfirmationError("Child Fare is Not Available for this Trip !");
							}
							pax.setSeatFare(sector.getChildFare());
							totalFare += sector.getChildFare();
							totalCost += sector.getChildCost();
						} else {
							pax.setSeatFare(sector.getFare());
							totalFare += sector.getFare();
							totalCost += sector.getCost();
						}
						pax.setGrossPrice(pax.getSeatFare() + totalMarkup);
						pax.setPriceBeforeTax(pax.getGrossPrice() - totalDiscounts);
						pax.setPriceBeforeCharge(pax.getPriceBeforeTax() + totalTax);
						pax.setPrice(pax.getPriceBeforeCharge() + totalCharges);
						bookingItem.addPassnger(pax);

					}
					// Compute total markup for all seats.
					totalMarkup *= heldItem.getSeatNumbers().size();
					
					// Compute total discount for all seats.
					totalDiscounts *= heldItem.getSeatNumbers().size();
					
					// Compute total tax for all seats.
					totalTax *= heldItem.getSeatNumbers().size();
					
					// Compute total charges for all seats.
					totalCharges *= heldItem.getSeatNumbers().size();
					
					// set total fare for booking item
					bookingItem.setFare(totalFare);
					bookingItem.setCost(totalCost);
					bookingItem.setGrossPrice(totalFare + totalMarkup);
					bookingItem.setPriceBeforeTax((totalFare + totalMarkup) - totalDiscounts);
					bookingItem.setPriceBeforeCharge(((totalFare + totalMarkup) - totalDiscounts) + totalTax);
					bookingItem.setPrice((((totalFare + totalMarkup) - totalDiscounts) + totalTax) + totalCharges);
					// Save booking item record
					bookingItemDAO.persist(bookingItem);

					// Set Booking Chargeable
					booking.setChargeable(bookingItem.getPrice());
					logger.debug("Persisting BookingItem Instance ");
					booking = bookingDAO.persist(booking);
					logger.debug("Done  BookingItem Instance ");
					logger.debug("Pax for booking item (index) " + bookingItem.getIndex() + ": "
							+ bookingItem.getPassengers());

					// remove held item, which cascades to held item seats
					heldBusItemDAO.delete(heldItem);
				}

				// invalidate any one-time discount code involved
				String discountCode = searchCriteria.getDiscountCode();
				if (discountCode != null && !discountCode.isEmpty()) {
					DiscountCode ex = new DiscountCode();
					ex.setCode(discountCode);
					GenericDAO<DiscountCode> discountCodeDAO = daoFac.getDiscountCodeDAO();
					DiscountCode code = discountCodeDAO.findUnique(ex);
					if (code != null) {
						code.setActive(false);
						discountCodeDAO.persist(code);
					}
				}
				
				ConfirmResponse confirmResponse = new ConfirmResponse(booking);
				logger.debug("Creating Payment Record ");
				// handle payment method
				if (paymentMethodCriteria != null) {
					handlePaymentMethod(bookingDAO, booking, paymentMethodCriteria);
				}

				// add payment
				List<PaymentCriteria> paymentCriteria = criteria.getPaymentCriteria();
				paymentCriteria.removeAll(Collections.singleton(null));
				if (paymentCriteria.size() > 0) {
					for (PaymentCriteria paymentCriterion : paymentCriteria) {
						paymentCriterion.setBookingId(booking.getId());

						// Set default pay option as Cash

						logger.debug("Default Booking pay option is set to  == " + booking.getBookingPayOption());

						if ((paymentCriterion.getVendorMode() != null)
								&& (VendorPaymentRefundMode.Warrant.equals(paymentCriterion.getVendorMode()))
								|| (VendorPaymentRefundMode.Pass.equals(paymentCriterion.getVendorMode()))
								|| (VendorPaymentRefundMode.PayAtBus.equals(paymentCriterion.getVendorMode()))) {

							switch (paymentCriterion.getVendorMode()) {
							case Warrant:
								booking.setBookingPayOption(VendorPaymentRefundMode.Warrant.toString());
								break;
							case Pass:
								booking.setBookingPayOption(VendorPaymentRefundMode.Pass.toString());
								break;
							case PayAtBus:
								booking.setBookingPayOption(VendorPaymentRefundMode.PayAtBus.toString());
								break;
							default:
								booking.setBookingPayOption(VendorPaymentRefundMode.Cash.toString());
								break;
							}

							// If payment mode is Warrant,Pass or Pay at bus, set the amount to total fare.
							paymentCriterion.setAmount(totalFare);
						}

						if ((null != paymentCriterion.getVendorMode()) && (null == booking.getBookingPayOption())) {
							booking.setBookingPayOption(paymentCriterion.getVendorMode().toString());
						} else if ((null == paymentCriterion.getVendorMode())
								&& (null == booking.getBookingPayOption())) {
							booking.setBookingPayOption(paymentCriterion.getMode().toString());
						}
						logger.debug(" Booking pay option set to  == " + booking.getBookingPayOption());
						logger.debug("Persisting Payment Record ");
						PaymentRefundsResponse paymentResponse = addPaymentRefund(paymentCriterion);
						logger.debug("Done Payment Record ");
						if (paymentResponse.getStatus() < 0) {
							return new ConfirmResponse(paymentResponse.getMessage());
						}
						confirmResponse.setPaymentResponse(paymentResponse);
					}

					// paymentCriteria.setBookingId(booking.getId());
					// PaymentRefundsResponse paymentResponse = addPaymentRefund(paymentCriteria);
					// if (paymentResponse.getStatus() < 0) {
					// return new ConfirmResponse(paymentResponse.getMessage());
					// }
					// confirmResponse.setPaymentResponse(paymentResponse);
				}

				logger.debug("Confirmation Reponse: " + confirmResponse);

				BookingChangeEvent event = new BookingChangeEvent(booking, BookingChangeType.BOOKING_CONFIRM);
				BookingMatrix.getInstance().fireScheduleChangeEvent(event);
				logger.debug("Returning Booking confirmation");
				return confirmResponse;

			} catch (Exception e) {
				logger.error("Exception while confirming", e);
				HibernateUtil.rollback();
				return new ConfirmResponse("Exception while confirming");
			}
		// } // Sync End
	}

	private BusHeldItem getHeldItem(SeatAllocations allocations, List<BusHeldItem> heldItems) {
		int heldItemId = allocations.getHeldItemId();
		BusHeldItem heldItem = null;
		for (BusHeldItem item : heldItems) {
			if (item.getId() == heldItemId) {
				heldItem = item;
			}
		}
		return heldItem;
	}

	private ConfirmResponse sendConfirmationError(String errorMessage) {
		logger.debug("Confirmation Reponse: " + errorMessage);
		HibernateUtil.rollback();
		return new ConfirmResponse(errorMessage);
	}

	private Passenger createPassenger(PassengerDetail detail, ClientDetail clientDetail) {
		Passenger passenger = new Passenger();

		String name = null;
		if (detail.getFirstName() != null && !detail.getFirstName().isEmpty()) {
			name = detail.getFirstName();
		} else {
			name = clientDetail.getFirstName();
		}
		passenger.setName(name);

		passenger.setNic(detail.getNic());
		passenger.setGender(detail.getGender());
//		passenger.setEmails(detail.getEmail());
		if (detail.getEmail()  != null && !detail.getEmail().isEmpty()) {
			passenger.setEmails(detail.getEmail());
		} else {
			passenger.setEmails(clientDetail.getEmail());
		}
		
		String mobile = null;
		if (detail.getMobile() != null && !detail.getMobile().isEmpty()) {
			mobile = detail.getMobile();
		} else {
			mobile = clientDetail.getMobile();
		}
		
		
		passenger.setMobileTelephone(mobile);

		passenger.setIndex(detail.getIndex());
		passenger.setPassengerType(detail.getPassengerType());

		return passenger;
	}
}
