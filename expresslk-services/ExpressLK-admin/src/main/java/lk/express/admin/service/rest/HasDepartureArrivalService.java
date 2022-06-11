package lk.express.admin.service.rest;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lk.express.HasDepartureArrival;
import lk.express.bean.Entity;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public abstract class HasDepartureArrivalService<T extends Entity & HasDepartureArrival> extends EntityService<T> {

	public static final String DEP_START = "departureStart";
	public static final String DEP_END = "departureEnd";
	public static final String DEP_ON = "departureOn";
	public static final String ARR_START = "arrivalStart";
	public static final String ARR_END = "arrivalEnd";
	public static final String ARR_ON = "arrivalOn";

	public HasDepartureArrivalService(Class<T> clazz) {
		super(clazz);
	}

	@Override
	@SuppressWarnings("hiding")
	protected <T extends Entity> void populateCriteria(Class<T> clazz, Criteria criteria,
			Map<String, String> pathToAlias, Map<String, List<String>> queryParams) {

		Iterator<Entry<String, List<String>>> iterator = queryParams.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, List<String>> e = iterator.next();
			String key = e.getKey();
			List<String> vals = e.getValue();

			String value = getSingleParam(vals);
			if (value != null) {
				if (DEP_START.equals(key) || DEP_END.equals(key) || ARR_START.equals(key) || ARR_END.equals(key)
						|| DEP_ON.equals(key) || ARR_ON.equals(key)) {

					Date time = new Date(Long.valueOf(value));

					if (DEP_START.equals(key)) {
						criteria.add(Restrictions.ge(HasDepartureArrival.DEPARTURE_TIME, time));
					} else if (DEP_END.equals(key)) {
						criteria.add(Restrictions.le(HasDepartureArrival.DEPARTURE_TIME, time));
					} else if (ARR_START.equals(key)) {
						criteria.add(Restrictions.ge(HasDepartureArrival.ARRIVAL_TIME, time));
					} else if (ARR_END.equals(key)) {
						criteria.add(Restrictions.le(HasDepartureArrival.ARRIVAL_TIME, time));
					} else if (DEP_ON.equals(key) || ARR_ON.equals(key)) {

						Date start = getStartOfDay(time);
						Date end = getEndOfDay(time);

						if (DEP_ON.equals(key)) {
							criteria.add(Restrictions.ge(HasDepartureArrival.DEPARTURE_TIME, start));
							criteria.add(Restrictions.le(HasDepartureArrival.DEPARTURE_TIME, end));
						} else if (ARR_ON.equals(key)) {
							criteria.add(Restrictions.ge(HasDepartureArrival.ARRIVAL_TIME, start));
							criteria.add(Restrictions.le(HasDepartureArrival.ARRIVAL_TIME, end));
						}
					}

					// these params have been taken care of; remove them
					iterator.remove();
				}
			}
		}

		super.populateCriteria(clazz, criteria, pathToAlias, queryParams);
	}

	private Date getStartOfDay(Date time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		calendar.set(year, month, day, 0, 0, 0);
		return calendar.getTime();
	}

	private Date getEndOfDay(Date time) {
		Date start = getStartOfDay(time);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(start);
		calendar.add(Calendar.DATE, 1);
		return calendar.getTime();
	}
}
