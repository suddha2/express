package lk.express.filter;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.ResultLeg;
import lk.express.ResultSector;
import lk.express.bean.Bus;
import lk.express.schedule.BusSchedule;

@XmlRootElement
@XmlType(name = "BusFilter", namespace = "http://filter.express.lk")
public class BusFilter extends Filter<ResultLeg> {

	private Integer busId;

	@Override
	public boolean test(ResultLeg leg) {
		for (ResultSector sector : leg.getSectors()) {
			BusSchedule schedule = sector.getSchedule();
			if (schedule.getBus().getId() == busId) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<FilterValue> getValues(List<? extends ResultLeg> list) {
		List<FilterValue> values = new ArrayList<>();
		for (ResultLeg leg : list) {
			for (ResultSector sector : leg.getSectors()) {
				Bus bus = sector.getSchedule().getBus();
				values.add(new FilterValue(bus.getId().toString(), bus.getPlateNumber()));
			}
		}
		return values;
	}

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}
}
