package lk.express;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "SearchCriteria", namespace = "http://express.lk")
public class SearchCriteria extends BaseCriteria {

	private static final long serialVersionUID = 1L;

	private int fromCityId;
	private String fromCity;
	private int toCityId;
	private String toCity;
	private boolean roundTrip;
	private LegCriteria[] legCriterion;

	public SearchCriteria() {
		legCriterion = new LegCriteria[2];
	}

	public int getFromCityId() {
		return fromCityId;
	}

	public void setFromCityId(int fromCityId) {
		this.fromCityId = fromCityId;
	}

	public String getFromCity() {
		return fromCity;
	}

	public void setFromCity(String fromCity) {
		this.fromCity = fromCity;
	}

	public int getToCityId() {
		return toCityId;
	}

	public void setToCityId(int toCityId) {
		this.toCityId = toCityId;
	}

	public String getToCity() {
		return toCity;
	}

	public void setToCity(String toCity) {
		this.toCity = toCity;
	}

	public boolean isRoundTrip() {
		return roundTrip;
	}

	public void setRoundTrip(boolean roundTrip) {
		this.roundTrip = roundTrip;
	}

	public LegCriteria[] getLegCriterion() {
		return legCriterion;
	}

	public void setLegCriterion(LegCriteria[] legCriterion) {
		this.legCriterion = legCriterion;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
