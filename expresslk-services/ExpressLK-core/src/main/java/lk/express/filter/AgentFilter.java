package lk.express.filter;

import java.util.ArrayList;
import java.util.List;

import lk.express.ResultLeg;
import lk.express.ResultSector;
import lk.express.bean.Bus;

public class AgentFilter extends Filter<ResultLeg> {

	private List<Integer> restrictedBuses;

	public AgentFilter(List<Integer> restrictedBuses) {
		this.restrictedBuses = restrictedBuses;
	}

	@Override
	public boolean test(ResultLeg leg) {
		for (ResultSector sector : leg.getSectors()) {
			Bus bus = sector.getSchedule().getBus();
			if (restrictedBuses.contains(bus.getId())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public List<FilterValue> getValues(List<? extends ResultLeg> list) {
		return new ArrayList<>();
	}
}
