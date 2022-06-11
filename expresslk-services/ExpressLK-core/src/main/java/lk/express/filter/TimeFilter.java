package lk.express.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.ResultLeg;

@XmlRootElement
@XmlType(name = "TimeFilter", namespace = "http://filter.express.lk")
public class TimeFilter extends Filter<ResultLeg> {

	private Date leaveAfter;
	private Date leaveBefore;
	private Date reachAfter;
	private Date reachBefore;

	public Date getLeaveAfter() {
		return leaveAfter;
	}

	public void setLeaveAfter(Date leaveAfter) {
		this.leaveAfter = leaveAfter;
	}

	public Date getLeaveBefore() {
		return leaveBefore;
	}

	public void setLeaveBefore(Date leaveBefore) {
		this.leaveBefore = leaveBefore;
	}

	public Date getReachAfter() {
		return reachAfter;
	}

	public void setReachAfter(Date reachAfter) {
		this.reachAfter = reachAfter;
	}

	public Date getReachBefore() {
		return reachBefore;
	}

	public void setReachBefore(Date reachBefore) {
		this.reachBefore = reachBefore;
	}

	@Override
	public boolean test(ResultLeg t) {
		Date dep = t.getDepartureTime();
		if (leaveAfter != null && leaveAfter.after(dep)) {
			return false;
		}
		if (leaveBefore != null && leaveBefore.before(dep)) {
			return false;
		}

		Date arrival = t.getArrivalTime();
		if (reachAfter != null && reachAfter.after(arrival)) {
			return false;
		}
		if (reachBefore != null && reachBefore.before(arrival)) {
			return false;
		}
		return true;
	}

	@Override
	public List<FilterValue> getValues(List<? extends ResultLeg> list) {
		return new ArrayList<>();
	}
}
