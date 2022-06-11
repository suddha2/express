package lk.express;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "SeatAllocations", namespace = "http://express.lk")
public class SeatAllocations {

	private int heldItemId;
	private List<Allocation> allocations = new ArrayList<Allocation>();

	public int getHeldItemId() {
		return heldItemId;
	}

	public void setHeldItemId(int heldItemId) {
		this.heldItemId = heldItemId;
	}

	public List<Allocation> getAllocations() {
		return allocations;
	}

	public void setAllocations(List<Allocation> allocations) {
		this.allocations = allocations;
	}

	public Integer getPassengerIndex(String seatNumber) {
		for (Allocation a : allocations) {
			if (a.getSeatNumber().equals(seatNumber)) {
				return a.getPassengerIndex();
			}
		}
		return null;
	}

	public void setSeatAllocation(String seatNumber, Integer passengerIndex) {
		Allocation a = new Allocation();
		a.setSeatNumber(seatNumber);
		a.setPassengerIndex(passengerIndex);
		allocations.add(a);
	}

	@XmlType(name = "Allocation", namespace = "http://express.lk")
	public static class Allocation {

		private String seatNumber;
		private int passengerIndex;

		public String getSeatNumber() {
			return seatNumber;
		}

		public void setSeatNumber(String seatNumber) {
			this.seatNumber = seatNumber;
		}

		public int getPassengerIndex() {
			return passengerIndex;
		}

		public void setPassengerIndex(int passengerIndex) {
			this.passengerIndex = passengerIndex;
		}
	}
}
