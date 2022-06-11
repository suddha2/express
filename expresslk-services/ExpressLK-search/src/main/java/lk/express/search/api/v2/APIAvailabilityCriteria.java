package lk.express.search.api.v2;

import lk.express.AvailabilityCriteria;

/**
 * Availability criteria
 * 
 * @version v2
 */
public class APIAvailabilityCriteria extends APICriteria {

	/**
	 * Index of the result for a journey leg
	 */
	public String resultIndex;
	/**
	 * Index of the sector in the journey leg
	 */
	public int sectorIndex;

	/**
	 * @exclude
	 */
	@Override
	public String validate() {
		if (resultIndex == null || resultIndex.isEmpty()) {
			return "[resultIndex] cannot be empty";
		}
		if (sectorIndex < 0) {
			return "Wrong [sectorIndex]";
		}
		return null;
	}

	/**
	 * @exclude
	 */
	public AvailabilityCriteria getCriteria() {
		AvailabilityCriteria criteria = new AvailabilityCriteria();
		criteria.setResultIndex(resultIndex);
		criteria.setSectorIndex(sectorIndex);
		return criteria;
	}
}
