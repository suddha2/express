package lk.express.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.ResultLeg;
import lk.express.ResultSector;
import lk.express.bean.Bus;
import lk.express.bean.TravelClass;

@XmlRootElement
@XmlType(name = "TravelClassFilter", namespace = "http://filter.express.lk")
public class TravelClassFilter extends Filter<ResultLeg> {

	private String[] travelClasses;

	public String[] getTravelClasses() {
		return travelClasses;
	}

	public void setTravelClasses(String[] travelClasses) {
		this.travelClasses = travelClasses;
	}

	@Override
	public boolean test(ResultLeg leg) {
		for (ResultSector sector : leg.getSectors()) {
			Bus bus = sector.getSchedule().getBus();
			String classCode = bus.getTravelClass().getCode();
			for (String travelClass : travelClasses) {
				if (travelClass.equalsIgnoreCase(classCode)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public List<FilterValue> getValues(List<? extends ResultLeg> list) {
		Set<TravelClass> tcs = new HashSet<>();
		for (ResultLeg leg : list) {
			for (ResultSector sector : leg.getSectors()) {
				Bus bus = sector.getSchedule().getBus();
				tcs.add(bus.getTravelClass());
			}
		}
		List<FilterValue> values = new ArrayList<>();
		for (TravelClass tc : tcs) {
			values.add(new FilterValue(tc.getCode(), tc.getName()));
		}
		return values;
	}
}
