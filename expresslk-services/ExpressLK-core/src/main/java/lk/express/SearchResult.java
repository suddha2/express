package lk.express;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import lk.express.filter.LegFilterData;

/**
 * A <code>SearchResult</code> is the result of a search ran on ExpressLK search
 * engine. It contains <code>LegResult</code>s for each of the two legs of the
 * search criteria.
 */
@XmlRootElement
@XmlType(name = "SearchResult", namespace = "http://express.lk")
@XmlSeeAlso({ LegResult.class, LegFilterData.class })
public class SearchResult {

	private LegResult[] legResults = new LegResult[2];
	private LegFilterData[] legFilterData = new LegFilterData[2];

	public LegResult[] getLegResults() {
		return legResults;
	}

	public void setLegResults(LegResult[] legResults) {
		this.legResults = legResults;
	}

	public void setOutBoundLegResult(LegResult out) {
		legResults[0] = out;
	}

	public void setInBoundLegResult(LegResult in) {
		legResults[1] = in;
	}

	public LegFilterData[] getLegFilterData() {
		return legFilterData;
	}

	public void setLegFilterData(LegFilterData[] legFilterData) {
		this.legFilterData = legFilterData;
	}

	public void setOutBoundLegFilterData(LegFilterData out) {
		legFilterData[0] = out;
	}

	public void setInBoundLegFilterData(LegFilterData in) {
		legFilterData[1] = in;
	}
}
