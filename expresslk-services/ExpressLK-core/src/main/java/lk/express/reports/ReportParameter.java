package lk.express.reports;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.ValueType;

@Entity
@Table(name = "report_param")
@XmlType(name = "ReportParameter", namespace = "http://reports.express.lk")
@XmlRootElement
public class ReportParameter extends lk.express.bean.Entity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "report_id")
	private Integer reportId;
	@Column(name = "name")
	private String name;
	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private ValueType type;
	@Column(name = "visible", columnDefinition = "bit")
	@org.hibernate.annotations.Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean visible;

	@Transient
	private List<String> values;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ValueType getType() {
		return type;
	}

	public void setType(ValueType type) {
		this.type = type;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public Object getObject() {
		return type.getObject(values);
	}
}
