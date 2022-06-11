package lk.express.reporting;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import lk.express.ExpResponse;

@XmlRootElement
@XmlType(name = "GenerateReportResponse", namespace = "http://reports.express.lk")
@XmlSeeAlso(ReportInfo.class)
public class GenerateReportResponse extends ExpResponse<ReportInfo> {

	public GenerateReportResponse() {
	}

	public GenerateReportResponse(String errorMessage) {
		super(errorMessage);
	}

	public GenerateReportResponse(ReportInfo report) {
		super(report);
	}
}
