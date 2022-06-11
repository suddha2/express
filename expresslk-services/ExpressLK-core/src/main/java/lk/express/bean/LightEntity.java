package lk.express.bean;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import lk.express.admin.UserLight;
import lk.express.reservation.BookingLight;
import lk.express.schedule.BusScheduleLight;

@XmlType(name = "LightEntity", namespace = "http://bean.express.lk")
@XmlSeeAlso({ BookingLight.class, BusLight.class, BusRouteLight.class, BusScheduleLight.class, UserLight.class })
public abstract class LightEntity extends Entity {

	private static final long serialVersionUID = 1L;
}
