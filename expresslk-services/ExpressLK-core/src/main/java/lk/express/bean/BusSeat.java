package lk.express.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Entity
@Table(name = "bus_seat")
@XmlType(name = "BusSeat", namespace = "http://bean.express.lk")
@XmlRootElement
public class BusSeat extends lk.express.bean.Entity {

	private static final long serialVersionUID = 1L;

	public static final String SEAT_TYPE_ORDINARY = "ordinary";
	public static final String SEAT_TYPE_WINDOW = "window";
	public static final String SEAT_TYPE_AISLE = "aisle";

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "bus_type_id")
	private Integer busTypeId;
	@Column(name = "seat_number")
	private String number;
	@Column(name = "x")
	private Integer x;
	@Column(name = "y")
	private Integer y;
	@Column(name = "type")
	private String seatType;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBusTypeId() {
		return busTypeId;
	}

	public void setBusTypeId(Integer busTypeId) {
		this.busTypeId = busTypeId;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public String getSeatType() {
		return seatType;
	}

	public void setSeatType(String seatType) {
		this.seatType = seatType;
	}

	@Override
	public String toString() {
		return number;
	}

	public boolean isWindowSeat() {
		return seatType.equals(SEAT_TYPE_WINDOW);
	}

	public boolean isAisleSeat() {
		return seatType.equals(SEAT_TYPE_AISLE);
	}
}
