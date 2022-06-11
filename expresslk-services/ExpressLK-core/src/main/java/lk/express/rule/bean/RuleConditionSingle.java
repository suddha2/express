package lk.express.rule.bean;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.ValueType;
import lk.express.rule.Qualifier;

@Entity
@DiscriminatorValue("RuleConditionSingle")
@XmlType(name = "RuleConditionSingle", namespace = "http://bean.rule.express.lk")
@XmlRootElement
public class RuleConditionSingle extends RuleCondition {

	private static final long serialVersionUID = 1L;

	@Column(name = "property")
	private String property;
	@Column(name = "qualifier")
	@Enumerated(EnumType.STRING)
	private Qualifier qualifier;
	@Column(name = "value_type")
	@Enumerated(EnumType.STRING)
	private ValueType valueType;
	@Column(name = "value_string")
	private String valueString;

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public Qualifier getQualifier() {
		return qualifier;
	}

	public void setQualifier(Qualifier qualifier) {
		this.qualifier = qualifier;
	}

	public ValueType getValueType() {
		return valueType;
	}

	public void setValueType(ValueType valueType) {
		this.valueType = valueType;
	}

	public String getValueString() {
		return valueString;
	}

	public void setValueString(String valueString) {
		this.valueString = valueString;
	}
}
