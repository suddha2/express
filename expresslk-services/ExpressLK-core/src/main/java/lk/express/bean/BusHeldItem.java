package lk.express.bean;

import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.schedule.BusSchedule;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "bus_held_item")
@XmlType(name = "BusHeldItem", namespace = "http://bean.express.lk")
@XmlRootElement
public class BusHeldItem extends lk.express.bean.Entity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;

	@Column(name = "session_id")
	private String sessionId;
	@Column(name = "idx")
	private int index;
	@Column(name = "result_idx")
	private String resultIndex;
	@Column(name = "sector_idx")
	private int sectorIndex;
	@ManyToOne
	@JoinColumn(name = "schedule_id")
	private BusSchedule schedule;
	@ManyToOne
	@JoinColumn(name = "from_bus_stop_id")
	private BusStop fromBusStop;
	@ManyToOne
	@JoinColumn(name = "to_bus_stop_id")
	private BusStop toBusStop;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "timestamp", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date timestamp;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "bus_held_item_seat", joinColumns = @JoinColumn(name = "bus_held_item_id"))
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

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
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

	public BusSchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(BusSchedule schedule) {
		this.schedule = schedule;
	}

	public BusStop getFromBusStop() {
		return fromBusStop;
	}

	public void setFromBusStop(BusStop fromBusStop) {
		this.fromBusStop = fromBusStop;
	}

	public BusStop getToBusStop() {
		return toBusStop;
	}

	public void setToBusStop(BusStop toBusStop) {
		this.toBusStop = toBusStop;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public List<String> getSeatNumbers() {
		return seatNumbers;
	}

	public void setSeatNumbers(List<String> seatNumbers) {
		this.seatNumbers = seatNumbers;
	}
}
