package lk.express.rule.bean;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Entity
@Table(name = "tax_rule")
@XmlType(name = "RuleTaxRule", namespace = "http://bean.rule.express.lk")
@XmlRootElement
public class RuleTaxRule extends Rule {

	private static final long serialVersionUID = 1L;

}
