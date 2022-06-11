package lk.express.cnx;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "CancellationCause", namespace = "http://cnx.express.lk")
@XmlRootElement
public enum CancellationCause {
	ClientRequested, NonPayment, DataEntryError, Other
}
