package lk.express.bean;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "bus_route")
@XmlType(name = "BusRoute", namespace = "http://bean.express.lk")
@XmlRootElement
public class BusRoute extends lk.express.bean.Entity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "route_number")
	private String routeNumber;
	@Column(name = "display_number")
	private String displayNumber;
	@Column(name = "name")
	private String name;
	@ManyToOne
	@JoinColumn(name = "from_city_id")
	private City fromCity;
	@ManyToOne
	@JoinColumn(name = "to_city_id")
	private City toCity;
	@Column(name = "gender_required", columnDefinition = "bit")
	@org.hibernate.annotations.Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean genderRequired;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "routeId", cascade = CascadeType.ALL)
	private Set<BusRouteBusStop> routeStops;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRouteNumber() {
		return routeNumber;
	}

	public void setRouteNumber(String routeNumber) {
		this.routeNumber = routeNumber;
	}

	public String getDisplayNumber() {
		return displayNumber;
	}

	public void setDisplayNumber(String displayNumber) {
		this.displayNumber = displayNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Set<BusRouteBusStop> getRouteStops() {
		return routeStops;
	}

	public void setRouteStops(Set<BusRouteBusStop> routeStops) {
		this.routeStops = routeStops;
	}

	public Boolean isGenderRequired() {
		return genderRequired;
	}

	public void setGenderRequired(Boolean genderRequired) {
		this.genderRequired = genderRequired;
	}
}
