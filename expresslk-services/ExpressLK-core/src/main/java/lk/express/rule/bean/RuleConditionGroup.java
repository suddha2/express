package lk.express.rule.bean;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.rule.Combiner;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@DiscriminatorValue("RuleConditionGroup")
@XmlType(name = "RuleConditionGroup", namespace = "http://bean.rule.express.lk")
@XmlRootElement
public class RuleConditionGroup extends RuleCondition {

	private static final long serialVersionUID = 1L;

	@ManyToOne(cascade = javax.persistence.CascadeType.PERSIST)
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "first_rule")
	private RuleCondition firstRule;
	@Column(name = "combiner")
	@Enumerated(EnumType.STRING)
	private Combiner combiner;
	@ManyToOne(cascade = javax.persistence.CascadeType.PERSIST)
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "second_rule")
	private RuleCondition secondRule;

	public RuleCondition getFirstRule() {
		return firstRule;
	}

	public void setFirstRule(RuleCondition firstRule) {
		this.firstRule = firstRule;
	}

	public Combiner getCombiner() {
		return combiner;
	}

	public void setCombiner(Combiner combiner) {
		this.combiner = combiner;
	}

	public RuleCondition getSecondRule() {
		return secondRule;
	}

	public void setSecondRule(RuleCondition secondRule) {
		this.secondRule = secondRule;
	}
}
