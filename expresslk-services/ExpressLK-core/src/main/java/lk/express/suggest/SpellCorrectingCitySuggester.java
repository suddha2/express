package lk.express.suggest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import lk.express.bean.City;
import lk.express.config.Configuration;
import lk.express.config.ConfigurationManager;
import lk.express.db.HibernateUtil;
import lk.express.textsearch.spell.IndexBuilder;
import lk.express.textsearch.spell.KGramLevenshteinSpellingCorrector;
import lk.express.textsearch.spell.PrefixStrategy;
import lk.express.textsearch.spell.RelationalIndexBuilder;
import lk.express.textsearch.spell.SpellingCorrector;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpellCorrectingCitySuggester extends CitySuggester {

	private static final Logger logger = LoggerFactory.getLogger(SpellCorrectingCitySuggester.class);
	private static final Configuration config = ConfigurationManager.getConfiguration();

	private static final int DEFAULT_INDEX_BUILDING_INTERVAL = 60 * 60; // seconds
	private static final String CONFIG_INDEX_BUILDING_INTERVAL = "INDEX_BUILDING_INTERVAL";
	private static final int k = 3;
	private static final int suggestions = 3;

	private static final Thread indexBuilder = new Thread(new IndexBuilderThread());
	private static final ReentrantLock lock = new ReentrantLock();
	private static SpellingCorrector corrector;

	static {
		indexBuilder.start();
	}

	private static void buildIndex() {
		lock.lock();
		try {
			// ensure transaction
			HibernateUtil.getCurrentSessionWithTransaction();

			logger.info("Updating city index for suggestions");
			IndexBuilder builder = new RelationalIndexBuilder(k, City.class, "name", new PrefixStrategy(k));
			corrector = new KGramLevenshteinSpellingCorrector(k, builder);
		} finally {
			lock.unlock();
		}
	}

	private <T> List<T> applyStartAndRows(List<T> items, int start, Integer rows) {
		if (rows != null) {
			if (items.size() > start + rows) {
				return items.subList(start, start + rows);
			} else if (items.size() > start) {
				return items.subList(start, items.size());
			} else {
				return new ArrayList<>();
			}
		} else {
			return items;
		}
	}

	@Override
	public List<City> suggestDepartures(String text, int start, Integer rows) {
		List<City> cities = suggest(text);
		List<City> departures = super.suggestDepartures(null, 0, null);
		cities.retainAll(departures);
		return applyStartAndRows(cities, start, rows);
	}

	@Override
	public List<City> suggestDestinations(String origin, String text, int start, Integer rows) {
		List<City> cities = suggest(text);
		List<City> destinations = new ArrayList<>();
		try {
			int originId = Integer.valueOf(origin); // origin ID
			destinations = super.suggestDestinations(originId, null, 0, null);
		} catch (NumberFormatException nfe) { // origin name
			List<City> departureCities = suggestDepartures(origin, 0, 1);
			if (!departureCities.isEmpty()) {
				int originId = departureCities.get(0).getId();
				destinations = super.suggestDestinations(originId, null, 0, null);
			}
		}
		cities.retainAll(destinations);
		return applyStartAndRows(cities, start, rows);
	}

	@Override
	public List<City> suggestDestinations(int originId, String text, int start, Integer rows) {
		List<City> cities = suggest(text);
		List<City> destinations = super.suggestDestinations(originId, null, 0, null);
		cities.retainAll(destinations);
		return applyStartAndRows(cities, start, rows);
	}

	private List<City> suggest(String text) {

		if (text == null || text.isEmpty()) {
			return new ArrayList<City>();
		}

		// Get the list of corrections
		List<String> corrections = null;
		lock.lock();
		try {
			corrections = corrector.corrections(text, suggestions);
		} finally {
			lock.unlock();
		}

		// Do not allow for corrections starting from a different letter
		String firstChar = text.substring(0, 1);
		Iterator<String> it = corrections.iterator();
		while (it.hasNext()) {
			String correction = it.next();
			if (!correction.toLowerCase().startsWith(firstChar.toLowerCase())) {
				it.remove();
			}
		}

		// If not corrections, add the original text
		if (corrections.size() == 0) {
			corrections.add(text);
		}

		// TODO make this a stored procedure
		String sql = "SELECT DISTINCT `code`, `id`, `name`, `name_si`, `name_ta`, `active`,`district_id` "
				+ " FROM (SELECT *, 0 AS `priority` FROM `city` WHERE `name` LIKE :pref0";
		for (int a = 1; a < corrections.size(); a++) {
			sql += " UNION SELECT *, " + a + " AS `priority` FROM `city` WHERE `name` LIKE :pref" + a;
		}
		sql += ") A ORDER BY `priority` ASC";

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.createSQLQuery(sql).addEntity(City.class).setParameter("pref0", corrections.get(0) + "%");
		for (int a = 1; a < corrections.size(); a++) {
			query.setParameter("pref" + a, corrections.get(a) + "%");
		}
		@SuppressWarnings("unchecked")
		List<City> cities = query.list();

		return cities;
	}

	private static class IndexBuilderThread implements Runnable {

		@Override
		public void run() {
			buildIndex();
			while (true) {
				try {
					// sleep for a specified time before building index again
					Thread.sleep(config.getLong(CONFIG_INDEX_BUILDING_INTERVAL, DEFAULT_INDEX_BUILDING_INTERVAL) * 1000);
					buildIndex();
				} catch (InterruptedException e) {
					logger.error("Exception in index builder thread", e);
				}
			}
		}
	}
}
