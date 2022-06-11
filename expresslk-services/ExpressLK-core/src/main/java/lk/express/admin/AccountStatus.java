package lk.express.admin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.HasNameCode;

@Entity
@Table(name = "account_status")
@XmlType(name = "AccountStatus", namespace = "http://bean.express.lk")
@XmlRootElement
public class AccountStatus extends lk.express.bean.Entity implements HasNameCode {

	private static final long serialVersionUID = 1L;

	public static final String ACTIVE = "ACT";
	public static final String INACTIVE = "INA";

	@Id
	@GeneratedValue
	private Integer id;
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
