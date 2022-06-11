package lk.express.search.service;

import java.util.ArrayList;
import java.util.List;

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import lk.express.AvailabilityCriteria;
import lk.express.AvailabilityResponse;
import lk.express.ConfirmResponse;
import lk.express.ConfirmationCriteria;
import lk.express.Context;
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
import lk.express.Session;
import lk.express.SessionCriteria;
import lk.express.SessionData;
import lk.express.SessionManager;
import lk.express.cnx.CancellationChargeResponse;
import lk.express.cnx.CancellationCriteria;
import lk.express.cnx.CancellationResponse;
import lk.express.search.GatewayFactory;
import lk.express.search.ISearchGateway;
import lk.express.search.SearchSession;
import lk.express.search.SearchSessionListener;
import lk.express.search.SearchType;
import lk.express.service.WSService;

import org.slf4j.MDC;

@HandlerChain(file = "../../../../handler_chains.xml")
@WebService(endpointInterface = "lk.express.search.service.ExpressLKSearch")
public class ExpressLKSearchImpl extends WSService implements ExpressLKSearch {

	private SessionManager sessionManager;

	public ExpressLKSearchImpl() {
		sessionManager = new SessionManager();
		sessionManager.addSessionListener(new SearchSessionListener());
	}

	/**
	 * Creates a new search session.
	 * 
	 * @param criteria
	 *            session criteria
	 * @return search session ID
	 */
	@Override
	@WebMethod
	public String createSession(@WebParam(name = "sessionCriteria") SessionCriteria criteria) {
		SearchSession session = sessionManager.createSession(null, SearchSession.class);
		if (session != null) {
			session.setLocale(criteria.getLocale());
			return session.getId();
		}
		return null;
	}

	/**
	 * Refresh the search session
	 * 
	 * @param sessionId
	 *            search session id
	 * @return
	 */
	@Override
	@WebMethod
	public ExpResponse<Void> refreshSession(@WebParam(name = "sessionId") String sessionId) {
		SearchSession session = getSearchSession(sessionId);
		if (session != null) {
			return new ExpResponse<>();
		} else {
			return new ExpResponse<>(ExpResponse.SESSION_EXPIRED);
		}
	}

	/**
	 * Search for a given search criteria.
	 * 
	 * @param sessionId
	 *            search session id
	 * @param criteria
	 *            search criteria
	 * @return the search result
	 */
	@Override
	@WebMethod
	public SearchResponse search(@WebParam(name = "sessionId") String sessionId,
			@WebParam(name = "searchCriteria") SearchCriteria criteria) {
		SearchSession session = getSearchSession(sessionId);
		if (session != null) {
			ISearchGateway gateway = GatewayFactory.createSearchGateway(SearchType.Bus);
			session.setGateway(gateway);
			return gateway.search(criteria);
		} else {
			return new SearchResponse(ExpResponse.SESSION_EXPIRED);
		}
	}

	/**
	 * Filter the search results
	 * 
	 * @param sessionId
	 *            search session id
	 * @param criteria
	 *            search criteria with filters
	 * @return the search result
	 */
	@Override
	@WebMethod
	public SearchResponse filter(@WebParam(name = "sessionId") String sessionId,
			@WebParam(name = "searchCriteria") SearchCriteria criteria) {
		SearchSession session = getSearchSession(sessionId);
		if (session != null) {
			ISearchGateway gateway = session.getGateway();
			return gateway.filter(criteria);
		} else {
			return new SearchResponse(ExpResponse.SESSION_EXPIRED);
		}
	}

	/**
	 * Check availability for a given criteria
	 * 
	 * @param sessionId
	 *            search session id
	 * @param availability
	 *            criteria
	 * @return availability details
	 */
	@Override
	@WebMethod
	public AvailabilityResponse checkAvailability(@WebParam(name = "sessionId") String sessionId,
			@WebParam(name = "availabilityCriteria") AvailabilityCriteria criteria) {
		if (sessionId != null && !sessionId.isEmpty()) { // using the search
															// session
			SearchSession session = getSearchSession(sessionId);
			if (session != null) {
				ISearchGateway gateway = session.getGateway();
				return gateway.checkAvailability(criteria);
			} else {
				return new AvailabilityResponse(ExpResponse.SESSION_EXPIRED);
			}
		} else { // allow bypassing the session for arbitrary availability
					// checks
			ISearchGateway gateway = GatewayFactory.createSearchGateway(SearchType.Bus);
			return gateway.checkAvailability(criteria);
		}
	}

	/**
	 * Re-prices a results leg by possibly changing certain parameters of the
	 * search criteria
	 * 
	 * @param criteria
	 *            re-pricing criteria
	 * @return re-priced results leg
	 */
	@Override
	@WebMethod
	public ExpResponse<ResultLeg> reprice(@WebParam(name = "sessionId") String sessionId,
			@WebParam(name = "repriceCriteria") RepriceCriteria criteria) {
		SearchSession session = getSearchSession(sessionId);
		if (session != null) {
			ISearchGateway gateway = session.getGateway();
			return gateway.reprice(criteria);
		} else {
			return new ExpResponse<ResultLeg>(ExpResponse.SESSION_EXPIRED);
		}
	}

	/**
	 * Hold the result for a given criteria
	 * 
	 * @param sessionId
	 *            search session id
	 * @param criteria
	 *            holding criteria
	 * @return <code>HoldResult</code> containing <code>AdvisoryNotice</code>s.
	 */
	@Override
	@WebMethod
	public HoldResponse hold(@WebParam(name = "sessionId") String sessionId,
			@WebParam(name = "holdCriteria") HoldCriteria criteria) {
		SearchSession session = getSearchSession(sessionId);
		if (session != null) {
			ISearchGateway gateway = session.getGateway();
			return gateway.hold(criteria);
		} else {
			return new HoldResponse(ExpResponse.SESSION_EXPIRED);
		}
	}

	/**
	 * Releases one or more held items
	 * 
	 * @param itemIds
	 *            items IDs of held items
	 * @return <code>ReleaseResponse</code> indicating the outcome of the
	 *         operation
	 */
	@Override
	public ReleaseResponse release(@WebParam(name = "sessionId") String sessionId,
			@WebParam(name = "itemIds") int[] itemIds) {
		SearchSession session = getSearchSession(sessionId);
		if (session != null) {
			ISearchGateway gateway = session.getGateway();
			List<Integer> list = new ArrayList<Integer>();
			for (int itemId : itemIds) {
				list.add(itemId);
			}
			return gateway.release(list);
		} else {
			return new ReleaseResponse(ExpResponse.SESSION_EXPIRED);
		}
	}

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
	@Override
	@WebMethod
	public ConfirmResponse confirm(@WebParam(name = "sessionId") String sessionId,
			@WebParam(name = "confirmationCriteria") ConfirmationCriteria criteria) {
		SearchSession session = getSearchSession(sessionId);
		if (session != null) {
			ISearchGateway gateway = session.getGateway();
			return gateway.confirm(criteria);
		} else {
			return new ConfirmResponse(ExpResponse.SESSION_EXPIRED);
		}
	}

	/**
	 * Calculates the cancellation charges.
	 * 
	 * @param criteria
	 *            cancellation criteria
	 * @return cancellation charges.
	 */
	@Override
	@WebMethod
	public CancellationChargeResponse calculateCancellationCharge(
			@WebParam(name = "cancellationCriteria") CancellationCriteria criteria) {
		ISearchGateway gateway = GatewayFactory.createSearchGateway(SearchType.Bus);
		return gateway.calculateCancellationCharge(criteria);
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
	@WebMethod
	public CancellationResponse cancel(@WebParam(name = "cancellationCriteria") CancellationCriteria criteria) {
		ISearchGateway gateway = GatewayFactory.createSearchGateway(SearchType.Bus);
		return gateway.cancel(criteria);
	}

	/**
	 * Do payments or refunds for the booking
	 * 
	 * @param criteria
	 */
	@Override
	@WebMethod
	public PaymentRefundsResponse addPaymentRefund(
			@WebParam(name = "paymentRefundCriteria") PaymentRefundCriteria criteria) {
		ISearchGateway gateway = GatewayFactory.createSearchGateway(SearchType.Bus);
		return gateway.addPaymentRefund(criteria);
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
	@WebMethod
	public ExpResponse<Void> markJourneyPerformed(
			@WebParam(name = "journeyPerformedCriteria") JourneyPerformedCriteria criteria) {
		ISearchGateway gateway = GatewayFactory.createSearchGateway(SearchType.Bus);
		return gateway.markJourneyPerformed(criteria);
	}

	/*
	 * Retrieves a search session identified by the given id
	 * 
	 * @param sessionId session id
	 * 
	 * @return search session identified by the given id
	 */
	private SearchSession getSearchSession(String sessionId) {
		Session session = sessionManager.getValidSession(sessionId);
		if (session != null) {
			// set session id and locale to be relayed in a thread local
			// session data variable
			SessionData sessionData = Context.getSessionData();
			sessionData.setSessionId(session.getId());
			sessionData.setLocale(session.getLocale());
			MDC.put("searchSessionId", session.getId());
			SearchSession searchSession = (SearchSession) session;
			return searchSession;
		}
		return null;
	}
}
