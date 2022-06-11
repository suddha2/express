package lk.express.admin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.HasNameCode;
import lk.express.db.TriggerUpdatable;

@Entity
@Table(name = "company")
@XmlType(name = "Company", namespace = "http://admin.express.lk")
@XmlRootElement
public class Company extends lk.express.bean.Entity implements HasNameCode, TriggerUpdatable {

	private static final long serialVersionUID = 1L;

	public static final String BBK = "BBK";

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "code")
	private String code;
	@Column(name = "name")
	private String name;
	@Column(name = "logo_file")
	private String logoFile;
	@Column(name = "bitmask", updatable = false, insertable = false)
	private Integer bitmask;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getLogoFile() {
		return logoFile;
	}

	public void setLogoFile(String logoFile) {
		this.logoFile = logoFile;
	}

	public Integer getBitmask() {
		return bitmask;
	}

	public void setBitmask(Integer bitmask) {
		this.bitmask = bitmask;
	}
}
