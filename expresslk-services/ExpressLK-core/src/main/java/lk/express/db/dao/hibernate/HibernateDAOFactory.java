package lk.express.db.dao.hibernate;

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
import lk.express.db.HibernateUtil;
import lk.express.db.dao.DAOFactory;
import lk.express.db.dao.GenericDAO;
import lk.express.db.dao.HasCodeDAO;
import lk.express.db.dao.HasValidPeriodDAO;
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

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class HibernateDAOFactory extends DAOFactory {

	@Override
	public HasCodeDAO<City> getCityDAO() {
		return (HasCodeDAO<City>) instantiateDAO(CityDAOHibernate.class);
	}

	@Override
	public HasCodeDAO<District> getDistrictDAO() {
		return (HasCodeDAO<District>) instantiateDAO(DistrictDAOHibernate.class);
	}

	@Override
	public HasCodeDAO<Province> getProvinceDAO() {
		return (HasCodeDAO<Province>) instantiateDAO(ProvinceDAOHibernate.class);
	}

	@Override
	public HasCodeDAO<Country> getCountryDAO() {
		return (HasCodeDAO<Country>) instantiateDAO(CountryDAOHibernate.class);
	}

	@Override
	public HasCodeDAO<BookingStatus> getBookingStatusDAO() {
		return (HasCodeDAO<BookingStatus>) instantiateDAO(BookingStatusDAOHibernate.class);
	}

	@Override
	public HasCodeDAO<ChangeType> getChangeTypeDAO() {
		return (HasCodeDAO<ChangeType>) instantiateDAO(BookingItemChangeTypeDAOHibernate.class);
	}

	@Override
	public HasCodeDAO<OperationalStage> getOperationalStageDAO() {
		return (HasCodeDAO<OperationalStage>) instantiateDAO(OperationalStageDAOHibernate.class);
	}

	@Override
	public HasUniqueHibernateDAO<Person> getPersonDAO() {
		return (HasUniqueHibernateDAO<Person>) instantiateDAO(PersonDAOHibernate.class);
	}

	@Override
	public HasUniqueHibernateDAO<User> getUserDAO() {
		return (HasUniqueHibernateDAO<User>) instantiateDAO(UserDAOHibernate.class);
	}

	@Override
	public HasUniqueHibernateDAO<Staff> getStaffDAO() {
		return (HasUniqueHibernateDAO<Staff>) instantiateDAO(StaffDAOHibernate.class);
	}

	@Override
	public GenericDAO<Client> getClientDAO() {
		return instantiateDAO(ClientDAOHibernate.class);
	}

	@Override
	public GenericDAO<Agent> getAgentDAO() {
		return instantiateDAO(AgentDAOHibernate.class);
	}

	@Override
	public GenericDAO<PasswordResetToken> getPasswordResetTokenDAO() {
		return instantiateDAO(PasswordResetTokenDAOHibernate.class);
	}

	@Override
	public GenericDAO<BusSchedule> getBusScheduleDAO() {
		return instantiateDAO(BusScheduleDAOHibernate.class);
	}

	@Override
	public GenericDAO<BusStop> getBusStopDAO() {
		return instantiateDAO(BusStopDAOHibernate.class);
	}

	@Override
	public GenericDAO<Booking> getBookingDAO() {
		return instantiateDAO(BookingDAOHibernate.class);
	}

	@Override
	public GenericDAO<BookingItem> getBookingItemDAO() {
		return instantiateDAO(BookingItemDAOHibernate.class);
	}

	@Override
	public GenericDAO<BusHeldItem> getHeldBusItemDAO() {
		return instantiateDAO(HeldBusItemDAOHibernate.class);
	}

	@Override
	public GenericDAO<BusRoute> getBusRouteDAO() {
		return instantiateDAO(BusRouteDAOHibernate.class);
	}

	@Override
	public GenericDAO<BookingItemPassenger> getBookingItemPassengerDAO() {
		return instantiateDAO(BookingItemPassengerDAOHibernate.class);
	}

	@Override
	public GenericDAO<Supplier> getSupplierDAO() {
		return instantiateDAO(SupplierDAOHibernate.class);
	}

	@Override
	public GenericDAO<ReportType> getReportTypeDAO() {
		return instantiateDAO(ReportTypeDAOHibernate.class);
	}

	@Override
	public GenericDAO<BusScheduleBusStop> getBusScheduleBusStopDAO() {
		return instantiateDAO(BusScheduleBusStopDAOHibernate.class);
	}

	@Override
	public HasValidPeriodDAO<RuleChargeRule> getChargeRuleDAO() {
		return (HasValidPeriodDAO<RuleChargeRule>) instantiateDAO(RuleChargeRuleDAOHibernate.class);
	}

	@Override
	public HasValidPeriodDAO<RuleTaxRule> getTaxRuleDAO() {
		return (HasValidPeriodDAO<RuleTaxRule>) instantiateDAO(RuleTaxRuleDAOHibernate.class);
	}

	@Override
	public HasValidPeriodDAO<RuleDiscountRule> getDiscountRuleDAO() {
		return (HasValidPeriodDAO<RuleDiscountRule>) instantiateDAO(RuleDiscountRuleDAOHibernate.class);
	}

	@Override
	public HasValidPeriodDAO<RuleMarkupRule> getMarkupRuleDAO() {
		return (HasValidPeriodDAO<RuleMarkupRule>) instantiateDAO(RuleMarkupRuleDAOHibernate.class);
	}

	@Override
	public HasValidPeriodDAO<RuleCancellationRule> getCancellationRuleDAO() {
		return (HasValidPeriodDAO<RuleCancellationRule>) instantiateDAO(RuleCancellationRuleDAOHibernate.class);
	}

	@Override
	public HasValidPeriodDAO<BusFare> getFareDAO() {
		return (HasValidPeriodDAO<BusFare>) instantiateDAO(FareDAOHibernate.class);
	}

	@Override
	public GenericDAO<Service> getServiceDAO() {
		return instantiateDAO(ServiceDAOHibernate.class);
	}

	@Override
	public GenericDAO<PaymentRefund> getPaymentRefundDAO() {
		return instantiateDAO(PaymentRefundDAOHibernate.class);
	}

	@Override
	public GenericDAO<Coupon> getCouponDAO() {
		return instantiateDAO(CouponDAOHibernate.class);
	}

	@Override
	public GenericDAO<DiscountCode> getDiscountCodeDAO() {
		return instantiateDAO(DiscountCodeDAOHibernate.class);
	}

	@Override
	public <T extends Entity> GenericDAO<T> getGenericDAO(Class<T> clazz) {
		GenericHibernateDAO<T> dao = null;
		try {
			dao = new GenericHibernateDAO<T>(clazz);
			dao.setSession(getCurrentSession());
			return dao;
		} catch (Exception ex) {
			throw new DBException("Cannot instantiate DAO: " + dao.getClass(), ex);
		}
	}

	@Override
	public <T extends Entity & HasNameCode> HasCodeDAO<T> getHasCodeDAO(Class<T> clazz) {
		HasCodeHibernateDAO<T> dao = null;
		try {
			dao = new HasCodeHibernateDAO<T>(clazz);
			dao.setSession(getCurrentSession());
			return dao;
		} catch (Exception ex) {
			throw new DBException("Cannot instantiate DAO: " + dao.getClass(), ex);
		}
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T extends HasNameCode> HasCodeDAO<T> getHasCodeDAOWorkaround(Class<T> clazz) {
		HasCodeHibernateDAO dao = null;
		try {
			dao = new HasCodeHibernateDAO(clazz);
			dao.setSession(getCurrentSession());
			return dao;
		} catch (Exception ex) {
			throw new DBException("Cannot instantiate DAO: " + dao.getClass(), ex);
		}
	}

	private static <T extends Entity> GenericDAO<T> instantiateDAO(Class<? extends GenericHibernateDAO<T>> daoClass) {
		try {
			GenericHibernateDAO<T> dao = daoClass.newInstance();
			dao.setSession(getCurrentSession());
			return dao;
		} catch (Exception ex) {
			throw new DBException("Cannot instantiate DAO: " + daoClass, ex);
		}
	}

	protected static Session getCurrentSession() {
		return HibernateUtil.getCurrentSession();
	}

	// Inline concrete DAO implementations with no business-related data access
	// methods. If we use public static nested classes, we can centralize all of
	// them in one source file.
	public static class CityDAOHibernate extends HasCodeHibernateDAO<City> {
	}

	public static class DistrictDAOHibernate extends HasCodeHibernateDAO<District> {
	}

	public static class ProvinceDAOHibernate extends HasCodeHibernateDAO<Province> {
	}

	public static class CountryDAOHibernate extends HasCodeHibernateDAO<Country> {
	}

	public static class BookingStatusDAOHibernate extends HasCodeHibernateDAO<BookingStatus> {
	}

	public static class BookingItemChangeTypeDAOHibernate extends HasCodeHibernateDAO<ChangeType> {
	}

	public static class OperationalStageDAOHibernate extends HasCodeHibernateDAO<OperationalStage> {
	}

	public static class PersonDAOHibernate extends HasUniqueHibernateDAO<Person> {

		@Override
		public Person getUnique(Object unique) {
			String nic = (String) unique;
			Session session = HibernateUtil.getCurrentSession();
			return (Person) session.createCriteria(Person.class).add(Restrictions.eq("nic", nic)).uniqueResult();
		}
	}

	public static class UserDAOHibernate extends HasUniqueHibernateDAO<User> {

		@Override
		public User getUnique(Object unique) {
			String username = (String) unique;
			Session session = HibernateUtil.getCurrentSession();
			return (User) session.createCriteria(User.class).add(Restrictions.eq("username", username)).uniqueResult();
		}
	}

	public static class StaffDAOHibernate extends HasUniqueHibernateDAO<Staff> {

		@Override
		public Staff getUnique(Object unique) {
			String username = (String) unique;
			Session session = HibernateUtil.getCurrentSession();
			Staff staff = (Staff) session.createQuery("FROM Staff staff WHERE staff.user.username = :username")
					.setParameter("username", username).uniqueResult();
			if (staff != null) {
				return staff;
			}
			User user = ((HasUniqueHibernateDAO<User>) instantiateDAO(UserDAOHibernate.class)).getUnique(username);
			if (user != null) {
				return new Staff(user);
			} else {
				return null;
			}
		}
	}

	public static class ClientDAOHibernate extends GenericHibernateDAO<Client> {
	}

	public static class AgentDAOHibernate extends GenericHibernateDAO<Agent> {
	}

	public static class PasswordResetTokenDAOHibernate extends GenericHibernateDAO<PasswordResetToken> {
	}

	public static class BusScheduleDAOHibernate extends GenericHibernateDAO<BusSchedule> {
	}

	public static class BusStopDAOHibernate extends GenericHibernateDAO<BusStop> {
	}

	public static class BookingDAOHibernate extends GenericHibernateDAO<Booking> {
	}

	public static class BookingItemDAOHibernate extends GenericHibernateDAO<BookingItem> {
	}

	public static class HeldBusItemDAOHibernate extends GenericHibernateDAO<BusHeldItem> {
	}

	public static class BusRouteDAOHibernate extends GenericHibernateDAO<BusRoute> {
	}

	public static class BookingItemPassengerDAOHibernate extends GenericHibernateDAO<BookingItemPassenger> {
	}

	public static class SupplierDAOHibernate extends GenericHibernateDAO<Supplier> {
	}

	public static class BusScheduleBusStopDAOHibernate extends GenericHibernateDAO<BusScheduleBusStop> {
	}

	public static class RuleChargeRuleDAOHibernate extends HasValidPeriodHibernateDAO<RuleChargeRule> {
	}

	public static class RuleTaxRuleDAOHibernate extends HasValidPeriodHibernateDAO<RuleTaxRule> {
	}

	public static class RuleDiscountRuleDAOHibernate extends HasValidPeriodHibernateDAO<RuleDiscountRule> {
	}

	public static class RuleMarkupRuleDAOHibernate extends HasValidPeriodHibernateDAO<RuleMarkupRule> {
	}

	public static class RuleCancellationRuleDAOHibernate extends HasValidPeriodHibernateDAO<RuleCancellationRule> {
	}

	public static class FareDAOHibernate extends HasValidPeriodHibernateDAO<BusFare> {
	}

	public static class ServiceDAOHibernate extends GenericHibernateDAO<Service> {
	}

	public static class PaymentRefundDAOHibernate extends GenericHibernateDAO<PaymentRefund> {
	}

	public static class ReportTypeDAOHibernate extends GenericHibernateDAO<ReportType> {
	}

	public static class CouponDAOHibernate extends GenericHibernateDAO<Coupon> {
	}

	public static class DiscountCodeDAOHibernate extends GenericHibernateDAO<DiscountCode> {
	}
}
