package lk.express.reporting.reports;

import java.util.ArrayList;
import java.util.Comparator;
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
import lk.express.schedule.BusSchedule;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

public abstract class ScheduleBookingReport extends Report {

	public static final String PARAM_BUS_REG = "Plate number";
	public static final String PARAM_DATE = "Date";
	public static final String PARAM_TURN_NO = "Turn number";
	public static final String PARAM_SCHEDULE_ID = "SCHEDULE_ID";

	public ScheduleBookingReport(ReportGenerator generator, ReportCriteria criteria) {
		super(generator, criteria);
	}

	@Override
	public ReportInfo generateReport() throws Exception {

		Session dbSession = HibernateUtil.getCurrentSession();

		Query query = null;
		ReportParameter scheduleIdParam = criteria.getParameter(PARAM_SCHEDULE_ID);
		if (scheduleIdParam.getObject() == null) {
			String regNo = (String) criteria.getParameter(PARAM_BUS_REG).getObject();
			Integer turn = (Integer) criteria.getParameter(PARAM_TURN_NO).getObject();
			Date date = (Date) criteria.getParameter(PARAM_DATE).getObject();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT bs.id AS schduleId, b.name AS busName, b.plate_number AS regno, "
					+ "p1.first_name AS conductorFname, p1.last_name AS conductorLname, p1.mobile_telephone AS conductorMobile, "
					+ "p.first_name AS driverFname, p.last_name AS driverLname, p.mobile_telephone AS driverMobile, "
					+ "c1.name" + criteria.getLangSuffix() + " AS fromCity, c2.name" + criteria.getLangSuffix()
					+ " AS toCity, bs.departure_time AS dateTime, '" + turn + "' AS turn ");
			sql.append("FROM bus_schedule AS bs ");
			sql.append("JOIN bus AS b ON b.id = bs.bus_id ");
			sql.append("JOIN driver AS dr ON dr.id = bs.driver_id JOIN person AS p ON p.id = dr.person_id ");
			sql.append("JOIN conductor AS co ON co.id = bs.conductor_id JOIN person AS p1 ON p1.id = co.person_id ");
			sql.append("JOIN bus_route AS br on br.id = bs.bus_route_id JOIN city AS c1 on c1.id = br.from_city_id JOIN city AS c2 on c2.id = br.to_city_id ");
			sql.append("WHERE b.plate_number = :regNo AND DATE(bs.departure_time) = :dateOnly ORDER BY bs.departure_time LIMIT :start, 1");

			query = dbSession.createSQLQuery(sql.toString()).setParameter("regNo", regNo)
					.setParameter("dateOnly", new java.sql.Date(date.getTime())).setParameter("start", (turn - 1))
					.setResultTransformer(Transformers.aliasToBean(ScheduleBookingData.class));
		} else {
			Integer scheduleId = (Integer) scheduleIdParam.getObject();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT bs.id AS schduleId, b.name AS busName, b.plate_number AS regno, "
					+ "p1.first_name AS conductorFname, p1.last_name AS conductorLname, p1.mobile_telephone AS conductorMobile, "
					+ "p.first_name AS driverFname, p.last_name AS driverLname, p.mobile_telephone AS driverMobile, "
					+ "c1.name" + criteria.getLangSuffix() + " AS fromCity, c2.name" + criteria.getLangSuffix()
					+ " AS toCity, bs.departure_time AS dateTime, '' AS turn ");
			sql.append("FROM bus_schedule AS bs ");
			sql.append("JOIN bus AS b ON b.id = bs.bus_id ");
			sql.append("JOIN driver AS dr ON dr.id = bs.driver_id JOIN person AS p ON p.id = dr.person_id ");
			sql.append("JOIN conductor AS co ON co.id = bs.conductor_id JOIN person AS p1 ON p1.id = co.person_id ");
			sql.append("JOIN bus_route AS br on br.id = bs.bus_route_id JOIN city AS c1 on c1.id = br.from_city_id JOIN city AS c2 on c2.id = br.to_city_id ");
			sql.append("WHERE bs.id = :scheduleId");

			query = dbSession.createSQLQuery(sql.toString()).setParameter("scheduleId", scheduleId)
					.setResultTransformer(Transformers.aliasToBean(ScheduleBookingData.class));
		}
		@SuppressWarnings("unchecked")
		List<ScheduleBookingData> reports = query.list();

		ScheduleBookingData report = null;
		if (reports != null && reports.size() > 0) {
			report = reports.get(0);
			report.setPaxInfo(getSchedulePaxList(dbSession, report.getSchduleId()));
			setSeats(report);

			User user = Context.getSessionData().getUser();
			Company company = user.getDivision().getCompany();
			report.setLogoFile(company.getLogoFile());
			
			
			BusSchedule busSchedule = (BusSchedule) HibernateUtil.getCurrentSession().createCriteria(BusSchedule.class)
					.add(Restrictions.eq("id", report.getSchduleId())).uniqueResult();
			report.setSupplier(busSchedule.getBus().getSupplier().getName());
		}

		return buildReport(report);
	}

	private void setSeats(ScheduleBookingData report) {
		List<String> seats = new ArrayList<>();
		for (SchedulePassengerData paxInfo : report.getPaxInfo()) {
			seats.add(paxInfo.getSeatNo());
		}
		seats.sort(new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				try {
					return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));
				} catch (NumberFormatException e) {
					return 0;
				}
			}
		});
		report.setSeats(StringUtils.join(seats, ", "));
	}

	private List<SchedulePassengerData> getSchedulePaxList(Session dbSession, int scheduleId) {

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT bip.seat_number AS seatNo, p.name AS passengerName, p.mobile_telephone AS mobileNumber, bs.name"
				+ criteria.getLangSuffix() + " AS boardingPoint, bkg.reference AS ticketNumber , bkg.verification_code as verificationCode ");
		sql.append("FROM booking_item AS bi ");
		sql.append("JOIN booking AS bkg ON bkg.id = bi.booking_id ");
		sql.append("JOIN passenger AS p ON p.booking_id = bkg.id ");
		sql.append("JOIN booking_item_passenger AS bip ON bip.booking_item_id = bi.id AND bip.passenger_id = p.id ");
		sql.append("JOIN bus_stop AS bs ON bs.id = bi.from_bus_stop_id ");
		sql.append("JOIN bus_schedule AS bsc ON bsc.id = bi.schedule_id ");
		sql.append("JOIN bus_route AS br ON br.id = bsc.bus_route_id ");
		sql.append("JOIN bus_schedule_bus_stop AS bsbs ON bsbs.schedule_id = bsc.id AND bsbs.bus_stop_id = bi.from_bus_stop_id ");
		sql.append("WHERE bi.schedule_id = :scheduleId AND bi.status_code = :status ");
		// order by bus_stop, ticket, and seat number
		sql.append("ORDER BY bsbs.idx, bip.seat_number"); //bkg.reference, 

		Query query = dbSession.createSQLQuery(sql.toString()).setParameter("scheduleId", scheduleId)
				.setParameter("status", "CONF")
				.setResultTransformer(Transformers.aliasToBean(SchedulePassengerData.class));

		@SuppressWarnings("unchecked")
		List<SchedulePassengerData> paxInfo = query.list();
		return paxInfo;
	}

	public static final class ScheduleBookingData extends ReportData {

		private int schduleId;
		private String busName;
		private String regno;
		private int id;
		private String driverFname;
		private String driverLname;
		private String driverMobile;
		private String conductorFname;
		private String conductorLname;
		private String conductorMobile;
		private Date dateTime;
		private String turnNo;
		private String fromCity;
		private String toCity;
		private List<SchedulePassengerData> paxInfo;
		private String seats;
		private String turn;
		private String supplier;

		public String getRegno() {
			return regno;
		}

		public void setSupplier(String name) {
			this.supplier=name;
			
		}
		
		public String getSupplier() {
			return this.supplier;
		}

		public void setRegno(String regno) {
			this.regno = regno;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getDriverName() {
			return driverFname + " " + driverLname + " (" + driverMobile + ")";
		}

		public void setDriverName(String driverName) {
		}

		public String getConductorName() {
			return conductorFname + " " + conductorLname + " (" + conductorMobile + ")";
		}

		public void setConductorName(String conductorName) {
		}

		public String getDate() {
			return dateTime.toString();
		}

		public void setDate(String date) {
		}

		public String getTurnNo() {
			return turnNo;
		}

		public void setTurnNo(String turnNo) {
			this.turnNo = turnNo;
		}

		public List<SchedulePassengerData> getPaxInfo() {
			return paxInfo;
		}

		public void setPaxInfo(List<SchedulePassengerData> paxInfo) {
			this.paxInfo = paxInfo;
		}

		public String getBusName() {
			return busName;
		}

		public void setBusName(String busName) {
			this.busName = busName;
		}

		public String getDriverFname() {
			return driverFname;
		}

		public void setDriverFname(String driverFname) {
			this.driverFname = driverFname;
		}

		public String getDriverLname() {
			return driverLname;
		}

		public String getDriverMobile() {
			return driverMobile;
		}

		public void setDriverMobile(String driverMobile) {
			this.driverMobile = driverMobile;
		}

		public void setDriverLname(String driverLname) {
			this.driverLname = driverLname;
		}

		public String getConductorFname() {
			return conductorFname;
		}

		public void setConductorFname(String conductorFname) {
			this.conductorFname = conductorFname;
		}

		public String getConductorLname() {
			return conductorLname;
		}

		public void setConductorLname(String conductorLname) {
			this.conductorLname = conductorLname;
		}

		public String getConductorMobile() {
			return conductorMobile;
		}

		public void setConductorMobile(String conductorMobile) {
			this.conductorMobile = conductorMobile;
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

		public Date getDateTime() {
			return dateTime;
		}

		public void setDateTime(Date dateTime) {
			this.dateTime = dateTime;
		}

		public String getSeats() {
			return seats;
		}

		public void setSeats(String seats) {
			this.seats = seats;
		}

		public String getTurn() {
			return turn;
		}

		public void setTurn(String turn) {
			this.turn = turn;
		}

		public int getSchduleId() {
			return schduleId;
		}

		public void setSchduleId(int schduleId) {
			this.schduleId = schduleId;
		}
	}

	public static final class SchedulePassengerData {

		private String boardingPoint;
		private String seatNo;
		private String passengerName;
		private String ticketNumber;
		private String mobileNumber;
		private String verificationCode;
		

		public String getBoardingPoint() {
			return boardingPoint;
		}

		public void setBoardingPoint(String boardingPoint) {
			this.boardingPoint = boardingPoint;
		}

		public String getSeatNo() {
			return seatNo;
		}

		public void setSeatNo(String seatNo) {
			this.seatNo = seatNo;
		}

		public String getTicketNumber() {
			return ticketNumber;
		}

		public void setTicketNumber(String ticketNumber) {
			this.ticketNumber = ticketNumber;
		}

		public String getMobileNumber() {
			return mobileNumber;
		}

		public void setMobileNumber(String mobileNumber) {
			this.mobileNumber = mobileNumber;
		}

		public String getPassengerName() {
			return passengerName;
		}

		public void setPassengerName(String passengerName) {
			this.passengerName = passengerName;
		}

		public String getVerificationCode() {
			return verificationCode;
		}

		public void setVerificationCode(String verificationCode) {
			this.verificationCode = verificationCode;
		}
	}
}
