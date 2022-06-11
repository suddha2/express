package lk.express.search;

import java.util.List;

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
import lk.express.cnx.CancellationChargeResponse;
import lk.express.cnx.CancellationCriteria;
import lk.express.cnx.CancellationResponse;

public interface ISearchGateway {

	/**
	 * Search for a given search criteria.
	 * 
	 * @param criteria
	 *            search criteria
	 * @return the search result
	 */
	SearchResponse search(SearchCriteria criteria);

	/**
	 * Filter the search results
	 * 
	 * @param criteria
	 *            search criteria with filters
	 * @return the search result
	 */
	SearchResponse filter(SearchCriteria criteria);

	/**
	 * Check availability for a given criteria
	 * 
	 * @param availability
	 *            availability criteria
	 * @return availability details
	 */
	AvailabilityResponse checkAvailability(AvailabilityCriteria criteria);

	/**
	 * Re-prices a results leg by possibly with a slightly different version of
	 * search criteria
	 * 
	 * @param criteria
	 *            re-pricing criteria
	 * @return re-priced results leg
	 */
	ExpResponse<ResultLeg> reprice(RepriceCriteria criteria);

	/**
	 * Hold the result for a given criteria
	 * 
	 * @param criteria
	 *            holding criteria
	 * @return <code>HoldResponse</code> containing <code>AdvisoryNotice</code>
	 *         s.
	 */
	HoldResponse hold(HoldCriteria criteria);

	/**
	 * Releases one or more held items
	 * 
	 * @param itemIds
	 *            items IDs of held items
	 * @return
	 */
	ReleaseResponse release(List<Integer> itemIds);

	/**
	 * Confirms a booking item with the search gateway.
	 * 
	 * @param criteria
	 *            confirmation criteria
	 * @return <code>Booking</code> containing all the confirmed
	 *         <code>BookingItem</code>s.
	 */
	ConfirmResponse confirm(ConfirmationCriteria criteria);

	/**
	 * Calculates the cancellation charges.
	 * 
	 * @param criteria
	 *            cancellation criteria
	 * @return cancellation charges.
	 */
	CancellationChargeResponse calculateCancellationCharge(CancellationCriteria criteria);

	/**
	 * Cancels a set of booking items.
	 * 
	 * @param criteria
	 *            cancellation criteria
	 * @return <code>CancellationResponse</code> containing the status of
	 *         cancellations.
	 */
	CancellationResponse cancel(CancellationCriteria criteria);

	/**
	 * Payments or Refunds for a booking
	 * 
	 * @param criteria
	 *            payment refund criteria
	 * @return response containing the state
	 */
	PaymentRefundsResponse addPaymentRefund(PaymentRefundCriteria criteria);

	/**
	 * Marks as journey performed for a particular passenger for a particular
	 * schedule
	 * 
	 * @param criteria
	 *            journey performed criteria
	 * @return response containing the state
	 */
	ExpResponse<Void> markJourneyPerformed(JourneyPerformedCriteria criteria);
}
