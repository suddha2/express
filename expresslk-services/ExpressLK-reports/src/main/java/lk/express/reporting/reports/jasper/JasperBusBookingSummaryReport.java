package lk.express.reporting.reports.jasper;

import lk.express.reporting.ReportCriteria;
import lk.express.reporting.ReportGenerator;
import lk.express.reporting.ReportInfo;
import lk.express.reporting.reports.BusBookingSummaryReport;

public class JasperBusBookingSummaryReport extends BusBookingSummaryReport {

	public JasperBusBookingSummaryReport(ReportGenerator generator, ReportCriteria criteria) {
		super(generator, criteria);
	}

	@Override
	protected ReportInfo buildReport(Object reportData) throws Exception {
		return generator.buildReport("bus_booking_summary", criteria, reportData);
	}
}
