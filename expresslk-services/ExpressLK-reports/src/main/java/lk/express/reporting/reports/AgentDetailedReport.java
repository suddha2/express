package lk.express.reporting.reports;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lk.express.db.HibernateUtil;
import lk.express.reporting.Report;
import lk.express.reporting.ReportCriteria;
import lk.express.reporting.ReportData;
import lk.express.reporting.ReportGenerator;
import lk.express.reporting.ReportInfo;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

public abstract class AgentDetailedReport extends Report {

	public static final String PARAM_START_TIME = "Start time";
	public static final String PARAM_END_TIME = "End time";
	public static final String PARAM_AGENT = "Agent";

	public AgentDetailedReport(ReportGenerator generator, ReportCriteria criteria) {
		super(generator, criteria);
	}

	@Override
	public ReportInfo generateReport() throws Exception {

		Map<String, Object> paramMap = getParameters();
		Date start = (Date) paramMap.get(PARAM_START_TIME);
		Date end = (Date) paramMap.get(PARAM_END_TIME);
		Integer agentId = (Integer) paramMap.get(PARAM_AGENT);

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append("	`booking`.`booking_time` AS `bookingTime`, ");
		sql.append("	`user`.`username` AS `username`, ");
		sql.append("	`client`.`mobile_telephone` AS `mobile`, ");
		sql.append("	`bus`.`plate_number` AS `bus`, ");
		sql.append("	`dep`.`name` AS `fromCity`, ");
		sql.append("	`arr`.`name` AS `toCity`, ");
		sql.append("	COUNT(*) AS `seats`, ");
		sql.append("	`booking_item`.`price` AS `price`, ");
		sql.append("	`booking_item`.`price_before_charge` AS `payable` ");
		sql.append("FROM `booking` ");
		sql.append("JOIN `user` ON `user`.`id` = `booking`.`user_id` ");
		sql.append("JOIN `client` ON `client`.`id` = `booking`.`client_id` ");
		sql.append("JOIN `booking_item` ON `booking_item`.`booking_id` = `booking`.`id` ");
		sql.append("JOIN `bus_stop` AS `depStop` ON `depStop`.`id` = `booking_item`.`from_bus_stop_id` ");
		sql.append("JOIN `city` AS `dep` ON `dep`.`id` = `depStop`.`city_id` ");
		sql.append("JOIN `bus_stop` AS `arrStop` ON `arrStop`.`id` = `booking_item`.`to_bus_stop_id` ");
		sql.append("JOIN `city` AS `arr` ON `arr`.`id` = `arrStop`.`city_id` ");
		sql.append("JOIN `bus_schedule` ON `bus_schedule`.`id` = `booking_item`.`schedule_id` ");
		sql.append("JOIN `bus` ON `bus`.`id` = `bus_schedule`.`bus_id` ");
		sql.append("JOIN `booking_item_passenger` ON `booking_item_passenger`.`booking_item_id` = `booking_item`.`id` ");
		sql.append("WHERE `booking`.`agent_id` = :agentId ");
		sql.append("AND `booking`.`booking_time` >= :start ");
		sql.append("AND `booking`.`booking_time` <= :end ");
		sql.append("AND (`booking`.`status_code` = 'CONF' OR (`booking`.`status_code` = 'CANC' AND `booking`.`cancellation_cause` = 'ClientRequested') ) ");
		sql.append("GROUP BY `booking`.`id` ");
		sql.append("ORDER BY `booking`.`booking_time`;");

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.createSQLQuery(sql.toString()).setParameter("agentId", agentId)
				.setParameter("start", start).setParameter("end", end)
				.setResultTransformer(Transformers.aliasToBean(AgentDetailedResultRow.class));
		@SuppressWarnings("unchecked")
		List<AgentDetailedResultRow> result = query.list();

		int seats = 0;
		double price = 0d;
		double payable = 0d;
		List<AgentDetailedReportRow> rows = new ArrayList<>();
		for (AgentDetailedResultRow r : result) {
			seats += r.seats.intValue();
			price += r.price;
			payable += r.payable;
			rows.add(new AgentDetailedReportRow(r));
		}
		rows.add(new AgentDetailedReportRow(seats, price, payable));

		AgentDetailedReportData data = new AgentDetailedReportData();
		SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		data.fromTime = dateTimeFormatter.format(start);
		data.toTime = dateTimeFormatter.format(end);
		data.rows = rows;

		return buildReport(data);
	}

	public static final class AgentDetailedReportData extends ReportData {

		private String fromTime;
		private String toTime;
		private List<AgentDetailedReportRow> rows;

		public String getFromTime() {
			return fromTime;
		}

		public void setFromTime(String fromTime) {
			this.fromTime = fromTime;
		}

		public String getToTime() {
			return toTime;
		}

		public void setToTime(String toTime) {
			this.toTime = toTime;
		}

		public List<AgentDetailedReportRow> getRows() {
			return rows;
		}

		public void setRows(List<AgentDetailedReportRow> rows) {
			this.rows = rows;
		}
	}

	public static final class AgentDetailedReportRow {

		private static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		private static final DecimalFormat decimalFormatter = new DecimalFormat("0.00");

		private String bookingTime = "";
		private String username = "";
		private String mobile = "";
		private String bus = "";
		private String fromCity = "";
		private String toCity = "";
		private String seats = "";
		private String price = "";
		private String payable = "";

		public AgentDetailedReportRow(AgentDetailedResultRow row) {
			bookingTime = dateTimeFormatter.format(row.bookingTime);
			username = row.username;
			mobile = row.mobile;
			bus = row.bus;
			fromCity = row.fromCity;
			toCity = row.toCity;
			seats = String.valueOf(row.seats);
			price = decimalFormatter.format(row.price);
			payable = decimalFormatter.format(row.payable);
		}

		public AgentDetailedReportRow(int seats, double price, double payable) {
			this.bookingTime = "Total";
			this.seats = String.valueOf(seats);
			this.price = decimalFormatter.format(price);
			this.payable = decimalFormatter.format(payable);
		}

		public String getBookingTime() {
			return bookingTime;
		}

		public void setBookingTime(String bookingTime) {
			this.bookingTime = bookingTime;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getBus() {
			return bus;
		}

		public void setBus(String bus) {
			this.bus = bus;
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

		public String getSeats() {
			return seats;
		}

		public void setSeats(String seats) {
			this.seats = seats;
		}

		public String getPrice() {
			return price;
		}

		public void setPrice(String price) {
			this.price = price;
		}

		public String getPayable() {
			return payable;
		}

		public void setPayable(String payable) {
			this.payable = payable;
		}
	}

	public static final class AgentDetailedResultRow {

		private Date bookingTime;
		private String username;
		private String mobile;
		private String bus;
		private String fromCity;
		private String toCity;
		private BigInteger seats;
		private double price;
		private double payable;

		public Date getBookingTime() {
			return bookingTime;
		}

		public void setBookingTime(Date bookingTime) {
			this.bookingTime = bookingTime;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getBus() {
			return bus;
		}

		public void setBus(String bus) {
			this.bus = bus;
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

		public BigInteger getSeats() {
			return seats;
		}

		public void setSeats(BigInteger seats) {
			this.seats = seats;
		}

		public double getPrice() {
			return price;
		}

		public void setPrice(double price) {
			this.price = price;
		}

		public double getPayable() {
			return payable;
		}

		public void setPayable(double payable) {
			this.payable = payable;
		}
	}
}
