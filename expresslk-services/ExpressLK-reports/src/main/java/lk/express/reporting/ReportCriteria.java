package lk.express.reporting;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.reports.ReportParameter;
import lk.express.reports.ReportTypeEnum;

@XmlType(name = "ReportCriteria", namespace = "http://reports.express.lk")
@XmlRootElement
public class ReportCriteria {

	private static final String LANG_EN = "en";

	private String langCode;
	private String format;
	private List<String> dispatchMethods;
	private ReportTypeEnum reportType;
	private List<ReportParameter> reportParams;

	public String getLangSuffix() {
		if (langCode == null || LANG_EN.equalsIgnoreCase(langCode)) {
			return "";
		}
		return "_" + langCode.toLowerCase();
	}

	public String getFileType() {
		if ("xml".equalsIgnoreCase(format)) {
			return "xml";
		}
		if ("html".equalsIgnoreCase(format)) {
			return "html";
		}
		return "pdf";
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public List<String> getDispatchMethods() {
		return dispatchMethods;
	}

	public void setDispatchMethods(List<String> dispatchMethods) {
		this.dispatchMethods = dispatchMethods;
	}

	public List<ReportParameter> getReportParams() {
		if (reportParams == null) {
			reportParams = new ArrayList<>();
		}
		return reportParams;
	}

	public void setReportParams(List<ReportParameter> reportParams) {
		this.reportParams = reportParams;
	}

	public ReportTypeEnum getReportType() {
		return reportType;
	}

	public void setReportType(ReportTypeEnum reportType) {
		this.reportType = reportType;
	}

	public ReportParameter getParameter(String name) {
		for (ReportParameter param : reportParams) {
			if (name.equals(param.getName())) {
				return param;
			}
		}
		return null;
	}

	public void addParameter(ReportParameter param) {
		getReportParams().add(param);
	}
}
