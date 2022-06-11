package lk.express;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "SeatingInfoCriteria", namespace = "http://express.lk")
public class SeatingInfoCriteria {

	private int busTypeId;

	public int getBusTypeId() {
		return busTypeId;
	}

	public void setBusTypeId(int busTypeId) {
		this.busTypeId = busTypeId;
	}

}
