package lk.express;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "JourneyPerformedCriteria", namespace = "http://express.lk")
public class JourneyPerformedCriteria {

	private int scheduleId;
	private String seatNumber;
	private boolean journeyPerformed;

	public int getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
	}

	public String getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}

	public boolean isJourneyPerformed() {
		return journeyPerformed;
	}

	public void setJourneyPerformed(boolean journeyPerformed) {
		this.journeyPerformed = journeyPerformed;
	}
}
