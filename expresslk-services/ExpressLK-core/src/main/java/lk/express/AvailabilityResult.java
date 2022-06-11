package lk.express;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.admin.AgentAllocation;
import lk.express.bean.BusSeat;

@XmlRootElement
@XmlType(name = "AvailabilityResult", namespace = "http://express.lk")
public class AvailabilityResult {

	private List<String> vacantSeats;
	private List<Reservation> bookedSeats;
	private List<String> unavailableSeats;

	private List<BusSeat> seats;
	private List<AgentAllocation> agentAllocations;

	public List<BusSeat> getSeats() {
		return seats;
	}

	public void setSeats(List<BusSeat> seats) {
		this.seats = seats;
	}

	public List<String> getVacantSeats() {
		return vacantSeats;
	}

	public void setVacantSeats(List<String> vacantSeats) {
		this.vacantSeats = vacantSeats;
	}

	public List<Reservation> getBookedSeats() {
		return bookedSeats;
	}

	public void setBookedSeats(List<Reservation> bookedSeats) {
		this.bookedSeats = bookedSeats;
	}

	public List<String> getUnavailableSeats() {
		return unavailableSeats;
	}

	public void setUnavailableSeats(List<String> unavailableSeats) {
		this.unavailableSeats = unavailableSeats;
	}

	public List<AgentAllocation> getAgentAllocations() {
		if (agentAllocations == null) {
			agentAllocations = new ArrayList<>();
		}
		return agentAllocations;
	}

	public void setAgentAllocations(List<AgentAllocation> agentAllocations) {
		this.agentAllocations = agentAllocations;
	}
}
