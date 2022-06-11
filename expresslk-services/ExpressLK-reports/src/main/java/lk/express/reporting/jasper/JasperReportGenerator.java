package lk.express.reporting.jasper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.express.reporting.GenerateReportResponse;
import lk.express.reporting.IReport;
import lk.express.reporting.ReportCriteria;
import lk.express.reporting.ReportGenerator;
import lk.express.reporting.ReportInfo;
import lk.express.reporting.reports.jasper.JasperAgentCollectionReport;
import lk.express.reporting.reports.jasper.JasperAgentDetailedReport;
import lk.express.reporting.reports.jasper.JasperBusBookingSummaryReport;
import lk.express.reporting.reports.jasper.JasperPartnerBookingReport;
import lk.express.reporting.reports.jasper.JasperScheduleBookingReport;
import lk.express.reporting.reports.jasper.JasperSupplierBankingReport;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JasperReportGenerator extends ReportGenerator {

	private static final Logger logger = LoggerFactory.getLogger(JasperReportGenerator.class);

	public static final String templatePath = "/templates/";
	public static final String reportsFolder = "gen";

	@Override
	public GenerateReportResponse generateReport(ReportCriteria criteria, Session dbSession) {
		GenerateReportResponse response = null;
		try {
			IReport report = null;
			ReportInfo info = null;

			switch (criteria.getReportType()) {
			case AgentCollection:
				report = new JasperAgentCollectionReport(this, criteria);
				break;
			case ScheduleBooking:
				report = new JasperScheduleBookingReport(this, criteria);
				break;
			case SupplierBanking:
				report = new JasperSupplierBankingReport(this, criteria);
				info = report.generateReport();
				break;
			case BusBookingSummary:
				report = new JasperBusBookingSummaryReport(this, criteria);
				break;
			case PartnerBooking:
				report = new JasperPartnerBookingReport(this, criteria);
				break;
			case AgentDetailed:
				report = new JasperAgentDetailedReport(this, criteria);
				break;
			default:
				throw new IllegalArgumentException("Unsupported report type: " + criteria.getReportType());
			}

			info = report.generateReport();
			response = new GenerateReportResponse(info);
		} catch (Exception e) {
			logger.error("Error Generating Report", e);
			response = new GenerateReportResponse("Error Generating Report" + e.getMessage());
		}
		return response;
	}

	@Override
	public ReportInfo buildReport(String templateName, ReportCriteria criteria, Object reportData) throws Exception {

		JRDataSource jRDataSource = null;
		if (reportData instanceof List) {
			jRDataSource = new JRBeanCollectionDataSource((List<?>) reportData);
		} else {
			jRDataSource = new JRBeanCollectionDataSource(Arrays.asList(reportData));
		}

		URL resource = getClass().getResource("/");
		String path = resource.getPath();
		String outDirPath = path + "../../" + reportsFolder;
		File outDir = new File(outDirPath);
		if (!outDir.exists()) {
			try {
				outDir.mkdir();
			} catch (SecurityException se) {
				throw new RuntimeException(se);
			}
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("isPdfEmbedded", true);
		params.put("SUBREPORT_DIR", path + templatePath);
		JasperReport jasperReport = JasperCompileManager.compileReport(path + templatePath + templateName + ".jrxml");
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, jRDataSource);

		String fileType = criteria.getFileType();
		String fileName = Calendar.getInstance().getTimeInMillis() + "." + fileType;
		String outFilePath = outDirPath + File.separator + fileName;
		switch (fileType) {
		case "xml":
			JasperExportManager.exportReportToXmlFile(jasperPrint, outFilePath, true);
			break;
		case "html":
			JasperExportManager.exportReportToHtmlFile(jasperPrint, outFilePath);
			break;
		case "pdf":
		default:
			JasperExportManager.exportReportToPdfFile(jasperPrint, outFilePath);
			break;
		}

		InputStream outStream = null;
		byte[] fileBytes = null;
		try {
			File outFile = new File(outFilePath);
			outStream = new BufferedInputStream(new FileInputStream(outFile));
			fileBytes = new byte[(int) outFile.length()];
			outStream.read(fileBytes);
		} finally {
			if (outStream != null) {
				outStream.close();
			}
		}

		ReportInfo data = new ReportInfo();
		data.setFileLocation(reportsFolder + File.separator + fileName);
		data.setBytes(fileBytes);
		return data;
	}
}
