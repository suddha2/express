package lk.express.bean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Point;

@Entity
@Table(name = "bus_mobile_location")
@XmlType(name = "BusMobileLocation", namespace = "http://bean.express.lk")
@XmlRootElement
public class BusMobileLocation extends lk.express.bean.Entity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "bus_id")
	private Integer busId;
	@Column(name = "location")
	@Type(type = "org.hibernate.spatial.GeometryType")
	private Point location;
	@Column(name = "speed")
	private Float speed;
	@Column(name = "bearing")
	private Float bearing;
	@Column(name = "created_time")
	private Date createdTime;

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

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public Float getSpeed() {
		return speed;
	}

	public void setSpeed(Float speed) {
		this.speed = speed;
	}

	public Float getBearing() {
		return bearing;
	}

	public void setBearing(Float bearing) {
		this.bearing = bearing;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
}
