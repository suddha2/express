package lk.express.reporting;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import lk.express.ExpResponse;
import lk.express.reporting.ReportTypesResponse.ReportTypesData;
import lk.express.reports.ReportType;

@XmlRootElement
@XmlType(name = "ReportTypesResponse", namespace = "http://reports.express.lk")
@XmlSeeAlso(ReportTypesData.class)
public class ReportTypesResponse extends ExpResponse<ReportTypesData> {

	public ReportTypesResponse() {
	}

	public ReportTypesResponse(String errorMessage) {
		super(errorMessage);
	}

	public ReportTypesResponse(ReportTypesData data) {
		super(data);
	}

	@XmlSeeAlso(ReportType.class)
	public static class ReportTypesData {

		private List<ReportType> reportTypes;

		public ReportTypesData() {
		}

		public ReportTypesData(List<ReportType> reportTypes) {
			this.reportTypes = reportTypes;
		}

		public List<ReportType> getReportTypes() {
			return reportTypes;
		}

		public void setReportTypes(List<ReportType> reportTypes) {
			this.reportTypes = reportTypes;
		}
	}
}
