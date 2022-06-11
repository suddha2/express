package lk.express.reporting.reports.jasper;

import lk.express.reporting.ReportCriteria;
import lk.express.reporting.ReportGenerator;
import lk.express.reporting.ReportInfo;
import lk.express.reporting.reports.AgentCollectionReport;

public class JasperAgentCollectionReport extends AgentCollectionReport {

	public static final String TEMPLATE = "agent_collection";

	public JasperAgentCollectionReport(ReportGenerator generator, ReportCriteria criteria) {
		super(generator, criteria);
	}

	@Override
	protected ReportInfo buildReport(Object reportData) throws Exception {
		return generator.buildReport(TEMPLATE, criteria, reportData);
	}

}
