package lk.express.reporting;

import org.hibernate.Session;

public interface IReportGenerator {

	GenerateReportResponse generateReport(ReportCriteria criteria, Session dbSession);

}
