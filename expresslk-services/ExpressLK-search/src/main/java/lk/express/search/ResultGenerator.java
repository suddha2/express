package lk.express.search;

import lk.express.LegResult;
import lk.express.SearchCriteria;

public abstract class ResultGenerator {

	protected SearchCriteria criteria;
	protected LegResult outLegResult;
	protected LegResult inLegResult;

	public ResultGenerator(SearchCriteria criteria) {
		this.criteria = criteria;
	}

	protected abstract void generateResult();

	public LegResult getOutLegResult() {
		return outLegResult;
	}

	public LegResult getInLegResult() {
		return inLegResult;
	}
}
