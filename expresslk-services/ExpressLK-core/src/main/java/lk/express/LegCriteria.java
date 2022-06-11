package lk.express;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.filter.Filter;

@XmlRootElement
@XmlType(name = "LegCriteria", namespace = "http://express.lk")
public class LegCriteria {

	private long departureTimestamp;
	private List<String> type;
	private List<Integer> viaPoints;
	private List<Filter<ResultLeg>> filters;

	private long departureTimestampEnd;
	private int resultsCount;

	public long getDepartureTimestamp() {
		return departureTimestamp;
	}

	public void setDepartureTimestamp(long departureTimestamp) {
		this.departureTimestamp = departureTimestamp;
	}

	public Date getDepartureTime() {
		return new Date(departureTimestamp);
	}

	public List<String> getType() {
		if (type == null) {
			type = new ArrayList<>();
		}
		return type;
	}

	public void setType(List<String> type) {
		this.type = type;
	}

	public List<Integer> getViaPoints() {
		if (viaPoints == null) {
			viaPoints = new ArrayList<>();
		}
		return viaPoints;
	}

	public void setViaPoints(List<Integer> viaPoints) {
		this.viaPoints = viaPoints;
	}

	public List<Filter<ResultLeg>> getFilters() {
		if (filters == null) {
			filters = new ArrayList<>();
		} else {
			Iterator<Filter<ResultLeg>> iterator = filters.iterator();
			while (iterator.hasNext()) {
				Filter<ResultLeg> filter = iterator.next();
				if (filter == null) {
					iterator.remove();
				}
			}
		}
		return filters;
	}

	public void setFilters(List<Filter<ResultLeg>> filters) {
		this.filters = filters;
	}

	public long getDepartureTimestampEnd() {
		return departureTimestampEnd;
	}

	public void setDepartureTimestampEnd(long departureTimestampEnd) {
		this.departureTimestampEnd = departureTimestampEnd;
	}

	public Date getDepartureTimeEnd() {
		if (departureTimestampEnd <= 0) {
			return null;
		}
		return new Date(departureTimestampEnd);
	}

	public int getResultsCount() {
		return resultsCount;
	}

	public void setResultsCount(int resultsCount) {
		this.resultsCount = resultsCount;
	}

	@Override
	public String toString() {
		StringBuilder toString = new StringBuilder();
		toString.append("Depature Time :");
		toString.append(departureTimestamp);
		toString.append("\n");
		toString.append("Type :");
		toString.append(type);
		return toString.toString();
	}
}
