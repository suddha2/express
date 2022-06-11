package lk.express.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Entity
@Table(name = "operational_stage")
@XmlType(name = "OperationalStage", namespace = "http://bean.express.lk")
@XmlRootElement
public class OperationalStage extends lk.express.bean.Entity implements HasNameCode {

	private static final long serialVersionUID = 1L;

	public static final String OpenForBooking = "OFB";
	public static final String ClosedForBooking = "CFB";

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "ordinal")
	private Integer ordinal;
	@Column(name = "code")
	private String code;
	@Column(name = "name")
	private String name;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(Integer ordinal) {
		this.ordinal = ordinal;
	}

	@Override
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
