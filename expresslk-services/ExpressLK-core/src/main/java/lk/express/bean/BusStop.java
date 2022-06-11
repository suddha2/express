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

@Entity
@Table(name = "bus_stop")
@XmlType(name = "BusStop", namespace = "http://bean.express.lk")
@XmlRootElement
public class BusStop extends lk.express.bean.Entity {

	private static final long serialVersionUID = -1749695330570666565L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "name")
	private String name;
	@Column(name = "name_si")
	private String nameSi;
	@Column(name = "name_ta")
	private String nameTa;
	@ManyToOne
	@JoinColumn(name = "city_id")
	private City city;
	@Column(name = "longitude")
	private Float longitude;
	@Column(name = "latitude")
	private Float latitude;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameSi() {
		return nameSi;
	}

	public void setNameSi(String nameSi) {
		this.nameSi = nameSi;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}
}
