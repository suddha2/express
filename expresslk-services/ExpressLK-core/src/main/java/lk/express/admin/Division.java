package lk.express.admin;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.HasNameCode;
import lk.express.db.HibernateUtil;
import lk.express.db.TriggerUpdatable;

import org.hibernate.criterion.Projections;

@Entity
@Table(name = "division")
@XmlType(name = "Division", namespace = "http://admin.express.lk")
@XmlRootElement
public class Division extends lk.express.bean.Entity implements HasNameCode, TriggerUpdatable {

	private static final long serialVersionUID = 1L;
	private static Integer divisionCount = null;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "code")
	private String code;
	@Column(name = "name")
	private String name;
	@ManyToOne
	@JoinColumn(name = "company_id")
	private Company company;
	@Column(name = "bitmask", updatable = false, insertable = false)
	private BigInteger bitmask;

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

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public BigInteger getBitmask() {
		return bitmask;
	}

	public void setBitmask(BigInteger bitmask) {
		this.bitmask = bitmask;
	}

	public static Integer getDivisionCount() {
		if (divisionCount == null) {
			Long count = (Long) HibernateUtil.getCurrentSession().createCriteria(Division.class)
					.setProjection(Projections.rowCount()).uniqueResult();
			divisionCount = count.intValue();
		}
		return divisionCount;
	}

	public static void resetDivisionCount() {
		divisionCount = null;
	}
}
