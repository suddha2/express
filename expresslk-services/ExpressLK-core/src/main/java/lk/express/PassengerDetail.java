package lk.express;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.PassengerType;

@XmlRootElement
@XmlType(name = "PassengerDetail", namespace = "http://express.lk")
public class PassengerDetail extends PersonDetail {

	private int index;
	private PassengerType passengerType;

	public boolean isInfant() {
		return passengerType.equals(PassengerType.Infant);
	}

	public boolean isChild() {
		return passengerType.equals(PassengerType.Child);
	}

	public boolean isAdult() {
		return passengerType.equals(PassengerType.Adult);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public PassengerType getPassengerType() {
		return passengerType;
	}

	public void setPassengerType(PassengerType passengerType) {
		this.passengerType = passengerType;
	}
}
