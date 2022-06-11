package lk.express.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lk.express.AvailabilityCriteria;
import lk.express.AvailabilityResponse;
import lk.express.ConfirmResponse;
import lk.express.ConfirmationCriteria;
import lk.express.Context;
import lk.express.DiscountMarkupTaxManager;
import lk.express.ExpResponse;
import lk.express.HoldCriteria;
import lk.express.HoldCriteria.SeatCriteria;
import lk.express.HoldResponse;
import lk.express.JourneyPerformedCriteria;
import lk.express.LegCriteria;
import lk.express.LegResult;
import lk.express.MarkupManager;
import lk.express.PaymentMethodCriteria;
import lk.express.PaymentRefundCriteria;
import lk.express.PaymentRefundsResponse;
import lk.express.PricingManager;
import lk.express.ReleaseResponse;
import lk.express.RepriceCriteria;
import lk.express.Result;
import lk.express.ResultLeg;
import lk.express.ResultSector;
import lk.express.ResultWrapper;
import lk.express.SearchCriteria;
import lk.express.SearchResponse;
import lk.express.SearchResult;
import lk.express.accounting.AccountingManager;
import lk.express.accounting.IJournalEntry;
import lk.express.accounting.SingleJournalEntry;
import lk.express.bean.BusHeldItem;
import lk.express.bean.City;
import lk.express.bean.Coupon;
import lk.express.bean.Payment;
import lk.express.bean.PaymentRefund;
import lk.express.bean.PaymentRefund.PaymentRefundType;
import lk.express.bean.PaymentRefundMode;
import lk.express.bean.Refund;
import lk.express.bean.VendorPaymentRefundMode;
import lk.express.cnx.BookingCancellationWrapper;
import lk.express.cnx.BookingItemCancellation;
import lk.express.cnx.CancellationCause;
import lk.express.cnx.CancellationChargeResponse;
import lk.express.cnx.CancellationChargeResponse.BookingItemCancellationData;
import lk.express.cnx.CancellationCriteria;
import lk.express.cnx.CancellationManager;
import lk.express.cnx.CancellationResponse;
import lk.express.config.Configuration;
import lk.express.config.ConfigurationManager;
import lk.express.db.HibernateUtil;
import lk.express.db.dao.DAOFactory;
import lk.express.db.dao.GenericDAO;
import lk.express.db.dao.HasCodeDAO;
import lk.express.db.dao.HasValidPeriodDAO;
import lk.express.reservation.Booking;
import lk.express.reservation.BookingItem;
import lk.express.reservation.BookingItemPassenger;
import lk.express.reservation.BookingStatus;
import lk.express.rule.Scheme;
import lk.express.rule.bean.RuleCancellationRule;
import lk.express.schedule.BusSchedule;
import lk.express.suggest.CitySuggester;
import lk.express.suggest.SpellCorrectingCitySuggester;

public class SearchGateway implements ISearchGateway, Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(SearchGateway.class);

	protected static final Configuration config = ConfigurationManager.getConfiguration();
	protected static final DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);

	private static final int DEFAULT_DEFERRED_HELD_DURATION = 24 * 60 * 60; // seconds
	private static final String CONFIG_DEFERRED_HELD_DURATION = "DEFERRED_HELD_DURATION";

	protected SearchCriteria searchCriteria;
	protected Map<String, ResultLeg> outBoundResults = new LinkedHashMap<String, ResultLeg>();
	protected Map<String, ResultLeg> inBoundResults = new LinkedHashMap<String, ResultLeg>();

	/**
	 * Search for a given search criteria.
	 * 
	 * @param criteria
	 *            search criteria
	 * @return the search result
	 */
	@Override
	public SearchResponse search(SearchCriteria criteria) {
		return null;
	}

	protected void fillSearchCriteria(SearchCriteria criteria) {
		CitySuggester suggester = new SpellCorrectingCitySuggester();

		if (criteria.getFromCityId() <= 0 && !criteria.getFromCity().isEmpty()) {
			List<City> departure = suggester.suggestDepartures(criteria.getFromCity(), 0, 1);
			if (!departure.isEmpty()) {
				criteria.setFromCityId(departure.get(0).getId());
			}
		}

		if (criteria.getToCityId() <= 0 && !criteria.getToCity().isEmpty()) {
			List<City> destination = null;
			if (criteria.getFromCityId() > 0) {
				destination = suggester.suggestDestinations(criteria.getFromCityId(), criteria.getToCity(), 0, 1);
			} else if (!criteria.getFromCity().isEmpty()) {
				destination = suggester.suggestDestinations(criteria.getFromCity(), criteria.getToCity(), 0, 1);
			}

			if (!destination.isEmpty()) {
				criteria.setToCityId(destination.get(0).getId());
			}
		}
	}

	protected PricingManager getPricingManager() {

		DiscountMarkupTaxManager discountManager = new DiscountMarkupTaxManager(
				daoFac.getDiscountRuleDAO().findValid());
		DiscountMarkupTaxManager markupManager = new MarkupManager(daoFac.getMarkupRuleDAO().findValid());
		DiscountMarkupTaxManager taxManager = new DiscountMarkupTaxManager(daoFac.getTaxRuleDAO().findValid());
		DiscountMarkupTaxManager chargeManager = new DiscountMarkupTaxManager(daoFac.getChargeRuleDAO().findValid());
		PricingManager pricingManager = new PricingManager(markupManager, discountManager, taxManager, chargeManager);

		return pricingManager;
	}

	/**
	 * Filter the search results
	 * 
	 * @param criteria
	 *            search criteria with filters
	 * @return the search result
	 */
	@Override
	public SearchResponse filter(SearchCriteria criteria) {
		SearchResult result = new SearchResult();

		// outbound leg
		LegCriteria outCriteria = criteria.getLegCriterion()[0];
		List<ResultLeg> outFiltered = outBoundResults.values().stream().filter(outCriteria.getFilters().stream()
				.<Predicate<ResultLeg>>map(f -> f::test).reduce(Predicate::and).orElse(t -> true))
				.collect(Collectors.toList());
		result.setOutBoundLegResult(new LegResult(outFiltered));

		// inbound leg
		if (criteria.getLegCriterion().length > 1 && criteria.getLegCriterion()[1] != null) {
			LegCriteria inCriteria = criteria.getLegCriterion()[1];
			List<ResultLeg> inFiltered = inBoundResults.values().stream().filter(inCriteria.getFilters().stream()
					.<Predicate<ResultLeg>>map(f -> f::test).reduce(Predicate::and).orElse(t -> true))
					.collect(Collectors.toList());
			result.setInBoundLegResult(new LegResult(inFiltered));
		}

		return new SearchResponse(result);
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
		return null;
	}

	protected ResultSector getResultSector(String resultIndex, int sectorIndex) {
		ResultLeg leg = getResultLeg(resultIndex);
		return leg.getSectors().get(sectorIndex);
	}

	protected ResultLeg getResultLeg(String resultIndex) {
		Map<String, ResultLeg> resultsMap = null;
		if (resultIndex.startsWith(Result.OUT_BOUND_PREFIX)) {
			resultsMap = outBoundResults;
		} else if (resultIndex.startsWith(Result.IN_BOUND_PREFIX)) {
			resultsMap = inBoundResults;
		}

		return resultsMap.get(resultIndex);
	}

	/**
	 * Re-prices a results leg by possibly with a slightly different version of
	 * search criteria. Calling this method does not affect the search criteria or
	 * search results stored in the session.
	 * 
	 * @param criteria
	 *            re-pricing criteria
	 * @return re-priced results leg
	 */
	@Override
	public ExpResponse<ResultLeg> reprice(RepriceCriteria criteria) {
		try {
			PricingManager pricingManager = getPricingManager();
			SearchCriteria newCriteria = criteria.updateSearchCriteria(searchCriteria);

			String resultIndex = criteria.getResultIndex();
			ResultLeg resultLeg = getResultLeg(resultIndex);
			ResultLeg clonedResultLeg = (ResultLeg) SerializationUtils.clone(resultLeg);

			for (ResultSector sector : clonedResultLeg.getSectors()) {
				pricingManager.price(new ResultWrapper(newCriteria, sector));
			}

			return new ExpResponse<ResultLeg>(clonedResultLeg);
		} catch (Exception e) {
			logger.error("Exception while repricing", e);
			HibernateUtil.rollback();
			return new ExpResponse<ResultLeg>("Exception while repricing!");
		}
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
		return null;
	}

	protected BusHeldItem hold(HoldCriteria criteria, BusSchedule schedule, int sectorIndex, boolean lastSector)
			throws InterruptedException {
		return null;
	}

	/**
	 * Releases one or more held items
	 * 
	 * @param itemIds
	 *            items IDs of held items
	 */
	@Override
	public ReleaseResponse release(List<Integer> itemIds) {
		return null;
	}

	/**
	 * Confirms a booking item with the search gateway.
	 * 
	 * @param criteria
	 *            confirmation criteria
	 * @return <code>Booking</code> containing all the confirmed
	 *         <code>BookingItem</code>s.
	 */
	@Override
	public ConfirmResponse confirm(ConfirmationCriteria criteria) {
		return null;
	}

	/**
	 * Calculates the cancellation charges.
	 * 
	 * @param criteria
	 *            cancellation criteria
	 * @return cancellation charges.
	 */
	@Override
	public CancellationChargeResponse calculateCancellationCharge(CancellationCriteria criteria) {
		try {
			HasValidPeriodDAO<RuleCancellationRule> cancellationRuleDAO = daoFac.getCancellationRuleDAO();
			List<RuleCancellationRule> cancellationRules = cancellationRuleDAO.findValid();
			CancellationManager cnxManager = new CancellationManager(cancellationRules);
			List<BookingItemCancellation> charges = cnxManager.calculateCancellationCharge(criteria);
			BookingItemCancellationData data = new BookingItemCancellationData(charges);
			CancellationChargeResponse resp = new CancellationChargeResponse(data);
			return resp;
		} catch (Exception e) {
			logger.error("Exception while calculating cancellation charge", e);
			HibernateUtil.rollback();
			return new CancellationChargeResponse("Exception while calculating cancellation charge!");
		}
	}

	/**
	 * Cancels a set of booking items.
	 * 
	 * @param criteria
	 *            cancellation criteria
	 * @return <code>CancellationResponse</code> containing the status of
	 *         cancellations.
	 */
	@Override
	public CancellationResponse cancel(CancellationCriteria criteria) {
		try {
			HasValidPeriodDAO<RuleCancellationRule> cancellationRuleDAO = daoFac.getCancellationRuleDAO();
			List<RuleCancellationRule> cancellationRules = cancellationRuleDAO.findValid();
			CancellationManager cnxManager = new CancellationManager(cancellationRules);
			Booking booking = cnxManager.cancel(criteria);
			CancellationResponse response = null;
			if (booking == null) {
				response = new CancellationResponse(ExpResponse.FAIL);
			} else {
				response = new CancellationResponse(booking);
			}
			return response;
		} catch (Exception e) {
			logger.error("Exception while cancelling", e);
			HibernateUtil.rollback();
			return new CancellationResponse("Exception while cancelling!");
		}
	}

	/**
	 * Handles recording the payment method in the booking. Applicable when the
	 * payment is deferred and we need to extends the expiry time
	 * 
	 * @param bookingDAO
	 *            booking DAO
	 * @param booking
	 *            the booking
	 * @param criteria
	 *            payment method criteria
	 */
	protected void handlePaymentMethod(GenericDAO<Booking> bookingDAO, Booking booking,
			PaymentMethodCriteria criteria) {

		switch (criteria.getPaymentMethod()) {
		case Deferred:
			Date expiryTime = null;
			if (criteria.getHoldUntil() != null) {
				expiryTime = criteria.getHoldUntil();
			} else {
				int heldDuration = config.getInt(CONFIG_DEFERRED_HELD_DURATION, DEFAULT_DEFERRED_HELD_DURATION);
				Calendar cal = Calendar.getInstance();
				cal.setTime(booking.getBookingTime());
				cal.add(Calendar.SECOND, heldDuration);
				expiryTime = cal.getTime();

				if (expiryTime.after(booking.getDepartureTime())) {
					expiryTime = booking.getDepartureTime();
				}
			}
			booking.setExpiryTime(expiryTime);
			bookingDAO.persist(booking);
			break;
		// case CashAtBus:
		// triggerPaymentAction(bookingDAO, booking);
		// break;
		default:
			break;
		}
	}

	/**
	 * Payments or Refunds for a booking
	 * 
	 * @param criteria
	 *            payment refund criteria
	 * @return response containing the state
	 */
	@Override
	public PaymentRefundsResponse addPaymentRefund(PaymentRefundCriteria criteria) {
		try {

			PaymentRefundsResponse response = new PaymentRefundsResponse();

			GenericDAO<Booking> bookingDAO = daoFac.getBookingDAO();
			Booking booking = bookingDAO.get(criteria.getBookingId());

			if (PaymentRefundMode.Coupon == criteria.getMode()) {
				GenericDAO<Coupon> couponDAO = daoFac.getCouponDAO();

				if (PaymentRefundType.Payment == criteria.getType()) {
					Coupon coupon = couponDAO.findUnique(new Coupon(criteria.getReference()));
					if (coupon != null && coupon.isValid()) {
						if (coupon.getAmount() >= criteria.getAmount()) {
							// we are using the coupon
							coupon.setActive(false);
							couponDAO.persist(coupon);

							if (coupon.getAmount() > criteria.getAmount()) {
								Coupon newCoupon = Coupon.createCoupon(coupon.getAmount() - criteria.getAmount(),
										booking.getClient().getId());
								response.setCoupon(newCoupon);
							}
						} else {
							return new PaymentRefundsResponse("Insufficient coupon value");
						}
					} else {
						return new PaymentRefundsResponse("Invalid coupon number");
					}
				} else {
					Coupon newCoupon = Coupon.createCoupon(criteria.getAmount(), booking.getClient().getId());
					response.setCoupon(newCoupon);
				}
			}

			PaymentRefund paymentRefund = null;
			if (criteria.getType().equals(PaymentRefundType.Payment)) {

				/**
				 * FIXME Below comment is not always true. Move this code to a separate method
				 */
				// Do not add payment for cancelled bookings. Resources for
				// cancelled bookings are made available to other searches and
				// might not be available now.
				if (booking.getStatus().getCode().equals(BookingStatus.CANCELLED)) {

					// This is a booking previously cancelled due to non payment
					if (CancellationCause.NonPayment.equals(booking.getCancellationCause())) {

						List<Integer> heldItemIds = new ArrayList<>();
						// check if the seats are still available for all
						// booking items (by holding them)
						for (BookingItem item : booking.getBookingItems()) {

							HoldCriteria holdCriteria = new HoldCriteria();
							holdCriteria.setBoardingPoint(item.getFromBusStop().getId());
							holdCriteria.setDroppingPoint(item.getToBusStop().getId());
							holdCriteria.setResultIndex("");

							SeatCriteria seatCriteria = new SeatCriteria();
							// FIXME fix this for multi sector bookings
							seatCriteria.setSectorNumber(0);
							seatCriteria.setSeats(item.getPassengers().stream().map(BookingItemPassenger::getSeatNumber)
									.collect(Collectors.toList()));
							holdCriteria.setSeatCriterias(Arrays.asList(seatCriteria));

							// FIXME fix this for multi sector bookings
							BusHeldItem heldItem = hold(holdCriteria, item.getSchedule(), 0, true);
							if (heldItem == null) {
								// release items held so far
								release(heldItemIds);

								response.setMessage("Cannot add payments for cancelled bookings.");
								response.setStatus(PaymentRefundsResponse.PAYMENT_FOR_CANCELLED_BOOKING);
								return response;
							} else {
								heldItemIds.add(heldItem.getId());
							}
						}

						// We could hold all the items of the bookings. Now
						// reverse the cancellation of the bookings to add
						// payment.
						CancellationManager manager = new CancellationManager(
								(Scheme<BookingCancellationWrapper>) null);
						manager.reopenBooking(booking, BookingStatus.TENTATIVE,
								"Reopening the booking with the payment");
					} else {
						response.setMessage("Cannot add payments for cancelled bookings.");
						response.setStatus(PaymentRefundsResponse.PAYMENT_FOR_CANCELLED_BOOKING);
						return response;
					}
				}

				Payment payment = new Payment();
				payment.setAllowedDivisions(booking.getAllowedDivisions());
				payment.setWriteAllowedDivisions(Context.getSessionData().getUser().getDivisionsBitmask());
				payment.setUserId(Context.getSessionData().getUser().getId());
				payment.setBookingId(criteria.getBookingId());

				booking.getPayments().add(payment);
				paymentRefund = payment;

				// accounting entry for payment
				if (!PaymentRefundMode.Vendor.equals(criteria.getMode())) {
					IJournalEntry customerPaymentEntry = new SingleJournalEntry(
							"Payment for booking " + booking.getReference(), criteria.getMode().getAccountName(),
							IJournalEntry.RECEIVABLE, criteria.getAmount());
					AccountingManager.getInstance().record(customerPaymentEntry);
				}

			} else {
				Refund refund = new Refund();
				refund.setAllowedDivisions(booking.getAllowedDivisions());
				refund.setWriteAllowedDivisions(Context.getSessionData().getUser().getDivisionsBitmask());
				refund.setUserId(Context.getSessionData().getUser().getId());
				refund.setBookingId(criteria.getBookingId());

				booking.getRefunds().add(refund);
				paymentRefund = refund;

				// accounting entry for refund
				if (!PaymentRefundMode.Vendor.equals(criteria.getMode())) {
					IJournalEntry customerPaymentEntry = new SingleJournalEntry(
							"Refund for booking " + booking.getReference(), IJournalEntry.RECEIVABLE,
							criteria.getMode().getAccountName(), criteria.getAmount());
					AccountingManager.getInstance().record(customerPaymentEntry);
				}
			}

			paymentRefund.setAmount(criteria.getAmount());
			paymentRefund.setActualAmount(criteria.getActualAmount());
			paymentRefund.setActualCurrency(criteria.getActualCurrency());
			paymentRefund.setMode(criteria.getMode());
			paymentRefund.setVendorMode(criteria.getVendorMode());
			paymentRefund.setTime(new Date());
			paymentRefund.setReference(criteria.getReference());

			response.setData(paymentRefund);

			// persist the booking which will persist payment or refund
			bookingDAO.persist(booking);

			if (null == booking.getBookingPayOption()) {
				if (!(null == criteria.getVendorMode())) {
					booking.setBookingPayOption(criteria.getVendorMode().toString());
				}else if (!(null == criteria.getMode())) {
					booking.setBookingPayOption(criteria.getMode().toString());
				}
			}

			// trigger additional actions for payment
			if (criteria.getType().equals(PaymentRefundType.Payment)) {
				triggerPaymentAction(bookingDAO, booking);
			}

			return response;

		} catch (Exception e) {
			logger.error("Exception while doing the payment", e);
			HibernateUtil.rollback();
			return new PaymentRefundsResponse("Error while doing the payment");
		}
	}

	/**
	 * This method is triggered by a successful payment or a refund. Handles things
	 * such as marking tentative bookings as confirmed upon reaching full payment
	 * etc.
	 * 
	 * @param bookingDAO
	 * @param booking
	 * 
	 * @FIXME code duplicated in
	 *        {@link PaymentService#triggerPaymentAction(GenericDAO, Booking)
	 *        triggerPaymentAction of PaymentService}
	 */
	protected void triggerPaymentAction(GenericDAO<Booking> bookingDAO, Booking booking) {
		logger.debug("triggerPaymentAction Method start " + booking.getBookingPayOption());
		if (booking.getChargeable() <= booking.getTotalPayment()) {
			HasCodeDAO<BookingStatus> bookingStatusDAO = daoFac.getBookingStatusDAO();
			BookingStatus confirmed = bookingStatusDAO.get(BookingStatus.CONFIRMED);
			boolean persist = false;

			if (booking.getStatus().getCode().equals(BookingStatus.TENTATIVE)) {
				booking.setStatus(confirmed);
				persist = true;
			}

			for (BookingItem item : booking.getBookingItems()) {
				if (item.getStatus().getCode().equals(BookingStatus.TENTATIVE)) {
					item.setStatus(confirmed);
					persist = true;
				}
				for (BookingItemPassenger pax : item.getPassengers()) {
					if (pax.getStatus().getCode().equals(BookingStatus.TENTATIVE)) {
						pax.setStatus(confirmed);
						persist = true;
					}
					logger.debug(pax.getSeatNumber() + " Payment option = " + booking.getBookingPayOption());
					pax.setFarePaymentOption(VendorPaymentRefundMode.valueOf(booking.getBookingPayOption()));
				}
			}

			if (persist) {
				bookingDAO.persist(booking);
			}
		}
	}

	/**
	 * Marks as journey performed for a particular passenger for a particular
	 * schedule
	 * 
	 * @param criteria
	 *            journey performed criteria
	 * @return response containing the state
	 */
	@Override
	public ExpResponse<Void> markJourneyPerformed(JourneyPerformedCriteria criteria) {
		try {
			String hql = "FROM BookingItemPassenger WHERE seatNumber = :seatNumber "
					+ "AND bookingItem.schedule.id = :scheduleId AND bookingItem.status.code = :conf";
			BookingItemPassenger passenger = (BookingItemPassenger) HibernateUtil.getCurrentSession().createQuery(hql)
					.setParameter("seatNumber", criteria.getSeatNumber())
					.setParameter("scheduleId", criteria.getScheduleId()).setParameter("conf", BookingStatus.CONFIRMED)
					.uniqueResult();

			if (passenger != null) {
				passenger.setJourneyPerformed(criteria.isJourneyPerformed());
				daoFac.getBookingItemPassengerDAO().persist(passenger);
				return new ExpResponse<Void>();
			} else {
				return new ExpResponse<Void>("Could not find passenger!");
			}
		} catch (Exception e) {
			logger.error("Exception while marking journey performed", e);
			HibernateUtil.rollback();
			return new ExpResponse<Void>("Exception while marking journey as performed!");
		}
	}
}
