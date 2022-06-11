package lk.express.admin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Entity
@Table(name = "agent_restriction")
@XmlType(name = "AgentRestriction", namespace = "http://admin.express.lk")
@XmlRootElement
public class AgentRestriction extends lk.express.bean.Entity {

	private static final long serialVersionUID = 1L;

	public enum RestrictionType {
		Allow, Deny
	}

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "agent_id")
	private Integer agentId;
	@Column(name = "bus_id")
	private Integer busId;
	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private RestrictionType type;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAgentId() {
		return agentId;
	}

	public void setAgentId(Integer agentId) {
		this.agentId = agentId;
	}

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public RestrictionType getType() {
		return type;
	}

	public void setType(RestrictionType type) {
		this.type = type;
	}
}
