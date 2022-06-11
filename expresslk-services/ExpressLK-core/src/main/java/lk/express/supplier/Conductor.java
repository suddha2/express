package lk.express.supplier;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.admin.Person;
import lk.express.bean.HasAllowedDivisions;
import lk.express.bean.HasPerson;

import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "conductor")
@XmlType(name = "Conductor", namespace = "http://supplier.express.lk")
@XmlRootElement
public class Conductor extends lk.express.bean.Entity implements HasAllowedDivisions, HasPerson {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;

	@Column(name = "ntc_registration_number")
	private String ntcRegistrationNumber;
	@Temporal(TemporalType.DATE)
	@Column(name = "ntc_registration_expiry_date")
	private Date ntcRegistrationExpiryDate;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "person_id")
	private Person person;
	@Column(name = "nick_name")
	private String nickName;

	@Column(name = "allowed_divisions")
	private BigInteger allowedDivisions;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNtcRegistrationNumber() {
		return ntcRegistrationNumber;
	}

	public void setNtcRegistrationNumber(String ntcRegistrationNumber) {
		this.ntcRegistrationNumber = ntcRegistrationNumber;
	}

	@Override
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@Override
	public BigInteger getAllowedDivisions() {
		return allowedDivisions;
	}

	@Override
	public void setAllowedDivisions(BigInteger allowedDivisions) {
		this.allowedDivisions = allowedDivisions;
	}

	@Override
	public String getFullName() {
		return (person.getFirstName() != null ? person.getFirstName() : "")
				+ (person.getLastName() != null ? (" " + person.getLastName()) : "")
				+ (nickName != null ? (" (" + nickName + ")") : "");
	}

	public Date getNtcRegistrationExpiryDate() {
		return ntcRegistrationExpiryDate;
	}

	public void setNtcRegistrationExpiryDate(Date ntcRegistrationExpiryDate) {
		this.ntcRegistrationExpiryDate = ntcRegistrationExpiryDate;
	}
}
