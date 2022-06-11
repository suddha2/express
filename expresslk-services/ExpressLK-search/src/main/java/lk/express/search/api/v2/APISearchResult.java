package lk.express.search.api.v2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.express.LegResult;
import lk.express.ResultLeg;
import lk.express.ResultSector;
import lk.express.SearchResult;
import lk.express.api.v2.APIBusSchedule;
import lk.express.api.v2.APICity;

/**
 * Search results for a search request.
 * 
 * @version v2
 */
public class APISearchResult {

	/**
	 * Search results for leg(s)<br>
	 * Max array length: 2, pertaining to out-bound and in-bound legs
	 */
	public APILegResult[] legResults = new APILegResult[2];

	/**
	 * @exclude
	 */
	public APISearchResult() {
	}

	/**
	 * @exclude
	 */
	public APISearchResult(SearchResult e) {
		LegResult[] lResults = e.getLegResults();
		if (lResults.length > 0 && lResults[0] != null) {
			legResults[0] = new APILegResult(lResults[0]);
		}
		if (lResults.length > 1 && lResults[1] != null) {
			legResults[1] = new APILegResult(lResults[1]);
		}
	}

	/**
	 * Search results for a particular journey leg.
	 * 
	 * @version v2
	 */
	public static class APILegResult {

		/**
		 * List of results for the particular journey leg.
		 */
		public List<APIResultLeg> legs = new ArrayList<>();

		/**
		 * @exclude
		 */
		public APILegResult() {
		}

		/**
		 * @exclude
		 */
		public APILegResult(LegResult e) {
			for (ResultLeg leg : e.getLegs()) {
				legs.add(new APIResultLeg(leg));
			}
		}

	}

	/**
	 * A single results for a particular journey leg.
	 * 
	 * @version v2
	 */
	public static class APIResultLeg {

		/**
		 * Index of the result
		 */
		public String resultIndex;
		/**
		 * Per person fare
		 */
		public double fare;
		/**
		 * Per person price
		 */
		public double price;

		public double childFare;

		public double childPrice;

		/**
		 * Constituent sectors of the journey leg
		 */
		public List<APIResultSector> sectors = new ArrayList<>();

		/**
		 * @exclude
		 */
		public APIResultLeg() {
		}

		/**
		 * @exclude
		 */
		public APIResultLeg(ResultLeg e) {
			resultIndex = e.getResultIndex();
			fare = e.getFare();
			price = e.getPrice();
			childFare = e.getChildFare();
			childPrice = e.getChildPrice();
			int a = 0;
			for (ResultSector sector : e.getSectors()) {
				sectors.add(new APIResultSector(sector, a));
				a++;
			}
		}
	}

	/**
	 * A single sector of a result for particular journey leg.<br>
	 * A journey leg (out-bound or in-bound) may have multiple sectors (with
	 * transits).
	 *
	 * @version v2
	 */
	public static class APIResultSector {

		/**
		 * Index of the sector
		 */
		public int sectorIndex;
		/**
		 * Per person fare
		 */
		public double fare;
		/**
		 * Per person price
		 */
		public double price;

		public double childFare;

		public double childPrice;

		/**
		 * Bus schedule for the sector
		 */
		public APIBusSchedule schedule;
		/**
		 * Departure city for the sector
		 */
		public APICity fromCity;
		/**
		 * Arrival city for the sector
		 */
		public APICity toCity;
		/**
		 * Departure time for the sector
		 */
		public Date departureTime;
		/**
		 * Arrival time for the sector
		 */
		public Date arrivalTime;

		/**
		 * @exclude
		 */
		public APIResultSector() {
		}

		/**
		 * @exclude
		 */
		public APIResultSector(ResultSector e, int sectorIndex) {
			this.sectorIndex = sectorIndex;
			fare = e.getFare();
			price = e.getPrice();
			childFare = e.getChildFare();
			childPrice = e.getChildPrice();
			schedule = new APIBusSchedule(e.getSchedule());
			fromCity = new APICity(e.getFromCity());
			toCity = new APICity(e.getToCity());
			departureTime = e.getDepartureTime();
			arrivalTime = e.getArrivalTime();
		}
	}
}
