package lk.express.admin;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.HasAllowedDivisions;
import lk.express.bean.VisibleToMultipleDivisions;

@Entity
@Table(name = "agent")
@XmlType(name = "Agent", namespace = "http://bean.express.lk")
@XmlRootElement
public class Agent extends lk.express.bean.Entity implements HasAllowedDivisions, VisibleToMultipleDivisions {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "name")
	private String name;

	@Column(name = "allowed_divisions")
	private BigInteger allowedDivisions;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
