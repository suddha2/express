package lk.express.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.ResultLeg;

@XmlRootElement
@XmlType(name = "TransitFilter", namespace = "http://filter.express.lk")
public class TransitFilter extends Filter<ResultLeg> {

	private int maxTransits;

	public int getMaxTransits() {
		return maxTransits;
	}

	public void setMaxTransits(int maxTransits) {
		this.maxTransits = maxTransits;
	}

	@Override
	public boolean test(ResultLeg leg) {
		return leg.getNoOfTransits() <= maxTransits;
	}

	@Override
	public List<FilterValue> getValues(List<? extends ResultLeg> list) {
		Set<Integer> transits = new HashSet<>();
		for (ResultLeg leg : list) {
			transits.add(leg.getNoOfTransits());
		}
		List<FilterValue> values = new ArrayList<>();
		for (Integer transity : transits) {
			values.add(new FilterValue(transity.toString(), transity.toString()));
		}
		return values;
	}
}
