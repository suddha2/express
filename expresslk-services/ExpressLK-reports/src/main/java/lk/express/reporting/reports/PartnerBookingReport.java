package lk.express.reporting.reports;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lk.express.Context;
import lk.express.admin.Company;
import lk.express.admin.User;
import lk.express.db.HibernateUtil;
import lk.express.db.dao.DAOFactory;
import lk.express.reporting.Report;
import lk.express.reporting.ReportCriteria;
import lk.express.reporting.ReportData;
import lk.express.reporting.ReportGenerator;
import lk.express.reporting.ReportInfo;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

public abstract class PartnerBookingReport extends Report {

	public static final String PARAM_DATE = "Date";
	public static final String PARAM_USER = "User";
	public static final String PARAM_MODE = "Payment mode";

	protected static final DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);

	public PartnerBookingReport(ReportGenerator generator, ReportCriteria criteria) {
		super(generator, criteria);
	}

	@Override
	public ReportInfo generateReport() throws Exception {
		Map<String, Object> paramMap = getParameters();
		Integer userId = (Integer) paramMap.get(PARAM_USER);
		String mode = (String) paramMap.get(PARAM_MODE);
		Date date = (Date) paramMap.get(PARAM_DATE);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date start = calendar.getTime();

		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		Date end = calendar.getTime();

		User genUser = Context.getSessionData().getUser();
		Company company = genUser.getDivision().getCompany();

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append("	`temp`.`bus`, ");
		sql.append("	`temp`.`dep_time` AS `departureTime`, ");
		sql.append("	`temp`.`from`, ");
		sql.append("	`temp`.`to`, ");
		sql.append("	SUM(`temp`.`bked_seats`) AS `bookedSeats`, ");
		sql.append("	ROUND(SUM(`temp`.`fare`), 2) AS `fare`, ");
		sql.append("	ROUND(SUM(`temp`.`price`), 2) AS `collected`, ");
		sql.append("	ROUND(SUM(`temp`.`cost`), 2) AS `payable`, ");
		sql.append("	`temp`.`sys_user` AS `user`, ");
		sql.append("	`temp`.`agent_name` AS `agent`, ");
		sql.append("	`temp`.`mode` AS `paymentMethod` ");
		sql.append("FROM ");
		sql.append("	( ");
		sql.append("		SELECT ");
		sql.append("			`bus`.`plate_number` AS `bus`, ");
		sql.append("			`bus_schedule`.`departure_time` AS `dep_time`, ");
		sql.append("			( ");
		sql.append("				SELECT `city`.`name" + criteria.getLangSuffix() + "` ");
		sql.append("				FROM `city` ");
		sql.append("				WHERE `city`.`id` = `bus_route`.`from_city_id` ");
		sql.append("			) AS `from`, ");
		sql.append("			( ");
		sql.append("				SELECT `city`.`name" + criteria.getLangSuffix() + "` ");
		sql.append("				FROM `city` ");
		sql.append("				WHERE `city`.`id` = `bus_route`.`to_city_id` ");
		sql.append("			) AS `to`, ");
		sql.append("			`booking`.`reference`, ");
		sql.append("			( ");
		sql.append("				SELECT COUNT(*) ");
		sql.append("				FROM `booking_item_passenger` ");
		sql.append("				WHERE `booking_item_passenger`.`booking_item_id` = `booking_item`.`id` ");
		sql.append("					AND `booking_item_passenger`.`status_code` = 'conf' ");
		sql.append("			) AS `bked_seats`, ");
		sql.append("			`booking_item`.`fare`, ");
		sql.append("			`booking_item`.`price`, ");
		sql.append("			( ");
		sql.append("				CASE WHEN `user`.`username` = 'spl' OR `user`.`username` = 'splw' OR `user`.`division_id` = 1 THEN `booking_item`.`cost` ELSE ( ");
		sql.append("					CASE WHEN `user`.`username` = 'splm' THEN `booking_item`.`cost` * 0.94 ELSE ( ");
		sql.append("						CASE WHEN `user`.`division_id` = :division_id AND ( ");
		sql.append("		 					SELECT SUM(`payment_refund`.`amount`) ");
		sql.append("							FROM `payment` INNER JOIN `payment_refund` ON `payment_refund`.`id` = `payment`.`id` ");
		sql.append("							WHERE `payment`.`booking_id` = `booking`.`id` ");
		sql.append("							AND `payment_refund`.`mode` LIKE 'eZCash' ");
		sql.append("							GROUP BY `booking`.`id` ");
		sql.append("						) > 0 THEN ( ");
		sql.append("							SELECT SUM(`payment_refund`.`amount`) ");
		sql.append("							FROM `payment` INNER JOIN `payment_refund` ON `payment_refund`.`id` = `payment`.`id` ");
		sql.append("							WHERE `payment`.`booking_id` = `booking`.`id` ");
		sql.append("							AND `payment_refund`.`mode` LIKE 'eZCash' ");
		sql.append("							GROUP BY `booking`.`id` ");
		sql.append("						) * 0.94 ELSE ( ");
		sql.append("							0 ");
		sql.append("						) END ");
		sql.append("					) END ");
		sql.append("				) END ");
		sql.append("			) AS `cost`, ");
		sql.append("			( ");
		sql.append("				CASE WHEN `user`.`username` = 'spl' OR `user`.`username` = 'splw' OR `user`.`username` = 'splm' OR `user`.`username` = 'bbk' OR `user`.`division_id` = 1 THEN 'Web' ELSE ( ");
		sql.append("					`user`.`first_name` ");
		sql.append("				) END ");
		sql.append("			) AS `sys_user`, ");
		sql.append("			IFNULL(`agent`.`name`, '') AS `agent_name`, ");
		sql.append("			( ");
		sql.append("				SELECT GROUP_CONCAT(`mode` SEPARATOR ', ') ");
		sql.append("				FROM ( ");
		sql.append("					SELECT `payment`.`booking_id`, CONCAT(IFNULL(`payment_refund`.`mode`, ''), IF(`payment_refund`.`vendor_mode` IS NULL, '', CONCAT('(', `payment_refund`.`vendor_mode`, ')'))) AS `mode` ");
		sql.append("					FROM `payment` INNER JOIN `payment_refund` ON `payment_refund`.`id` = `payment`.`id` ");
		sql.append("				) `a` ");
		sql.append("				WHERE `a`.`booking_id` = `booking`.`id` ");
		sql.append("				GROUP BY `a`.`booking_id` ");
		sql.append("			)  AS `mode` ");
		if (mode != null && !mode.isEmpty()) {
			sql.append("		, ( ");
			sql.append("			SELECT GROUP_CONCAT(`mode` SEPARATOR ', ') ");
			sql.append("			FROM `payment` INNER JOIN `payment_refund` ON `payment_refund`.`id` = `payment`.`id` ");
			sql.append("			WHERE `payment`.`booking_id` = `booking`.`id` ");
			sql.append("			GROUP BY `payment`.`booking_id` ");
			sql.append("		)  AS `payment_mode` ");
			sql.append("		, ( ");
			sql.append("			SELECT GROUP_CONCAT(`vendor_mode` SEPARATOR ', ') ");
			sql.append("			FROM `payment` INNER JOIN `payment_refund` ON `payment_refund`.`id` = `payment`.`id` ");
			sql.append("			WHERE `payment`.`booking_id` = `booking`.`id` ");
			sql.append("			GROUP BY `payment`.`booking_id` ");
			sql.append("		)  AS `vendor_mode` ");
		}
		sql.append("		FROM ");
		sql.append("			`booking` ");
		sql.append("			INNER JOIN `booking_item` ON `booking_item`.`booking_id` = `booking`.`id` ");
		sql.append("			INNER JOIN `bus_schedule` ON `booking_item`.`schedule_id` = `bus_schedule`.`id` ");
		sql.append("			INNER JOIN `bus` ON `bus`.`id` = `bus_schedule`.`bus_id` ");
		sql.append("			INNER JOIN `bus_route` ON `bus_schedule`.`bus_route_id` = `bus_route`.`id` ");
		sql.append("			INNER JOIN `user` ON `user`.`id` = `booking`.`user_id` ");
		sql.append("			LEFT OUTER JOIN `agent` ON `agent`.`id` = `booking`.`agent_id` ");
		sql.append("		WHERE ");
		sql.append("			`bus_schedule`.`allowed_divisions` = :allowed_divisions ");
		sql.append("			AND `booking`.`status_code` = 'conf' ");
		sql.append("			AND `booking_item`.`status_code` = 'conf' ");
		sql.append("			AND `bus_schedule`.`departure_time` BETWEEN :start AND :end ");
		if (userId != null && userId > 0) {
			sql.append("		AND `booking`.`user_id` = :user_id ");
		}
		sql.append("	) AS `temp` ");
		if (mode != null && !mode.isEmpty()) {
			sql.append("WHERE ");
			if (Company.BBK.equals(company.getCode())) {
				sql.append("`temp`.`payment_mode` = :mode ");
			} else {
				sql.append("`temp`.`vendor_mode` = :mode ");
			}
		}
		sql.append("GROUP BY ");
		sql.append("	`temp`.`bus`, ");
		sql.append("	`temp`.`dep_time`, ");
		sql.append("	`temp`.`from`, ");
		sql.append("	`temp`.`to`, ");
		sql.append("	`temp`.`sys_user`, ");
		sql.append("	`temp`.`agent_name`, ");
		sql.append("	`temp`.`mode`");
		sql.append("ORDER BY ");
		sql.append("	`temp`.`dep_time`, ");
		sql.append("	`temp`.`bus`");

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.createSQLQuery(sql.toString()).setParameter("division_id", genUser.getDivision().getId())
				.setParameter("allowed_divisions", genUser.getDivision().getBitmask()).setParameter("start", start)
				.setParameter("end", end).setResultTransformer(Transformers.aliasToBean(ResultRow.class));
		if (userId != null && userId > 0) {
			query.setParameter("user_id", userId);
		}
		if (mode != null && !mode.isEmpty()) {
			query.setParameter("mode", mode);
		}
		@SuppressWarnings("unchecked")
		List<ResultRow> result = query.list();

		String bus = null;
		int seats = 0;
		double fare = 0;
		double collected = 0;
		double payable = 0;
		List<PartnerBookingRow> rows = new ArrayList<>();
		for (ResultRow r : result) {
			if (bus != null && !bus.equals(r.bus)) {
				rows.add(new PartnerBookingRow(seats, fare, collected, payable));
				rows.add(new PartnerBookingRow());
				seats = 0;
				fare = 0;
				collected = 0;
				payable = 0;
			}
			bus = r.bus;
			seats += r.bookedSeats.intValue();
			fare += r.fare;
			collected += r.collected;
			payable += r.payable;
			rows.add(new PartnerBookingRow(r));
		}
		rows.add(new PartnerBookingRow(seats, fare, collected, payable));

		PartnerBookingReportData data = new PartnerBookingReportData();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
		data.date = dateFormatter.format(date);
		if (userId != null && userId > 0) {
			User user = daoFac.getUserDAO().get(userId);
			data.user = user.getFirstName();
		}
		if (mode != null && !mode.isEmpty()) {
			data.mode = mode;
		}
		data.setLogoFile(company.getLogoFile());
		data.rows = rows;
		return buildReport(data);
	}

	public static final class PartnerBookingReportData extends ReportData {

		public String date;
		public String user = "All";
		public String mode = "All";
		public List<PartnerBookingRow> rows;

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getMode() {
			return mode;
		}

		public void setMode(String mode) {
			this.mode = mode;
		}

		public List<PartnerBookingRow> getRows() {
			return rows;
		}

		public void setRows(List<PartnerBookingRow> rows) {
			this.rows = rows;
		}
	}

	public static final class PartnerBookingRow {

		private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
		private static final DecimalFormat decimalFormatter = new DecimalFormat("0.00");

		private String bus;
		private String departureTime;
		private String from;
		private String to;
		private String bookedSeats;
		private String fare;
		private String collected;
		private String payable;
		private String user;
		private String agent;
		private String paymentMethod;

		public PartnerBookingRow() {
			this.bus = "";
			this.departureTime = "";
			this.from = "";
			this.to = "";
			this.bookedSeats = "";
			this.fare = "";
			this.collected = "";
			this.payable = "";
			this.user = "";
			this.agent = "";
			this.paymentMethod = "";
		}

		public PartnerBookingRow(ResultRow row) {
			this.bus = row.bus;
			this.departureTime = timeFormatter.format(row.departureTime);
			this.from = row.from;
			this.to = row.to;
			this.bookedSeats = row.bookedSeats.toString();
			this.fare = decimalFormatter.format(row.fare);
			this.collected = decimalFormatter.format(row.collected);
			this.payable = decimalFormatter.format(row.payable);
			this.user = row.user;
			this.agent = row.agent;
			this.paymentMethod = row.paymentMethod;
		}

		public PartnerBookingRow(int seats, double fare, double collected, double payable) {
			this();
			this.bus = "Total";
			this.bookedSeats = Integer.toString(seats);
			this.fare = decimalFormatter.format(fare);
			this.collected = decimalFormatter.format(collected);
			this.payable = decimalFormatter.format(payable);
		}

		public String getBus() {
			return bus;
		}

		public void setBus(String bus) {
			this.bus = bus;
		}

		public String getDepartureTime() {
			return departureTime;
		}

		public void setDepartureTime(String departureTime) {
			this.departureTime = departureTime;
		}

		public String getFrom() {
			return from;
		}

		public void setFrom(String from) {
			this.from = from;
		}

		public String getTo() {
			return to;
		}

		public void setTo(String to) {
			this.to = to;
		}

		public String getBookedSeats() {
			return bookedSeats;
		}

		public void setBookedSeats(String bookedSeats) {
			this.bookedSeats = bookedSeats;
		}

		public String getFare() {
			return fare;
		}

		public void setFare(String fare) {
			this.fare = fare;
		}

		public String getCollected() {
			return collected;
		}

		public void setCollected(String collected) {
			this.collected = collected;
		}

		public String getPayable() {
			return payable;
		}

		public void setPayable(String payable) {
			this.payable = payable;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getAgent() {
			return agent;
		}

		public void setAgent(String agent) {
			this.agent = agent;
		}

		public String getPaymentMethod() {
			return paymentMethod;
		}

		public void setPaymentMethod(String paymentMethod) {
			this.paymentMethod = paymentMethod;
		}
	}

	public static final class ResultRow {

		private String bus;
		private Date departureTime;
		private String from;
		private String to;
		private BigDecimal bookedSeats;
		private double fare;
		private double collected;
		private double payable;
		private String user;
		private String agent;
		private String paymentMethod;

		public String getBus() {
			return bus;
		}

		public void setBus(String bus) {
			this.bus = bus;
		}

		public Date getDepartureTime() {
			return departureTime;
		}

		public void setDepartureTime(Date departureTime) {
			this.departureTime = departureTime;
		}

		public String getFrom() {
			return from;
		}

		public void setFrom(String from) {
			this.from = from;
		}

		public String getTo() {
			return to;
		}

		public void setTo(String to) {
			this.to = to;
		}

		public BigDecimal getBookedSeats() {
			return bookedSeats;
		}

		public void setBookedSeats(BigDecimal bookedSeats) {
			this.bookedSeats = bookedSeats;
		}

		public double getFare() {
			return fare;
		}

		public void setFare(double fare) {
			this.fare = fare;
		}

		public double getCollected() {
			return collected;
		}

		public void setCollected(double collected) {
			this.collected = collected;
		}

		public double getPayable() {
			return payable;
		}

		public void setPayable(double payable) {
			this.payable = payable;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getAgent() {
			return agent;
		}

		public void setAgent(String agent) {
			this.agent = agent;
		}

		public String getPaymentMethod() {
			return paymentMethod;
		}

		public void setPaymentMethod(String paymentMethod) {
			this.paymentMethod = paymentMethod;
		}
	}
}
