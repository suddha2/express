package lk.express.reporting;

import lk.express.reporting.jasper.JasperReportGenerator;

public class ReportGeneratorFactory {

	public static final String JASPER_ENGINE = "JASPER";

	public static IReportGenerator getReportGenerator(String type) {
		IReportGenerator reportGenerator = null;
		if (type.equals(ReportGeneratorFactory.JASPER_ENGINE)) {
			reportGenerator = new JasperReportGenerator();
		}

		return reportGenerator;
	}
}
