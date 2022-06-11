package lk.express;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "HoldCriteria", namespace = "http://express.lk")
public class HoldCriteria {

	private String resultIndex;
	private List<SeatCriteria> seatCriterias;

	private int boardingPoint;
	private int droppingPoint;

	public int getBoardingPoint() {
		return boardingPoint;
	}

	public void setBoardingPoint(int boardingPoint) {
		this.boardingPoint = boardingPoint;
	}

	public int getDroppingPoint() {
		return droppingPoint;
	}

	public void setDroppingPoint(int droppingPoint) {
		this.droppingPoint = droppingPoint;
	}

	public List<SeatCriteria> getSeatCriterias() {
		return seatCriterias;
	}

	public void setSeatCriterias(List<SeatCriteria> seatCriterias) {
		this.seatCriterias = seatCriterias;
	}

	public String getResultIndex() {
		return resultIndex;
	}

	public void setResultIndex(String resultIndex) {
		this.resultIndex = resultIndex;
	}

	@XmlType(name = "SeatCriteria", namespace = "http://express.lk")
	public static class SeatCriteria {

		private int sectorNumber;
		private List<String> seats;

		public int getSectorNumber() {
			return sectorNumber;
		}

		public void setSectorNumber(int sectorNumber) {
			this.sectorNumber = sectorNumber;
		}

		public List<String> getSeats() {
			return seats;
		}

		public void setSeats(List<String> seats) {
			this.seats = seats;
		}
	}
}
