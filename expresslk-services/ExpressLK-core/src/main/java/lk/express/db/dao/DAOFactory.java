package lk.express.db.dao;

import lk.express.admin.Agent;
import lk.express.admin.PasswordResetToken;
import lk.express.admin.Person;
import lk.express.admin.Staff;
import lk.express.admin.User;
import lk.express.bean.BusFare;
import lk.express.bean.BusHeldItem;
import lk.express.bean.BusRoute;
import lk.express.bean.BusStop;
import lk.express.bean.City;
import lk.express.bean.Client;
import lk.express.bean.Country;
import lk.express.bean.Coupon;
import lk.express.bean.DiscountCode;
import lk.express.bean.District;
import lk.express.bean.Entity;
import lk.express.bean.HasNameCode;
import lk.express.bean.OperationalStage;
import lk.express.bean.PaymentRefund;
import lk.express.bean.Province;
import lk.express.bean.Service;
import lk.express.db.DBException;
import lk.express.db.dao.hibernate.HasUniqueHibernateDAO;
import lk.express.db.dao.hibernate.HibernateDAOFactory;
import lk.express.reports.ReportType;
import lk.express.reservation.Booking;
import lk.express.reservation.BookingItem;
import lk.express.reservation.BookingItemPassenger;
import lk.express.reservation.BookingStatus;
import lk.express.reservation.ChangeType;
import lk.express.rule.bean.RuleCancellationRule;
import lk.express.rule.bean.RuleChargeRule;
import lk.express.rule.bean.RuleDiscountRule;
import lk.express.rule.bean.RuleMarkupRule;
import lk.express.rule.bean.RuleTaxRule;
import lk.express.schedule.BusSchedule;
import lk.express.schedule.BusScheduleBusStop;
import lk.express.supplier.Supplier;

public abstract class DAOFactory {

	/**
	 * Creates a stand alone DAOFactory that returns unmanaged DAO beans for use
	 * in any environment Hibernate has been configured for. Uses
	 * HibernateUtil/SessionFactory and Hibernate context propagation
	 * (CurrentSessionContext), thread-bound or transaction-bound, and
	 * transaction scoped.
	 */
	public static final Class<HibernateDAOFactory> HIBERNATE = HibernateDAOFactory.class;

	/**
	 * Factory method for instantiation of concrete factories.
	 */
	public static DAOFactory instance(Class<? extends DAOFactory> factory) {
		try {
			return factory.newInstance();
		} catch (Exception ex) {
			throw new DBException("Couldn't create DAOFactory: " + factory);
		}
	}

	// Add your DAO interfaces here
	public abstract <T extends Entity> GenericDAO<T> getGenericDAO(Class<T> clazz);

	public abstract <T extends Entity & HasNameCode> HasCodeDAO<T> getHasCodeDAO(Class<T> clazz);

	public abstract <T extends HasNameCode> HasCodeDAO<T> getHasCodeDAOWorkaround(Class<T> clazz);

	public abstract HasCodeDAO<City> getCityDAO();

	public abstract HasCodeDAO<District> getDistrictDAO();

	public abstract HasCodeDAO<Province> getProvinceDAO();

	public abstract HasCodeDAO<Country> getCountryDAO();

	public abstract HasCodeDAO<BookingStatus> getBookingStatusDAO();

	public abstract HasCodeDAO<ChangeType> getChangeTypeDAO();

	public abstract HasCodeDAO<OperationalStage> getOperationalStageDAO();

	public abstract HasUniqueHibernateDAO<Person> getPersonDAO();

	public abstract HasUniqueHibernateDAO<User> getUserDAO();

	public abstract HasUniqueHibernateDAO<Staff> getStaffDAO();

	public abstract GenericDAO<Client> getClientDAO();

	public abstract GenericDAO<Agent> getAgentDAO();

	public abstract GenericDAO<PasswordResetToken> getPasswordResetTokenDAO();

	public abstract GenericDAO<BusSchedule> getBusScheduleDAO();

	public abstract GenericDAO<BusStop> getBusStopDAO();

	public abstract GenericDAO<Booking> getBookingDAO();

	public abstract GenericDAO<BookingItem> getBookingItemDAO();

	public abstract GenericDAO<BusHeldItem> getHeldBusItemDAO();

	public abstract GenericDAO<BusRoute> getBusRouteDAO();

	public abstract GenericDAO<BookingItemPassenger> getBookingItemPassengerDAO();

	public abstract GenericDAO<Supplier> getSupplierDAO();

	public abstract GenericDAO<BusScheduleBusStop> getBusScheduleBusStopDAO();

	public abstract HasValidPeriodDAO<RuleChargeRule> getChargeRuleDAO();

	public abstract HasValidPeriodDAO<RuleTaxRule> getTaxRuleDAO();

	public abstract HasValidPeriodDAO<RuleDiscountRule> getDiscountRuleDAO();

	public abstract HasValidPeriodDAO<RuleMarkupRule> getMarkupRuleDAO();

	public abstract HasValidPeriodDAO<RuleCancellationRule> getCancellationRuleDAO();

	public abstract HasValidPeriodDAO<BusFare> getFareDAO();

	public abstract GenericDAO<Service> getServiceDAO();

	public abstract GenericDAO<PaymentRefund> getPaymentRefundDAO();

	public abstract GenericDAO<ReportType> getReportTypeDAO();

	public abstract GenericDAO<Coupon> getCouponDAO();

	public abstract GenericDAO<DiscountCode> getDiscountCodeDAO();
}
