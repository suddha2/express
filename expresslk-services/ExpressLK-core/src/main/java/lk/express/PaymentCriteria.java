package lk.express;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "PaymentCriteria", namespace = "http://express.lk")
public class PaymentCriteria extends PaymentRefundCriteria {

}
