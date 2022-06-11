package lk.express.reporting;

public abstract class ReportGenerator implements IReportGenerator {

	public abstract ReportInfo buildReport(String templateName, ReportCriteria criteria, Object reportData)
			throws Exception;

}
