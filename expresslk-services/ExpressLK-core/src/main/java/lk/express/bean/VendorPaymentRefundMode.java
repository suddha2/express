package lk.express.bean;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "VendorPaymentRefundMode", namespace = "http://bean.express.lk")
@XmlRootElement
public enum VendorPaymentRefundMode {

	Cash, Card, mCash, eZCash, BankTransfer, Mobitel, Warrant, Pass, PayAtBus;
}
