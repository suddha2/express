package lk.express.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Entity
@Table(name = "bus_route_light")
@XmlType(name = "BusRouteLight", namespace = "http://beanexpress.lk")
@XmlRootElement
public class BusRouteLight extends LightEntity {

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
	@Column(name = "from_city_id")
	private Integer fromCity;
	@Column(name = "to_city_id")
	private Integer toCity;
	@Column(name = "gender_required", columnDefinition = "bit")
	@org.hibernate.annotations.Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean genderRequired;

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

	public Integer getFromCity() {
		return fromCity;
	}

	public void setFromCity(Integer fromCity) {
		this.fromCity = fromCity;
	}

	public Integer getToCity() {
		return toCity;
	}

	public void setToCity(Integer toCity) {
		this.toCity = toCity;
	}

	public Boolean getGenderRequired() {
		return genderRequired;
	}

	public void setGenderRequired(Boolean genderRequired) {
		this.genderRequired = genderRequired;
	}
}
