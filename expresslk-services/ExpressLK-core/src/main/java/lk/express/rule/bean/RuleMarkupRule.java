package lk.express.rule.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "markup_rule")
@XmlType(name = "RuleMarkupRule", namespace = "http://bean.rule.express.lk")
@XmlRootElement
public class RuleMarkupRule extends Rule {

	private static final long serialVersionUID = 1L;

	@Column(name = "is_margin", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean isMargin;

	public boolean isMargin() {
		return isMargin;
	}

	public void setMargin(boolean isMargin) {
		this.isMargin = isMargin;
	}
}
