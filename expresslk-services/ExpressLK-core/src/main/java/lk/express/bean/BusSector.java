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

import org.hibernate.annotations.Type;

@Entity
@Table(name = "bus_sector")
@XmlType(name = "BusSector", namespace = "http://bean.express.lk")
@XmlRootElement
public class BusSector extends lk.express.bean.Entity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "bus_route_id")
	private BusRoute busRoute;
	@ManyToOne
	@JoinColumn(name = "from_city_id")
	private City fromCity;
	@ManyToOne
	@JoinColumn(name = "to_city_id")
	private City toCity;

	@Column(name = "active", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean active = true;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BusRoute getBusRoute() {
		return busRoute;
	}

	public void setBusRoute(BusRoute busRoute) {
		this.busRoute = busRoute;
	}

	public City getFromCity() {
		return fromCity;
	}

	public void setFromCity(City fromCity) {
		this.fromCity = fromCity;
	}

	public City getToCity() {
		return toCity;
	}

	public void setToCity(City toCity) {
		this.toCity = toCity;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
