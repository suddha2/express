package lk.express.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity
@Table(name = "bus_bus_route")
@XmlType(name = "BusBusRoute", namespace = "http://bean.express.lk")
@XmlRootElement
public class BusBusRoute extends lk.express.bean.Entity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "bus_id")
	private Integer busId;
	@ManyToOne
	@JoinColumn(name = "bus_route_id")
	private BusRoute busRoute;
	@Column(name = "load_factor")
	private Float loadFactor;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public BusRoute getBusRoute() {
		return busRoute;
	}

	public void setBusRoute(BusRoute busRoute) {
		this.busRoute = busRoute;
	}

	public Float getLoadFactor() {
		return loadFactor;
	}

	public void setLoadFactor(Float loadFactor) {
		this.loadFactor = loadFactor;
	}
}
