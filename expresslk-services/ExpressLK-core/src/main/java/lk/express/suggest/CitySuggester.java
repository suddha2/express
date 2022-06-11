package lk.express.suggest;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lk.express.bean.City;
import lk.express.db.HibernateUtil;

import org.hibernate.Query;
import org.hibernate.Session;

public class CitySuggester {

	/**
	 * Suggest a list of departure cities
	 * 
	 * @param q
	 *            query for city name
	 * @param start
	 *            start
	 * @param rows
	 *            number of cities, {@code null} for all
	 * @return suggested departure cities
	 */
	public List<City> suggestDeparturesHavingSchedules(String q, int start, Integer rows) {

		String where = lk.express.Context.getSessionData().getAllowedDivisionsWhereClause("bus_schedule", false);
		StringBuffer sb = new StringBuffer();
//		sb.append("SELECT DISTINCT city.* ");
//		sb.append("FROM (SELECT * FROM bus_schedule WHERE " + where
//				+ " AND bus_schedule.departure_time > :today) AS bus_schedule ");
//		sb.append("JOIN bus_route ON bus_route.id = bus_schedule.bus_route_id ");
//		sb.append("JOIN bus_sector ON bus_sector.bus_route_id = bus_route.id ");
//		sb.append("JOIN city ON city.id = bus_sector.from_city_id ");
//		sb.append("WHERE city.active = 1 ");
		

		sb.append(" SELECT DISTINCT city.* FROM bus_route JOIN bus_sector ON bus_sector.bus_route_id = bus_route.id JOIN city ON city.id = bus_sector.from_city_id  " + 
				" WHERE city.active = 1 and bus_route.id in (select distinct(bus_schedule.bus_route_id) from  bus_schedule where ");
		sb.append(where);
		sb.append(" and bus_schedule.departure_time > :today)");
		
		if (q != null && q.length() > 0) {
			sb.append("AND city.name LIKE :q ");
		}
		sb.append("ORDER BY city.name ");
		if (rows != null) {
			sb.append("LIMIT :r OFFSET :s");
		}
		String sql = sb.toString();
	
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Session session = HibernateUtil.getCurrentSession();
		Query query = session.createSQLQuery(sql).addEntity(City.class).setParameter("today", calendar.getTime());
		if (q != null && q.length() > 0) {
			query.setParameter("q", escapeForLikeQuery(q) + "%");
		}
		if (rows != null) {
			query.setParameter("r", rows).setParameter("s", start);
		}
		@SuppressWarnings("unchecked")
		List<City> cities = query.list();
		return cities;
	}
	
	
	public List<City> suggestDepartures(String q, int start, Integer rows) {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT DISTINCT city.* FROM city  ");
		if (q != null && q.length() > 0) {
			sb.append(" where city.name LIKE :q ");
		}
		sb.append("ORDER BY city.name ");
		if (rows != null) {
			sb.append("LIMIT :r OFFSET :s");
		}
		String sql = sb.toString();
		Session session = HibernateUtil.getCurrentSession();
		Query query = session.createSQLQuery(sql).addEntity(City.class);
		if (q != null && q.length() > 0) {
			query.setParameter("q", escapeForLikeQuery(q) + "%");
		}
		if (rows != null) {
			query.setParameter("r", rows).setParameter("s", start);
		}
		@SuppressWarnings("unchecked")
		List<City> cities = query.list();
		return cities;
	}

	/**
	 * Suggest a list of destination cities
	 * 
	 * @param originId
	 *            departure city ID
	 * @param q
	 *            query for city name
	 * @param start
	 *            start
	 * @param rows
	 *            number of cities, {@code null} for all
	 * @return suggested destination cities
	 */
	public List<City> suggestDestinations(int originId, String q, int start, Integer rows) {

		String where = lk.express.Context.getSessionData().getAllowedDivisionsWhereClause("bus_schedule", false);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT city.* ");
		sb.append("FROM (SELECT * FROM bus_schedule WHERE " + where
				+ " AND bus_schedule.departure_time > :today) AS bus_schedule ");
		sb.append("JOIN bus_route ON bus_route.id = bus_schedule.bus_route_id ");
		sb.append("JOIN bus_sector ON bus_sector.bus_route_id = bus_route.id ");
		sb.append("JOIN city ON city.id = bus_sector.to_city_id ");
		sb.append("WHERE city.active = 1 ");
		sb.append("AND bus_sector.from_city_id = :originId ");
		if (q != null && q.length() > 0) {
			sb.append("AND city.name LIKE :q ");
		}
		sb.append("ORDER BY city.name ");
		if (rows != null) {
			sb.append("LIMIT :r OFFSET :s");
		}
		String sql = sb.toString();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Session session = HibernateUtil.getCurrentSession();
		Query query = session.createSQLQuery(sql).addEntity(City.class).setParameter("today", calendar.getTime())
				.setParameter("originId", originId);
		if (q != null && q.length() > 0) {
			query.setParameter("q", escapeForLikeQuery(q) + "%");
		}
		if (rows != null) {
			query.setParameter("r", rows).setParameter("s", start);
		}
		@SuppressWarnings("unchecked")
		List<City> destinations = query.list();
		return destinations;
	}

	/**
	 * Suggest a list of destination cities
	 * 
	 * @param origin
	 *            departure city name/ID
	 * @param q
	 *            query for city name
	 * @param start
	 *            start
	 * @param rows
	 *            number of cities, {@code null} for all
	 * @return suggested destination cities
	 */
	public List<City> suggestDestinations(String origin, String text, int start, Integer rows) {
		throw new UnsupportedOperationException();
	}

	private static String escapeForLikeQuery(String q) {
		return q.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
	}

}
