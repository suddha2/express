package lk.express.search.api.v2;

import java.util.ArrayList;
import java.util.List;

import lk.express.HoldCriteria;
import lk.express.HoldCriteria.SeatCriteria;

/**
 * Seat holding criteria for a particular journey leg
 *
 * @version v2
 */
public class APIHoldCriteria extends APICriteria {

	/**
	 * Index of the result for a journey leg
	 */
	public String resultIndex;
	/**
	 * Seat holding criteria for search sector of the journey leg
	 */
	public List<APISeatCriteria> seatCriterias = new ArrayList<>();

	/**
	 * @exclude
	 */
	@Override
	public String validate() {
		if (resultIndex == null || resultIndex.isEmpty()) {
			return "[resultIndex] cannot be empty";
		}
		String error;
		for (APISeatCriteria seatCriteria : seatCriterias) {
			error = seatCriteria.validate();
			if (error != null) {
				return error;
			}
		}
		return null;
	}

	/**
	 * @exclude
	 */
	public HoldCriteria getCriteria() {
		HoldCriteria criteria = new HoldCriteria();
		criteria.setResultIndex(resultIndex);

		List<SeatCriteria> sCriteria = new ArrayList<>();
		for (APISeatCriteria sc : seatCriterias) {
			sCriteria.add(sc.getCriteria());
			criteria.setBoardingPoint(sc.boardingLocationId);
			criteria.setDroppingPoint(sc.dropOffLocationId);
		}
		criteria.setSeatCriterias(sCriteria);

		return criteria;
	}

	/**
	 * Seat holding criteria for a particular sector
	 *
	 * @version v2
	 */
	public static final class APISeatCriteria extends APICriteria {

		/**
		 * Index of the sector in the journey leg
		 */
		public int sectorIndex;
		/**
		 * ID of the boarding bus stop
		 */
		public int boardingLocationId;
		/**
		 * ID of the drop off bus stop
		 */
		public int dropOffLocationId;
		/**
		 * List of seat numbers to hold
		 */
		public List<String> seats = new ArrayList<>();

		/**
		 * @exclude
		 */
		@Override
		public String validate() {
			if (sectorIndex < 0) {
				return "Wrong [sectorIndex]";
			}
			for (String seat : seats) {
				if (seat == null || seat.isEmpty()) {
					return "Seat number cannot be empty";
				}
			}
			return null;
		}

		/**
		 * @exclude
		 */
		public SeatCriteria getCriteria() {
			SeatCriteria criteria = new SeatCriteria();
			criteria.setSectorNumber(sectorIndex);
			criteria.setSeats(new ArrayList<>(seats));

			return criteria;
		}
	}
}
