package lk.express;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.BusSeat;

@XmlRootElement
@XmlType(name = "SeatingInfoCriteria", namespace = "http://express.lk")
public class SeatingInfoResult {

	List<BusSeat> seats;

	public List<BusSeat> getSeats() {
		if (seats == null) {
			seats = new ArrayList<BusSeat>();
		}
		return seats;
	}

	public void setSeats(List<BusSeat> seats) {
		this.seats = seats;
	}

}
