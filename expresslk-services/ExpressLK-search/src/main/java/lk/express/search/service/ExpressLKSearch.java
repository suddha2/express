package lk.express.search.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import lk.express.AvailabilityCriteria;
import lk.express.AvailabilityResponse;
import lk.express.ConfirmResponse;
import lk.express.ConfirmationCriteria;
import lk.express.ExpResponse;
import lk.express.HoldCriteria;
import lk.express.HoldResponse;
import lk.express.JourneyPerformedCriteria;
import lk.express.PaymentRefundCriteria;
import lk.express.PaymentRefundsResponse;
import lk.express.ReleaseResponse;
import lk.express.RepriceCriteria;
import lk.express.ResultLeg;
import lk.express.SearchCriteria;
import lk.express.SearchResponse;
import lk.express.SessionCriteria;
import lk.express.bean.Entity;
import lk.express.cnx.CancellationChargeResponse;
import lk.express.cnx.CancellationCriteria;
import lk.express.cnx.CancellationResponse;

@WebService
@SOAPBinding(style = Style.RPC)
public interface ExpressLKSearch {

	/**
	 * Save an {@link Entity} with the system.
	 * 
	 * @param t
	 *            entity to be saved
	 * @return entity with assigned id
	 */
	@WebMethod
	<T extends Entity> T save(@WebParam(name = "t") T t);

	/**
	 * Save a list of entities with the system.
	 * 
	 * @param list
	 *            list of entity to be saved
	 * @return list of entities with assigned ids
	 */
	@WebMethod
	<T extends Entity> T[] saveList(@WebParam(name = "list") T[] list);

	/**
	 * Checks the health of the service
	 * 
	 * @return {@code true} if healthy, {@code false} otherwise
	 */
	@WebMethod
	boolean heartBeat();

	/**
	 * Creates a new search session.
	 * 
	 * @param criteria
	 *            session criteria
	 * @return search session id
	 */
	@WebMethod
	String createSession(@WebParam(name = "sessionCriteria") SessionCriteria criteria);

	/**
	 * Refresh the search session
	 * 
	 * @param sessionId
	 *            search session id
	 * @return
	 */
	@WebMethod
	ExpResponse<Void> refreshSession(@WebParam(name = "sessionId") String sessionId);

	/**
	 * Search for a given search criteria.
	 * 
	 * @param sessionId
	 *            search session id
	 * @param criteria
	 *            search criteria
	 * @return the search result
	 */
	@WebMethod
	SearchResponse search(@WebParam(name = "sessionId") String sessionId,
			@WebParam(name = "criteria") SearchCriteria criteria);

	/**
	 * Filter the search results
	 * 
	 * @param sessionId
	 *            search session id
	 * @param criteria
	 *            search criteria with filters
	 * @return the search result
	 */
	@WebMethod
	SearchResponse filter(@WebParam(name = "sessionId") String sessionId,
			@WebParam(name = "criteria") SearchCriteria criteria);

	/**
	 * Check availability for a given criteria
	 * 
	 * @param sessionId
	 *            search session id
	 * @param availability
	 *            availability criteria
	 * @return availability details
	 */
	@WebMethod
	AvailabilityResponse checkAvailability(@WebParam(name = "sessionId") String sessionId,
			@WebParam(name = "criteria") AvailabilityCriteria criteria);

	/**
	 * Re-prices a results leg by possibly changing certain parameters of the
	 * search criteria
	 * 
	 * @param criteria
	 *            re-pricing criteria
	 * @return re-priced results leg
	 */
	@WebMethod
	ExpResponse<ResultLeg> reprice(@WebParam(name = "sessionId") String sessionId,
			@WebParam(name = "repriceCriteria") RepriceCriteria criteria);

	/**
	 * Hold the result for a given criteria
	 * 
	 * @param sessionId
	 *            search session id
	 * @param criteria
	 *            holding criteria
	 * @return <code>HoldResult</code> containing <code>AdvisoryNotice</code>s.
	 */
	@WebMethod
	HoldResponse hold(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "criteria") HoldCriteria criteria);

	/**
	 * Releases one or more held items
	 * 
	 * @param itemIds
	 *            items IDs of held items
	 * @return <code>ReleaseResponse</code> indicating the outcome of the
	 *         operation
	 */
	@WebMethod
	ReleaseResponse release(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "itemIds") int[] itemIds);

	/**
	 * Confirms a booking item with the search.
	 * 
	 * @param sessionId
	 *            search session id
	 * @param criteria
	 *            confirmation criteria
	 * @return <code>Booking</code> containing all the confirmed
	 *         <code>BookingItem</code>s.
	 */
	@WebMethod
	ConfirmResponse confirm(@WebParam(name = "sessionId") String sessionId,
			@WebParam(name = "criteria") ConfirmationCriteria criteria);

	/**
	 * Calculates the cancellation charges.
	 * 
	 * @param criteria
	 *            cancellation criteria
	 * @return cancellation charges.
	 */
	@WebMethod
	CancellationChargeResponse calculateCancellationCharge(@WebParam(name = "criteria") CancellationCriteria criteria);

	/**
	 * Cancels a set of booking items.
	 * 
	 * @param criteria
	 *            cancellation criteria
	 * @return <code>CancellationResponse</code> containing the status of
	 *         cancellations.
	 */
	@WebMethod
	CancellationResponse cancel(@WebParam(name = "criteria") CancellationCriteria criteria);

	/**
	 * Do payments or refunds for the booking
	 * 
	 * @param criteria
	 */
	@WebMethod
	PaymentRefundsResponse addPaymentRefund(@WebParam(name = "criteria") PaymentRefundCriteria criteria);

	/**
	 * Marks as journey performed for a particular passenger for a particular
	 * schedule
	 * 
	 * @param criteria
	 *            journey performed criteria
	 * @return response containing the state
	 */
	@WebMethod
	ExpResponse<Void> markJourneyPerformed(@WebParam(name = "criteria") JourneyPerformedCriteria criteria);
}
