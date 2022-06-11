package lk.express;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "AvailabilityCriteria", namespace = "http://express.lk")
public class AvailabilityCriteria {

	private String resultIndex;
	private int sectorIndex;

	private int scheduleId;
	private int boardingLocationId;
	private int droppingLocationId;
	private int busTypeId;

	public int getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
	}

	public int getBoardingLocationId() {
		return boardingLocationId;
	}

	public void setBoardingLocationId(int boardingLocationId) {
		this.boardingLocationId = boardingLocationId;
	}

	public int getDroppingLocationId() {
		return droppingLocationId;
	}

	public void setDroppingLocationId(int droppingLocationId) {
		this.droppingLocationId = droppingLocationId;
	}

	public int getBusTypeId() {
		return busTypeId;
	}

	public void setBusTypeId(int busTypeId) {
		this.busTypeId = busTypeId;
	}

	public String getResultIndex() {
		return resultIndex;
	}

	public void setResultIndex(String resultIndex) {
		this.resultIndex = resultIndex;
	}

	public int getSectorIndex() {
		return sectorIndex;
	}

	public void setSectorIndex(int sectorIndex) {
		this.sectorIndex = sectorIndex;
	}
}
