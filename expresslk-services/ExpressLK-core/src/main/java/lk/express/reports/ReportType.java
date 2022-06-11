package lk.express.reports;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.HasAllowedDivisions;
import lk.express.bean.VisibleToMultipleDivisions;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "report_type")
@XmlType(name = "ReportType", namespace = "http://reports.express.lk")
@XmlRootElement
public class ReportType extends lk.express.bean.Entity implements HasAllowedDivisions, VisibleToMultipleDivisions {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "report_name")
	private String reportName;
	@Enumerated(EnumType.STRING)
	@Column(name = "report_type")
	private ReportTypeEnum reportType;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "reportId", cascade = CascadeType.ALL)
	private List<ReportParameter> reportParameters;

	@Column(name = "allowed_divisions")
	private BigInteger allowedDivisions;

	public ReportType() {
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public ReportTypeEnum getReportType() {
		return reportType;
	}

	public void setReportType(ReportTypeEnum reportType) {
		this.reportType = reportType;
	}

	public List<ReportParameter> getParameters() {
		return reportParameters;
	}

	public void setParameters(List<ReportParameter> parameters) {
		this.reportParameters = parameters;
	}

	@Override
	public BigInteger getAllowedDivisions() {
		return allowedDivisions;
	}

	@Override
	public void setAllowedDivisions(BigInteger allowedDivisions) {
		this.allowedDivisions = allowedDivisions;
	}
}