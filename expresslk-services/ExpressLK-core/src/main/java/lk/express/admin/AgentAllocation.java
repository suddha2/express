package lk.express.admin;

import java.math.BigInteger;
import java.util.ArrayList;
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.HasAllowedDivisions;
import lk.express.bean.VisibleToMultipleDivisions;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "agent_allocation")
@XmlType(name = "AgentAllocation", namespace = "http://admin.express.lk")
@XmlRootElement
public class AgentAllocation extends lk.express.bean.Entity implements HasAllowedDivisions, VisibleToMultipleDivisions {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "bus_route_id")
	private Integer busRouteId;
	@Column(name = "division_id")
	private Integer divisionId;
	@ManyToOne
	@JoinColumn(name = "agent_id")
	private Agent agent;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "agent_allocation_seat", joinColumns = @JoinColumn(name = "agent_allocation_id"))
	@Column(name = "seat_number")
	@Fetch(FetchMode.SELECT)
	private List<String> seatNumbers;

	@Column(name = "allowed_divisions")
	private BigInteger allowedDivisions;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBusRouteId() {
		return busRouteId;
	}

	public void setBusRouteId(Integer busRouteId) {
		this.busRouteId = busRouteId;
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public List<String> getSeatNumbers() {
		if (seatNumbers == null) {
			seatNumbers = new ArrayList<>();
		}
		return seatNumbers;
	}

	public void setSeatNumbers(List<String> seatNumbers) {
		this.seatNumbers = seatNumbers;
	}

	@Override
	public BigInteger getAllowedDivisions() {
		return allowedDivisions;
	}

	@Override
	public void setAllowedDivisions(BigInteger allowedDivisions) {
		this.allowedDivisions = allowedDivisions;
	}
}
