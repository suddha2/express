package lk.express;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlSeeAlso({ ResultLeg.class, ResultSector.class })
@XmlType(name = "HasDepartureArrival", namespace = "http://express.lk")
public interface HasDepartureArrival {

	public static final String DEPARTURE_TIME = "departureTime";
	public static final String ARRIVAL_TIME = "arrivalTime";

	Date getDepartureTime();

	Date getArrivalTime();
}
