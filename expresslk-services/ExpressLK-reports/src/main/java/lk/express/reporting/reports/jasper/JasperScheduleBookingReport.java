package lk.express.reporting.reports.jasper;

import lk.express.reporting.ReportCriteria;
import lk.express.reporting.ReportGenerator;
import lk.express.reporting.ReportInfo;
import lk.express.reporting.reports.ScheduleBookingReport;

public class JasperScheduleBookingReport extends ScheduleBookingReport {

	public JasperScheduleBookingReport(ReportGenerator generator, ReportCriteria criteria) {
		super(generator, criteria);
	}

	@Override
	protected ReportInfo buildReport(Object reportData) throws Exception {
		return generator.buildReport("booked_seats", criteria, reportData);
	}
}
