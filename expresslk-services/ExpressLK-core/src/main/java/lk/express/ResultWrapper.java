package lk.express;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import lk.express.admin.Agent;
import lk.express.admin.User;
import lk.express.admin.UserGroup;
import lk.express.db.dao.DAOFactory;

public class ResultWrapper {

	protected static final DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);

	// price related
	public static final String PROP_PRICE = "price";
	public static final String PROP_PRICE_BEFORE_CHARGE = "priceBeforeCharge";
	public static final String PROP_PRICE_BEFORE_TAX = "priceBeforeTax";
	public static final String PROP_GROSS_PRICE = "grossPrice";
	public static final String PROP_COST = "cost";
	public static final String PROP_FARE = "fare";

	public static final String PROP_CHILD_FARE = "childFare";
	public static final String PROP_CHILD_PRICE = "childPrice";
	
	public static final String PROP_INFANT_FARE = "infantFare";

	public static final String PROP_FROM_CITY = "fromCity";
	public static final String PROP_TO_CITY = "toCity";
	public static final String PROP_DEPARTURE_TIME = "departureTime";
	public static final String PROP_ARRIVAL_TIME = "arrivalTime";
	public static final String PROP_MINS_TO_DEPARTURE = "minsToDeparture";
	public static final String PROP_ROUTE_NUMBER = "routeNumber";
	public static final String PROP_ROUND_TRIP = "roundTrip";
	@Deprecated
	public static final String PROP_BUS_DIVISION_ID = "busDivisionId";
	public static final String PROP_BUS_DIVISION = "busDivision";

	public static final String PROP_SOURCE = "source";
	public static final String PROP_IP = "ip";
	public static final String PROP_DISCOUNT_CODE = "discountCode";
	public static final String PROP_USER_GROUPS = "userGroups";
	public static final String PROP_USER_DIVISION = "userDivision";
	public static final String PROP_AGENT_ID = "agentId";

	private ResultSector sector;
	private SearchCriteria criteria;

	public ResultWrapper() {

	}

	public ResultWrapper(SearchCriteria criteria, ResultSector sector) {
		this.criteria = criteria;
		this.sector = sector;
	}

	public void applyCharge(Integer ruleId, Double charge) {
		sector.applyCharge(ruleId, charge);
	}

	public void applyTax(Integer ruleId, Double tax) {
		sector.applyTax(ruleId, tax);
	}

	public void applyDiscount(Integer ruleId, Double discount) {
		sector.applyDiscount(ruleId, discount);
	}

	public void applyMargin(Integer ruleId, Double margin) {
		sector.applyMargin(ruleId, margin);
	}

	public void applyMarkup(Integer ruleId, Double markup) {
		sector.applyMarkup(ruleId, markup);
	}

	public void setCost(Double cost) {
		sector.setCost(cost);
	}

	public void setChildCost(Double cost) {
		sector.setChildCost(cost);
	}

	@RuleProperty(PROP_PRICE)
	public Double getPrice() {
		return sector.getPrice();
	}

	@RuleProperty(PROP_PRICE_BEFORE_CHARGE)
	public Double getPriceBeforeCharges() {
		return sector.getPriceBeforeCharges();
	}

	@RuleProperty(PROP_PRICE_BEFORE_TAX)
	public Double getPriceBeforeTax() {
		return sector.getPriceBeforeTax();
	}

	@RuleProperty(PROP_GROSS_PRICE)
	public Double getGrossPrice() {
		return sector.getGrossPrice();
	}

	@RuleProperty(PROP_COST)
	public Double getCost() {
		return sector.getCost();
	}

	@RuleProperty(PROP_FARE)
	public Double getFare() {
		return sector.getFare();
	}

	@RuleProperty(PROP_CHILD_FARE)
	public Double getChildFare() {
		return sector.getChildFare();
	}
	@RuleProperty(PROP_CHILD_PRICE)
	public Double getChildPrice() {
		return sector.getChildPrice();
	}
//	@RuleProperty(PROP_INFANT_FARE)
//	public Double getInfantFare() {
//		return sector.getInfantFare();
//	}

	@RuleProperty(PROP_FROM_CITY)
	public String fromCity() {
		return sector.getFromCity().getCode();
	}

	@RuleProperty(PROP_TO_CITY)
	public String toCity() {
		return sector.getToCity().getCode();
	}

	@RuleProperty(PROP_DEPARTURE_TIME)
	public Date getDepartureTime() {
		return sector.getDepartureTime();
	}

	@RuleProperty(PROP_ARRIVAL_TIME)
	public Date getArrivalTime() {
		return sector.getArrivalTime();
	}

	@RuleProperty(PROP_MINS_TO_DEPARTURE)
	public long minsToDeparture() {
		return (sector.getDepartureTime().getTime() - (new Date()).getTime()) / (1000 * 60);
	}

	@RuleProperty(PROP_ROUTE_NUMBER)
	public String getRouteNumber() {
		return sector.getSchedule().getBusRoute().getRouteNumber();
	}

	@RuleProperty(PROP_BUS_DIVISION_ID)
	@Deprecated
	public Integer getBusDivisionId() {
		return sector.getSchedule().getBus().getDivision().getId();
	}

	@RuleProperty(PROP_BUS_DIVISION)
	public String getBusDivision() {
		return sector.getSchedule().getBus().getDivision().getCode();
	}

	@RuleProperty(PROP_ROUND_TRIP)
	public boolean isRoundTrip() {
		return criteria.isRoundTrip();
	}

	@RuleProperty(PROP_SOURCE)
	public String getSource() {
		return criteria.getSource();
	}

	@RuleProperty(PROP_IP)
	public String getIp() {
		return criteria.getIp();
	}

	@RuleProperty(PROP_DISCOUNT_CODE)
	public String getDiscountCode() {
		return criteria.getDiscountCode();
	}

	@RuleProperty(PROP_USER_GROUPS)
	public Collection<String> getUserGroups() {
		Collection<String> groups = new HashSet<>();
		User user = Context.getSessionData().getUser();
		if (user == null) {
			return groups;
		}
		for (UserGroup ug : user.getUserGroups()) {
			groups.add(ug.getCode());
		}
		return groups;
	}

	@RuleProperty(PROP_USER_DIVISION)
	public String getUserDivision() {
		User user = Context.getSessionData().getUser();
		return user.getDivision().getCode();
	}

	@RuleProperty(PROP_AGENT_ID)
	public Integer getAgent() {
		User user = Context.getSessionData().getUser();
		Agent agent = user.getAgent();
		if (agent != null) {
			return agent.getId();
		}
		return -1;
	}

	public ResultSector getSector() {
		return sector;
	}

	public SearchCriteria getCriteria() {
		return criteria;
	}
}
