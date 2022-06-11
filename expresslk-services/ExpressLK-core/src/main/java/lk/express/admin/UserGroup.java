package lk.express.admin;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.BelongsToDivision;
import lk.express.bean.HasNameCode;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "user_group")
@XmlType(name = "UserGroup", namespace = "http://admin.express.lk")
@XmlRootElement
public class UserGroup extends lk.express.bean.Entity implements HasNameCode, BelongsToDivision {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "code")
	private String code;
	@Column(name = "name")
	private String name;
	@Column(name = "description", columnDefinition = "text")
	private String description;
	@ManyToOne
	@JoinColumn(name = "division_id")
	private Division division;
	@ManyToOne
	@JoinColumn(name = "agent_id")
	private Agent agent;
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(cascade = { CascadeType.REFRESH })
	@JoinTable(name = "user_group_permission", joinColumns = { @JoinColumn(name = "user_group_id") }, inverseJoinColumns = { @JoinColumn(name = "permission_id") })
	private List<Permission> permission;
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(cascade = { CascadeType.REFRESH })
	@JoinTable(name = "user_group_division", joinColumns = { @JoinColumn(name = "user_group_id") }, inverseJoinColumns = { @JoinColumn(name = "division_id") })
	private List<Division> visibleDivisions;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Division getDivision() {
		return division;
	}

	@Override
	public void setDivision(Division division) {
		this.division = division;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public List<Permission> getPermission() {
		if (permission == null) {
			permission = new ArrayList<>();
		}
		return permission;
	}

	public void setPermission(List<Permission> permission) {
		this.permission = permission;
	}

	public List<Division> getVisibleDivisions() {
		return visibleDivisions;
	}

	public void setVisibleDivisions(List<Division> visibleDivisions) {
		this.visibleDivisions = visibleDivisions;
	}

	public boolean hasPermission(String code) {
		for (Permission p : permission) {
			if (p.match(code)) {
				return true;
			}
		}
		return false;
	}

	public BigInteger getVisibleDivisionsBitmask() {
		BigInteger bitmask = BigInteger.ZERO;
		for (Division division : visibleDivisions) {
			bitmask = bitmask.or(division.getBitmask());
		}
		return bitmask;
	}
}
