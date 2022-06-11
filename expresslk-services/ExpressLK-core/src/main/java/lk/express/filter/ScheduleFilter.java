package lk.express.filter;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.ResultLeg;
import lk.express.ResultSector;
import lk.express.schedule.BusSchedule;

@XmlRootElement
@XmlType(name = "ScheduleFilter", namespace = "http://filter.express.lk")
public class ScheduleFilter extends Filter<ResultLeg> {

	private Integer scheduleId;

	@Override
	public boolean test(ResultLeg leg) {
		for (ResultSector sector : leg.getSectors()) {
			BusSchedule schedule = sector.getSchedule();
			if (schedule.getId() == scheduleId) {
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
				BusSchedule schedlue = sector.getSchedule();
				values.add(new FilterValue(schedlue.getId().toString(), schedlue.toString()));
			}
		}
		return values;
	}

	public Integer getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Integer scheduleId) {
		this.scheduleId = scheduleId;
	}
}
