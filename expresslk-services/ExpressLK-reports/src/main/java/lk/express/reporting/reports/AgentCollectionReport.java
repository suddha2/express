package lk.express.reporting.reports;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lk.express.Context;
import lk.express.admin.Company;
import lk.express.admin.User;
import lk.express.bean.BusLight;
import lk.express.db.HibernateUtil;
import lk.express.db.dao.DAOFactory;
import lk.express.db.dao.GenericDAO;
import lk.express.reporting.Report;
import lk.express.reporting.ReportCriteria;
import lk.express.reporting.ReportData;
import lk.express.reporting.ReportGenerator;
import lk.express.reporting.ReportInfo;
import lk.express.schedule.BusSchedule;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

public abstract class AgentCollectionReport extends Report {

	public static final String PARAM_BUS_REG = "Plate number";
	public static final String PARAM_START_TIME = "Start time";
	public static final String PARAM_END_TIME = "End time";
	public static final String PARAM_SCHEDULE_ID = "SCHEDULE_ID";

	protected static final DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);

	public AgentCollectionReport(ReportGenerator generator, ReportCriteria criteria) {
		super(generator, criteria);
	}

	@Override
	public ReportInfo generateReport() throws Exception {
		Map<String, Object> paramMap = getParameters();
		String plate = (String) paramMap.get(PARAM_BUS_REG);
		Date start = (Date) paramMap.get(PARAM_START_TIME);
		Date end = (Date) paramMap.get(PARAM_END_TIME);
		Integer scheduleId = (Integer) paramMap.get(PARAM_SCHEDULE_ID);

		User user = Context.getSessionData().getUser();
		Company company = user.getDivision().getCompany();

		// validate
		if (scheduleId == null && (start == null || end == null)) {
			throw new RuntimeException("Either start time, end time combination or shedule ID must be supplied!");
		}

		Integer busId = null;
		String where = null;
		if (scheduleId == null) {
			GenericDAO<BusLight> busDAO = daoFac.getGenericDAO(BusLight.class);
			BusLight ex = new BusLight();
			ex.setPlateNumber(plate);
			BusLight bus = busDAO.findUnique(ex);
			busId = bus.getId();

			where = "`bus_schedule`.`bus_id` = :busId AND `bus_schedule`.`departure_time` >= :start AND `bus_schedule`.`departure_time` <= :end";
		} else {
			where = "`bus_schedule`.`id` = :scheduleId";
		}

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT `agent`.`name` AS `agent`, SUM(`booking`.`chargeable`) AS `total` ");
		sql.append("FROM `bus_schedule` ");
		sql.append("JOIN `booking_item` ON `bus_schedule`.`id` = `booking_item`.`schedule_id` ");
		sql.append("JOIN `booking` ON `booking_item`.`booking_id` = `booking`.`id` ");
		sql.append("JOIN `agent` ON `booking`.`agent_id` = `agent`.`id` ");
		sql.append("WHERE " + where + " ");
		sql.append("GROUP BY `agent`.`name` ");
		sql.append("ORDER BY `agent`.`name` ");

		Query query = null;
		Session session = HibernateUtil.getCurrentSession();
		if (scheduleId == null) {
			query = session.createSQLQuery(sql.toString()).setParameter("busId", busId).setParameter("start", start)
					.setParameter("end", end).setResultTransformer(Transformers.aliasToBean(AgentSummary.class));
		} else {
			query = session.createSQLQuery(sql.toString()).setParameter("scheduleId", scheduleId)
					.setResultTransformer(Transformers.aliasToBean(AgentSummary.class));
		}
		@SuppressWarnings("unchecked")
		List<AgentSummary> rows = query.list();

		AgentCollectionReportData data = new AgentCollectionReportData();
		data.setLogoFile(company.getLogoFile());
		data.companyName = company.getName();
		data.rows = rows;

		if (scheduleId == null) {
			data.periodStart = start;
			data.periodEnd = end;
			data.plateNumber = plate;
		} else {
			GenericDAO<BusSchedule> scheduleDAO = daoFac.getBusScheduleDAO();
			BusSchedule schedule = scheduleDAO.get(scheduleId);
			if (schedule != null) {
				data.periodStart = schedule.getDepartureTime();
				data.periodEnd = schedule.getDepartureTime();
				data.plateNumber = schedule.getBus().getPlateNumber();
			}
		}

		return buildReport(data);
	}

	public static final class AgentCollectionReportData extends ReportData {

		public String companyName;
		public String plateNumber;
		public Date periodStart;
		public Date periodEnd;
		public List<AgentSummary> rows;

		public String getCompanyName() {
			return companyName;
		}

		public void setCompanyName(String companyName) {
			this.companyName = companyName;
		}

		public String getPlateNumber() {
			return plateNumber;
		}

		public void setPlateNumber(String plateNumber) {
			this.plateNumber = plateNumber;
		}

		public Date getPeriodStart() {
			return periodStart;
		}

		public void setPeriodStart(Date periodStart) {
			this.periodStart = periodStart;
		}

		public Date getPeriodEnd() {
			return periodEnd;
		}

		public void setPeriodEnd(Date periodEnd) {
			this.periodEnd = periodEnd;
		}

		public List<AgentSummary> getRows() {
			return rows;
		}

		public void setRows(List<AgentSummary> rows) {
			this.rows = rows;
		}
	}

	public static final class AgentSummary {

		private String agent;
		private double total;

		public String getAgent() {
			return agent;
		}

		public void setAgent(String agent) {
			this.agent = agent;
		}

		public double getTotal() {
			return total;
		}

		public void setTotal(double total) {
			this.total = total;
		}
	}
}
