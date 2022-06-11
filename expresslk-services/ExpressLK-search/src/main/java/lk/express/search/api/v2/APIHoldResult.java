package lk.express.search.api.v2;

import java.util.ArrayList;
import java.util.List;

import lk.express.HoldResult;
import lk.express.HoldResult.HeldItem;

/**
 * Seat holding result
 * 
 * @version v2
 *
 */
public class APIHoldResult {

	/**
	 * Seat holding acknowledgements for each sector
	 */
	public List<APIHeldItem> heldItemIds = new ArrayList<>();

	/**
	 * @exclude
	 */
	public APIHoldResult() {

	}

	/**
	 * @exclude
	 */
	public APIHoldResult(HoldResult e) {
		for (HeldItem heldItem : e.getHeldItems()) {
			heldItemIds.add(new APIHeldItem(heldItem));
		}
	}

	/**
	 * Seat holding acknowledgement for a particular sector
	 * 
	 * @version v2
	 *
	 */
	public static class APIHeldItem {

		/**
		 * Index of the result for a journey leg
		 */
		public String resultIndex;
		/**
		 * Index of the sector in the journey leg
		 */
		public int sectorIndex;
		/**
		 * Reference ID for the held seat(s)<br>
		 * This will be required for the passenger seat allocation in booking
		 * confirmation.
		 */
		public int heldItemId;

		/**
		 * @exclude
		 */
		public APIHeldItem() {
		}

		/**
		 * @exclude
		 */
		public APIHeldItem(HeldItem e) {
			resultIndex = e.getResultIndex();
			sectorIndex = e.getSectorIndex();
			heldItemId = e.getHeldItemId();
		}
	}
}
