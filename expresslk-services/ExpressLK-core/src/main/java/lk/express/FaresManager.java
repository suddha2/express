package lk.express;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lk.express.bean.Bus;
import lk.express.bean.BusFare;
import lk.express.bean.BusRoute;
import lk.express.bean.City;
import lk.express.db.HibernateUtil;

public class FaresManager {

	private static final Logger logger = LoggerFactory.getLogger(FaresManager.class);

	/**
	 * Retrieves fare for the given criteria
	 * 
	 * @param route
	 *            bus route
	 * @param bus
	 *            bus
	 * @param fromCity
	 *            departure city
	 * @param toCity
	 *            arrival city
	 * @param departureTime
	 *            departure time
	 * @return fare for the given criteria
	 * @throws IrregularFareException
	 *             Throws in case no, multiple or negative fares were found for the
	 *             given criteria
	 */
	public static BusFare getFare(BusRoute route, Bus bus, City fromCity, City toCity, Date departureTime)
			throws IrregularFareException {

		Integer supplierGroupId = null;
		if (bus.getSupplier().getGroup() != null) {
			supplierGroupId = bus.getSupplier().getGroup().getId();
		}

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.createSQLQuery(
				"CALL GetFare(:routeId, :travelClassId, :supplierGroupId, :supplierId, :busId, :fromCityId, :toCityId, :departureTime)")
				.addEntity(BusFare.class).setParameter("routeId", route.getId())
				.setParameter("travelClassId", bus.getTravelClass().getId())
				.setParameter("supplierGroupId", supplierGroupId).setParameter("supplierId", bus.getSupplier().getId())
				.setParameter("busId", bus.getId()).setParameter("fromCityId", fromCity.getId())
				.setParameter("toCityId", toCity.getId()).setParameter("departureTime", departureTime);
		@SuppressWarnings("unchecked")
		List<BusFare> queryResult = query.list();
		BusFare fare = null;
		try {
			if (queryResult.size() == 0) {
				throw new IrregularFareException("No fare found for route: " + route.getId() + ", from: "
						+ fromCity.getId() + ", to: " + toCity.getId() + ", dep: " + departureTime +"  travel class :"+ bus.getTravelClass().getId());
			} else if (queryResult.size() > 1) {
				throw new IrregularFareException("Multiple fares found for route: " + route.getId() + ", from: "
						+ fromCity.getId() + ", to: " + toCity.getId() + ", dep: " + departureTime+"  travel class :"+ bus.getTravelClass().getId());
			}

			fare = queryResult.get(0);

			if (fare.getAdultFare() < 0) {
				throw new IrregularFareException("Negative fare found for route: " + route.getId() + ", from: "
						+ fromCity.getId() + ", to: " + toCity.getId() + ", dep: " + departureTime+"  travel class :"+ bus.getTravelClass().getId());
			}
		} catch (Exception ex) {
			logger.debug("Exception while retrieving fares", ex);
		}
		return fare;
	}
}
