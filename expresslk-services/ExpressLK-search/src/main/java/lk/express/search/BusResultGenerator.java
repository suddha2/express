package lk.express.search;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import lk.express.FaresManager;
import lk.express.IrregularFareException;
import lk.express.LegResult;
import lk.express.PricingManager;
import lk.express.Result;
import lk.express.ResultLeg;
import lk.express.ResultSector;
import lk.express.ResultWrapper;
import lk.express.SearchCriteria;
import lk.express.bean.Bus;
import lk.express.bean.BusFare;
import lk.express.bean.BusRoute;
import lk.express.bean.City;
import lk.express.bean.ScheduleSector;
import lk.express.db.dao.DAOFactory;
import lk.express.db.dao.GenericDAO;
import lk.express.schedule.BusSchedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusResultGenerator extends ResultGenerator {

	private static final DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);
	private static final Logger logger = LoggerFactory.getLogger(BusResultGenerator.class);

	private List<ScheduleSector> outBoundScheduleSectors;
	private List<ScheduleSector> inBoundScheduleSectors;
	private PricingManager pricingManager;

	public BusResultGenerator(SearchCriteria criteria, List<ScheduleSector> outBoundScheduleSectors,
			List<ScheduleSector> inBoundScheduleSectors, PricingManager pricingManager) {
		super(criteria);
		this.outBoundScheduleSectors = outBoundScheduleSectors;
		this.inBoundScheduleSectors = inBoundScheduleSectors;
		this.pricingManager = pricingManager;

		generateResult();
	}

	@Override
	protected void generateResult() {
		LegResult out = new LegResult();
		int i = 0;
		for (ScheduleSector outScheduleSector : outBoundScheduleSectors) {
			ResultSector outSector = createSector(outScheduleSector);
			if (outSector != null) {
				pricingManager.price(new ResultWrapper(criteria, outSector));
				ResultLeg outLeg = new ResultLeg();
				outLeg.setResultIndex(Result.OUT_BOUND_PREFIX + i++);
				outLeg.setSectors(Arrays.asList(outSector));
				outLeg.arrangeCostPrices();

				out.getLegs().add(outLeg);
			}
		}
		outLegResult = out;

		i = 0;
		if (inBoundScheduleSectors != null) {
			LegResult in = new LegResult();
			for (ScheduleSector inScheduleSector : inBoundScheduleSectors) {
				ResultSector inSector = createSector(inScheduleSector);
				if (inSector != null) {
					pricingManager.price(new ResultWrapper(criteria, inSector));
					ResultLeg inLeg = new ResultLeg();
					inLeg.setResultIndex(Result.IN_BOUND_PREFIX + i++);
					inLeg.setSectors(Arrays.asList(inSector));
					inLeg.arrangeCostPrices();

					in.getLegs().add(inLeg);
				}
			}
			inLegResult = in;
		}
	}

	private ResultSector createSector(ScheduleSector scheduleSector) {
		GenericDAO<City> cityDAO = daoFac.getCityDAO();
		GenericDAO<BusSchedule> scheduleDAO = daoFac.getBusScheduleDAO();

		BusSchedule schedule = scheduleDAO.get(scheduleSector.getScheduleId());
		City fromCity = cityDAO.get(scheduleSector.getFromCityId());
		City toCity = cityDAO.get(scheduleSector.getToCityId());
		BusFare fare = getFare(schedule.getBusRoute(), schedule.getBus(), fromCity, toCity,
				scheduleSector.getDepartureTime());

		if (fare == null || fare.getAdultFare() <= 0) {
			return null;
		}

		ResultSector sector = new ResultSector();
		sector.setSchedule(schedule);
		sector.setFromCity(fromCity);
		sector.setToCity(toCity);
		sector.setDepartureTime(scheduleSector.getDepartureTime());
		sector.setArrivalTime(scheduleSector.getArrivalTime());
		sector.setFare(fare.getAdultFare());
		sector.setChildFare(fare.getChildFare());

		return sector;
	}

	private BusFare getFare(BusRoute route, Bus bus, City fromCity, City toCity, Date departureTime) {
		BusFare fare = null;
		try {
			fare = FaresManager.getFare(route, bus, fromCity, toCity, departureTime);
			// adultFare = fare.getAdultFare();
		} catch (IrregularFareException e) {
			logger.debug("Exception while retrieving fares", e);
		}

		return fare;
	}
}
