package lk.express.filter;

import java.util.List;
import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlSeeAlso({ AmenityFilter.class, AvailabilityFilter.class, BusFilter.class, ScheduleFilter.class, TimeFilter.class,
		TransitFilter.class, TravelClassFilter.class })
@XmlType(name = "Filter", namespace = "http://filter.express.lk")
public abstract class Filter<T> implements Predicate<T> {

	public abstract List<FilterValue> getValues(List<? extends T> list);
}
