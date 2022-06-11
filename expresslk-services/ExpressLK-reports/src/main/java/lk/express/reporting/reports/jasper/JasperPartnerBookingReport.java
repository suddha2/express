package lk.express.reporting.reports.jasper;

import lk.express.reporting.ReportCriteria;
import lk.express.reporting.ReportGenerator;
import lk.express.reporting.ReportInfo;
import lk.express.reporting.reports.PartnerBookingReport;

public class JasperPartnerBookingReport extends PartnerBookingReport {

	public JasperPartnerBookingReport(ReportGenerator generator, ReportCriteria criteria) {
		super(generator, criteria);
	}

	@Override
	protected ReportInfo buildReport(Object reportData) throws Exception {
		return generator.buildReport("partner_bookings", criteria, reportData);
	}

}
