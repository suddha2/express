package lk.express.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "city")
@XmlType(name = "City", namespace = "http://bean.express.lk")
@XmlRootElement
public class City extends lk.express.bean.Entity implements HasNameCode {

	private static final long serialVersionUID = -5650738298224376935L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "code")
	private String code;
	@Column(name = "name")
	private String name;
	@Column(name = "name_si")
	private String nameSi;
	@Column(name = "name_ta")
	private String nameTa;

	@Column (name = "district_id")
	private String districtId;
	
	@Column(name = "active", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean active = true;

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

	public String getNameSi() {
		return nameSi;
	}

	public void setNameSi(String nameSi) {
		this.nameSi = nameSi;
	}

	public String getNameTa() {
		return nameTa;
	}

	public void setNameTa(String nameTa) {
		this.nameTa = nameTa;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getDistrictId() {
		return districtId;
	}

	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}
}
