package lk.express.bean;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * {@link PassengerType} has been implemented as an enum rather than a bean with
 * the understanding that the types of {@link PassengerType} is limited to these
 * enumerations.
 * </p>
 * 
 * <p>
 * TODO: However in the future we might need a way to define which age groups
 * would fall into each enumeration based on the transportation type, i.e. buses
 * might have a different age grouping compared to trains, etc.
 * </p>
 */
@XmlType(name = "PassengerType", namespace = "http://bean.express.lk")
@XmlRootElement
public enum PassengerType {
	Infant, Child, Adult
}
