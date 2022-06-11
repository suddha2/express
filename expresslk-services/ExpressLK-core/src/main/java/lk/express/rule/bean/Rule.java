package lk.express.rule.bean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.HasValidPeriod;
import lk.express.rule.ApplicationType;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "rule")
@Inheritance(strategy = InheritanceType.JOINED)
@XmlType(name = "Rule", namespace = "http://bean.rule.express.lk")
@XmlRootElement
public abstract class Rule extends lk.express.bean.Entity implements HasValidPeriod {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "name")
	private String name;
	@Column(name = "description", columnDefinition = "text")
	private String description;
	@Column(name = "salience")
	private float salience;
	@Column(name = "scheme")
	private String scheme;
	@ManyToOne(cascade = javax.persistence.CascadeType.PERSIST)
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "rule_condition")
	private RuleCondition condition;
	@Column(name = "amount")
	private float amount;
	@Column(name = "application_type")
	@Enumerated(EnumType.STRING)
	private ApplicationType applicationType;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_time")
	private Date startTime;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_time")
	private Date endTime;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getSalience() {
		return salience;
	}

	public void setSalience(float salience) {
		this.salience = salience;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public RuleCondition getCondition() {
		return condition;
	}

	public void setCondition(RuleCondition condition) {
		this.condition = condition;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public ApplicationType getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(ApplicationType applicationType) {
		this.applicationType = applicationType;
	}

	@Override
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Override
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}
