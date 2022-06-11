package lk.express.filter;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.ResultLeg;
import lk.express.ResultSector;
import lk.express.bean.BusRouteBusStop;
import lk.express.bean.City;
import lk.express.schedule.BusSchedule;
import lk.express.search.AvailabilityChecker;
import lk.express.search.AvailabilityCheckerImpl;

@XmlRootElement
@XmlType(name = "AvailabilityFilter", namespace = "http://filter.express.lk")
public class AvailabilityFilter extends Filter<ResultLeg> {

	@Override
	public boolean test(ResultLeg leg) {
		for (ResultSector sector : leg.getSectors()) {
			BusSchedule schedule = sector.getSchedule();
			City fromCity = sector.getFromCity();
			City toCity = sector.getToCity();

			int fromStop = -1, toStop = -1;
			for (BusRouteBusStop stop : schedule.getBusRoute().getRouteStops()) {
				if (stop.getStop().getCity().equals(fromCity)) {
					fromStop = stop.getStop().getId();
				}
				if (stop.getStop().getCity().equals(toCity)) {
					toStop = stop.getStop().getId();
				}
			}
			assert fromStop != -1;
			assert toStop != -1;

			AvailabilityChecker checker = new AvailabilityCheckerImpl(schedule.getId(), fromStop, toStop, false, false);
			return checker.getVacantSeats(null).size() > 0;
		}
		return true;
	}

	@Override
	public List<FilterValue> getValues(List<? extends ResultLeg> list) {
		List<FilterValue> values = new ArrayList<>();
		values.add(new FilterValue(null, "With available seats"));
		return values;
	}
}
