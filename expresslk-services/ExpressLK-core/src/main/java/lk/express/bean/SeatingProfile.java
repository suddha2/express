package lk.express.bean;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "seating_profile")
@XmlType(name = "SeatingProfile", namespace = "http://bean.express.lk")
@XmlRootElement
public class SeatingProfile extends lk.express.bean.Entity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "bus_type_id")
	private Integer busTypeId;
	@Column(name = "name")
	private String name;
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "seating_profile_seat", joinColumns = @JoinColumn(name = "seating_profile_id"))
	@Column(name = "seat_number")
	@Fetch(FetchMode.SELECT)
	private List<String> seatNumbers;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getSeatNumbers() {
		return seatNumbers;
	}

	public void setSeatNumbers(List<String> seatNumbers) {
		this.seatNumbers = seatNumbers;
	}
}
