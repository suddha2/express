package lk.express.bean;

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
@Table(name = "bus_image")
@XmlType(name = "BusImage", namespace = "http://bean.express.lk")
@XmlRootElement
public class BusImage extends lk.express.bean.Entity {

	@XmlType(name = "BusImageType", namespace = "http://bean.express.lk")
	@XmlRootElement
	public enum BusImageType {
		Thumbnail, Main, Other
	}

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "bus_id")
	private Integer busId;
	@Column(name = "image")
	private byte[] image;
	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private BusImageType type;

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

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public BusImageType getType() {
		return type;
	}

	public void setType(BusImageType type) {
		this.type = type;
	}
}
