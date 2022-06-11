package lk.express.reporting.reports;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.express.Context;
import lk.express.admin.Company;
import lk.express.admin.User;
import lk.express.db.HibernateUtil;
import lk.express.reporting.Report;
import lk.express.reporting.ReportCriteria;
import lk.express.reporting.ReportData;
import lk.express.reporting.ReportGenerator;
import lk.express.reporting.ReportInfo;
import lk.express.reports.ReportParameter;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

public abstract class BusBookingSummaryReport extends Report {

	public static final String PARAM_BUS_REG = "Plate number";
	public static final String PARAM_FROM_DATE = "From date";
	public static final String PARAM_TO_DATE = "To date";
	public static final String PARAM_SCHEDULE_ID = "SCHEDULE_ID";

	public BusBookingSummaryReport(ReportGenerator generator, ReportCriteria criteria) {
		super(generator, criteria);
	}

	@Override
	public ReportInfo generateReport() throws Exception {

		Session dbSession = HibernateUtil.getCurrentSession();

		Query query = null;
		Date fromDate = null;
		Date toDate = null;
		Integer scheduleId = null;
		ReportParameter scheduleIdParam = criteria.getParameter(PARAM_SCHEDULE_ID);
		if (scheduleIdParam.getObject() == null) {
			String regNo = (String) criteria.getParameter(PARAM_BUS_REG).getObject();
			fromDate = (Date) criteria.getParameter(PARAM_FROM_DATE).getObject();
			toDate = (Date) criteria.getParameter(PARAM_TO_DATE).getObject();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT b.id AS busId, b.name AS busName, b.plate_number AS regNo, c1.name"
					+ criteria.getLangSuffix() + " AS fromCity, c2.name" + criteria.getLangSuffix()
					+ " AS toCity, s.name AS supplierName ");
			sql.append("FROM bus AS b ");
			sql.append("JOIN bus_schedule AS bs ON bs.bus_id = b.id ");
			sql.append("JOIN bus_route AS br on br.id = bs.bus_route_id ");
			sql.append("JOIN city AS c1 on c1.id = br.from_city_id ");
			sql.append("JOIN city AS c2 on c2.id = br.to_city_id ");
			sql.append("JOIN supplier AS s on s.id = b.supplier_id ");
			sql.append("WHERE b.plate_number = :regNo GROUP BY b.plate_number");

			query = dbSession.createSQLQuery(sql.toString()).setParameter("regNo", regNo)
					.setResultTransformer(Transformers.aliasToBean(BookingSummary.class));
		} else {
			scheduleId = (Integer) scheduleIdParam.getObject();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT b.id AS busId, b.name AS busName, b.plate_number AS regNo, c1.name"
					+ criteria.getLangSuffix() + " AS fromCity, c2.name" + criteria.getLangSuffix()
					+ " AS toCity, s.name AS supplierName ");
			sql.append("FROM bus AS b ");
			sql.append("JOIN bus_schedule AS bs ON bs.bus_id = b.id ");
			sql.append("JOIN bus_route AS br on br.id = bs.bus_route_id ");
			sql.append("JOIN city AS c1 on c1.id = br.from_city_id ");
			sql.append("JOIN city AS c2 on c2.id = br.to_city_id ");
			sql.append("JOIN supplier AS s on s.id = b.supplier_id ");
			sql.append("WHERE bs.id = :scheduleId GROUP BY b.plate_number");

			query = dbSession.createSQLQuery(sql.toString()).setParameter("scheduleId", scheduleId)
					.setResultTransformer(Transformers.aliasToBean(BookingSummary.class));
		}
		@SuppressWarnings("unchecked")
		List<BookingSummary> reports = query.list();

		BookingSummary report = null;
		if (reports != null && reports.size() > 0) {
			report = reports.get(0);
			report.setSectorSummary(getSectorSummaryList(dbSession, report.getBusId(), scheduleId, fromDate, toDate));

			User user = Context.getSessionData().getUser();
			Company company = user.getDivision().getCompany();
			report.setLogoFile(company.getLogoFile());
		}

		return buildReport(report);
	}

	private List<SectorSummary> getSectorSummaryList(Session dbSession, double busId, Integer scheduleId,
			Date fromDate, Date toDate) {

		String where = null;
		if (scheduleId == null) {
			where = "bs.bus_id = :busId AND bs.departure_time >= :fromDate AND bs.departure_time <= :toDate";
		} else {
			where = "bs.id = :scheduleId";
		}

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.fromCityName, a.toCityName, b.paidSeats, b.unpaidSeats, a.totalPrice, IFNULL(b.paymentMethod, 'Unpaid') AS paymentMethod ");
		sql.append("FROM ( ");
		sql.append("    SELECT bi.schedule_id, c1.id AS from_city_id, c1.name" + criteria.getLangSuffix()
				+ " AS fromCityName, c2.id AS to_city_id, c2.name" + criteria.getLangSuffix()
				+ " AS toCityName, SUM(bi.cost) AS totalPrice, p.mode AS paymentMethod ");
		sql.append("    FROM bus_schedule AS bs ");
		sql.append("    JOIN booking_item AS bi on bi.schedule_id = bs.id ");
		sql.append("    JOIN bus_stop AS bs1 ON bs1.id = bi.from_bus_stop_id ");
		sql.append("    JOIN city AS c1 on c1.id = bs1.city_id ");
		sql.append("    JOIN bus_stop bs2 ON bs2.id = bi.to_bus_stop_id ");
		sql.append("    JOIN city AS c2 on c2.id = bs2.city_id ");
		sql.append("    LEFT JOIN ( ");
		sql.append("        SELECT p.id, p.booking_id, pr.mode FROM payment AS p JOIN payment_refund AS pr ON p.id = pr.id ");
		sql.append("    ) AS p ON p.booking_id = bi.booking_id ");
		sql.append("    WHERE " + where);
		sql.append("    AND bi.status_code = 'CONF' ");
		sql.append("    GROUP BY c1.name, c2.name, p.mode ");
		sql.append(") AS a ");
		sql.append("JOIN ( ");
		sql.append("   SELECT bi.schedule_id, c1.id AS from_city_id, c2.id AS to_city_id, COUNT(IF(bi.cost > 0, 1, NULL)) AS paidSeats, COUNT(IF(bi.cost = 0, 1, NULL)) AS unpaidSeats, p.mode AS paymentMethod ");
		sql.append("    FROM bus_schedule AS bs ");
		sql.append("    JOIN booking_item AS bi on bi.schedule_id = bs.id ");
		sql.append("    JOIN booking_item_passenger AS bip on bip.booking_item_id = bi.id ");
		sql.append("    JOIN bus_stop AS bs1 ON bs1.id = bi.from_bus_stop_id ");
		sql.append("    JOIN city AS c1 ON c1.id = bs1.city_id ");
		sql.append("    JOIN bus_stop bs2 ON bs2.id = bi.to_bus_stop_id ");
		sql.append("    JOIN city AS c2 on c2.id = bs2.city_id ");
		sql.append("    LEFT JOIN ( ");
		sql.append("        SELECT p.id, p.booking_id, pr.mode FROM payment AS p JOIN payment_refund AS pr ON p.id = pr.id ");
		sql.append("    ) AS p ON p.booking_id = bi.booking_id ");
		sql.append("    WHERE " + where);
		sql.append("    AND bi.status_code = 'CONF' ");
		sql.append("    GROUP BY c1.name, c2.name, p.mode ");
		sql.append(") AS b ");
		sql.append("ON a.schedule_id = b.schedule_id ");
		sql.append("AND a.from_city_id = b.from_city_id ");
		sql.append("AND a.to_city_id = b.to_city_id	");
		sql.append("AND (a.paymentMethod = b.paymentMethod OR (a.paymentMethod IS NULL AND b.paymentMethod IS NULL))");

		Query query = null;
		if (scheduleId == null) {
			query = dbSession.createSQLQuery(sql.toString()).setParameter("busId", busId)
					.setParameter("fromDate", fromDate).setParameter("toDate", toDate)
					.setResultTransformer(Transformers.aliasToBean(SectorSummary.class));
		} else {
			query = dbSession.createSQLQuery(sql.toString()).setParameter("scheduleId", scheduleId)
					.setResultTransformer(Transformers.aliasToBean(SectorSummary.class));
		}

		@SuppressWarnings("unchecked")
		List<SectorSummary> reports = query.list();

		return reports;
	}

	public static final class BookingSummary extends ReportData {

		private String supplierName;
		private String regNo;
		private String fromCity;
		private String toCity;
		private String busName;
		private int busId;
		private List<SectorSummary> sectorSummary;

		public List<SectorSummary> getSectorSummary() {
			if (sectorSummary == null) {
				sectorSummary = new ArrayList<>();
			}
			return sectorSummary;
		}

		public void setSectorSummary(List<SectorSummary> sectorSummary) {
			this.sectorSummary = sectorSummary;
		}

		public String getRegNo() {
			return regNo;
		}

		public void setRegNo(String regNo) {
			this.regNo = regNo;
		}

		public String getFromCity() {
			return fromCity;
		}

		public void setFromCity(String fromCity) {
			this.fromCity = fromCity;
		}

		public String getToCity() {
			return toCity;
		}

		public void setToCity(String toCity) {
			this.toCity = toCity;
		}

		public String getBusName() {
			return busName;
		}

		public void setBusName(String busName) {
			this.busName = busName;
		}

		public String getSupplierName() {
			return supplierName;
		}

		public void setSupplierName(String supplierName) {
			this.supplierName = supplierName;
		}

		public String getTurns() {
			return Integer.toString(getSectorSummary().size());
		}

		public void setTurns(String turns) {
		}

		public int getBusId() {
			return busId;
		}

		public void setBusId(int busId) {
			this.busId = busId;
		}
	}

	public static final class SectorSummary {

		private String fromCityName;
		private String toCityName;
		private BigInteger paidSeats;
		private BigInteger unpaidSeats;
		private double totalPrice;
		private String paymentMethod;

		public String getFromCityName() {
			return fromCityName;
		}

		public void setFromCityName(String fromCityName) {
			this.fromCityName = fromCityName;
		}

		public String getToCityName() {
			return toCityName;
		}

		public void setToCityName(String toCityname) {
			this.toCityName = toCityname;
		}

		public BigInteger getPaidSeats() {
			return paidSeats;
		}

		public void setPaidSeats(BigInteger paidSeats) {
			this.paidSeats = paidSeats;
		}

		public BigInteger getUnpaidSeats() {
			return unpaidSeats;
		}

		public void setUnpaidSeats(BigInteger unpaidSeats) {
			this.unpaidSeats = unpaidSeats;
		}

		public double getTotalPrice() {
			return totalPrice;
		}

		public void setTotalPrice(double totalPrice) {
			this.totalPrice = totalPrice;
		}

		public String getPaymentMethod() {
			return paymentMethod;
		}

		public void setPaymentMethod(String paymentMethod) {
			this.paymentMethod = paymentMethod;
		}
	}
}
