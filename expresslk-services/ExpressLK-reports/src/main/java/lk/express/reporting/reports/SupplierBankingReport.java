package lk.express.reporting.reports;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lk.express.Context;
import lk.express.admin.Company;
import lk.express.admin.User;
import lk.express.db.HibernateUtil;
import lk.express.reporting.Report;
import lk.express.reporting.ReportCriteria;
import lk.express.reporting.ReportData;
import lk.express.reporting.ReportGenerator;
import lk.express.reporting.ReportInfo;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

public abstract class SupplierBankingReport extends Report {

	public static final String PARAM_START_TIME = "Start time";
	public static final String PARAM_END_TIME = "End time";
	public static final String QUERY;

	static {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT");
		sb.append("    `supplier`.`id` AS `supplierId`,");
		sb.append("    `supplier_account`.`bank`,");
		sb.append("    `supplier`.`name` AS `supplierName`,");
		sb.append("    `supplier_account`.`number` AS `accountNumber`,");
		sb.append("    `supplier_account`.`branch`,");
		sb.append("    SUM(`booking_item`.`cost`) AS `payable` ");
		sb.append("FROM `booking_item`");
		sb.append("    JOIN `bus_schedule` ON `booking_item`.`schedule_id` = `bus_schedule`.`id`");
		sb.append("    JOIN `bus` ON `bus_schedule`.`bus_id` = `bus`.`id`");
		sb.append("    JOIN `division` ON `bus`.`division_id` = `division`.`id`");
		sb.append("    JOIN `supplier` ON `bus`.`supplier_id` = `supplier`.`id`");
		sb.append("    LEFT OUTER JOIN `supplier_account` ON `supplier_account`.`supplier_id` = `supplier`.`id` AND `supplier_account`.`is_primary` = 1 ");
		sb.append("WHERE `bus_schedule`.`departure_time` >= :start");
		sb.append("    AND `bus_schedule`.`departure_time` < :end");
		sb.append("    AND `division`.`company_id` = :companyId");
		sb.append("    AND `booking_item`.`status_code` = 'CONF' ");
		sb.append("GROUP BY `supplier`.`name` ");
		sb.append("ORDER BY `supplier_account`.`bank`");
		QUERY = sb.toString();
	}

	public SupplierBankingReport(ReportGenerator generator, ReportCriteria criteria) {
		super(generator, criteria);
	}

	@Override
	public ReportInfo generateReport() throws Exception {

		Map<String, Object> paramMap = getParameters();
		Date start = (Date) paramMap.get(PARAM_START_TIME);
		Date end = (Date) paramMap.get(PARAM_END_TIME);
		User user = Context.getSessionData().getUser();
		Company company = user.getDivision().getCompany();

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.createSQLQuery(QUERY).setParameter("start", start).setParameter("end", end)
				.setParameter("companyId", company.getId())
				.setResultTransformer(Transformers.aliasToBean(BankingReportRow.class));
		@SuppressWarnings("unchecked")
		List<BankingReportRow> rows = query.list();

		ensureNoDuplicateSuppliers(rows);

		BankingReportData reportData = new BankingReportData();
		reportData.setLogoFile(company.getLogoFile());
		reportData.companyName = company.getName();
		reportData.start = start;
		reportData.end = end;
		reportData.rows = rows;

		return buildReport(reportData);
	}

	// Duplicates may happen is two accounts are made primary for the same
	// supplier
	private static void ensureNoDuplicateSuppliers(List<BankingReportRow> rows) {
		Set<Integer> supplierIds = new HashSet<>();
		Iterator<BankingReportRow> i = rows.iterator();
		while (i.hasNext()) {
			if (!supplierIds.add(i.next().supplierId)) {
				i.remove();
			}
		}
	}

	public static final class BankingReportData extends ReportData {

		public String companyName;
		public Date start;
		public Date end;
		public List<BankingReportRow> rows;

		public String getCompanyName() {
			return companyName;
		}

		public Date getStart() {
			return start;
		}

		public Date getEnd() {
			return end;
		}

		public List<BankingReportRow> getRows() {
			return rows;
		}
	}

	public static final class BankingReportRow {

		public int supplierId;
		public String bank;
		public String supplierName;
		public String accountNumber;
		public String branch;
		public double payable;

		public int getSupplierId() {
			return supplierId;
		}

		public String getBank() {
			return bank;
		}

		public String getSupplierName() {
			return supplierName;
		}

		public String getAccountNumber() {
			return accountNumber;
		}

		public String getBranch() {
			return branch;
		}

		public double getPayable() {
			return payable;
		}
	}
}
