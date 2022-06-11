package lk.express;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "RefundCriteria", namespace = "http://express.lk")
public class RefundCriteria extends PaymentRefundCriteria {

}
