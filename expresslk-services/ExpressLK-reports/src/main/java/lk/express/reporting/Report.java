package lk.express.reporting;

import java.util.HashMap;
import java.util.Map;

import lk.express.reports.ReportParameter;

public abstract class Report implements IReport {

	protected ReportGenerator generator;
	protected ReportCriteria criteria;

	public Report(ReportGenerator generator, ReportCriteria criteria) {
		this.generator = generator;
		this.criteria = criteria;
	}

	protected Map<String, Object> getParameters() {
		Map<String, Object> paramMap = new HashMap<>();
		for (ReportParameter parameter : criteria.getReportParams()) {
			paramMap.put(parameter.getName(), parameter.getObject());
		}
		return paramMap;
	}

	protected abstract ReportInfo buildReport(Object reportData) throws Exception;
}
