package lk.express.reservation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.HasNameCode;

/**
 * TODO: Consider making this an enum
 */
@Entity
@Table(name = "booking_status")
@XmlType(name = "BookingStatus", namespace = "http://bean.express.lk")
@XmlRootElement
public class BookingStatus extends lk.express.bean.Entity implements HasNameCode {

	private static final long serialVersionUID = 1L;

	public static final String TENTATIVE = "TENT";
	public static final String CONFIRMED = "CONF";
	public static final String CANCELLED = "CANC";

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "code")
	private String code;
	@Column(name = "name")
	private String name;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
