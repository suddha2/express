package lk.express.search.api.v2;

import java.util.Calendar;
import java.util.Date;

import lk.express.LegCriteria;
import lk.express.SearchCriteria;
import lk.express.api.v2.APICity;

/**
 * Search criteria
 * 
 * @version v2
 */
public class APISearchCriteria extends APICriteria {

	/**
	 * {@link APICity#id id} of the departure {@link APICity City}<br>
	 * (Required)
	 */
	public int fromCityId;
	/**
	 * {@link APICity#id id} of the arrival {@link APICity City}<br>
	 * (Required)
	 */
	public int toCityId;
	/**
	 * Array of {@link APILegCriteria journey leg criteria}<br>
	 * Maximum array length: 2<br>
	 * (Required)
	 */
	public APILegCriteria[] legCriteria = new APILegCriteria[2];

	/**
	 * @exclude
	 */
	@Override
	public String validate() {
		if (fromCityId <= 0) {
			return "Wrong [fromCityId]";
		}
		if (toCityId <= 0) {
			return "Wrong [toCityId]";
		}
		if (fromCityId == toCityId) {
			return "[toCityId] cannot be same as [fromCityId]";
		}
		if (legCriteria == null || legCriteria.length == 0 || legCriteria[0] == null) {
			return "Missing out-bound leg criteria";
		}
		String error;
		for (APILegCriteria legCriterion : legCriteria) {
			error = legCriterion.validate();
			if (error != null) {
				return error;
			}
		}
		return null;
	}

	/**
	 * @exclude
	 */
	public SearchCriteria getCriteria() {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setFromCityId(fromCityId);
		criteria.setToCityId(toCityId);
		criteria.getLegCriterion()[0] = legCriteria[0].getCriteria();
		if (legCriteria.length > 1 && legCriteria[1] != null) {
			criteria.getLegCriterion()[1] = legCriteria[1].getCriteria();
		}
		return criteria;
	}

	/**
	 * Journey leg criteria <br>
	 * 
	 * A journey may have up to two legs (out-bound and in-bound). Represents
	 * search criteria pertaining to a single journey leg.
	 * 
	 * @version v2
	 */
	public static class APILegCriteria extends APICriteria {

		/**
		 * Departure timestamp for the search<br>
		 * (Required)
		 */
		public long departureTimestampStart;
		/**
		 * End departure timestamp for the search<br>
		 * No search results having departures after this would be returned as
		 * search results<br>
		 * (Optional)
		 */
		public long departureTimestampEnd;

		/**
		 * @exclude
		 */
		@Override
		public String validate() {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, 3);
			Date threeMonths = calendar.getTime();

			calendar.setTime(new Date());
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			Date today = calendar.getTime();

			Date depDate = new Date(departureTimestampStart);
			if (depDate.before(today) || depDate.after(threeMonths)) {
				return "Departure date is out of allowed date range";
			}
			return null;
		}

		/**
		 * @exclude
		 */
		public LegCriteria getCriteria() {
			LegCriteria criteria = new LegCriteria();
			criteria.setDepartureTimestamp(departureTimestampStart);
			criteria.setDepartureTimestampEnd(departureTimestampEnd);
			criteria.setResultsCount(10);
			return criteria;
		}
	}
}
