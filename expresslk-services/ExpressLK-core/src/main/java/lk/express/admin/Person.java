package lk.express.admin;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.Gender;
import lk.express.bean.HasAllowedDivisions;

import org.hibernate.envers.Audited;

/**
 * <p>
 * A {@link Person} is someone who might not necessarily have a login with
 * ExpressLK system. For eg. we can use this structure to capture information
 * about drivers and conductors who might not get a login.
 * </p>
 */
@Audited
@Entity
@Table(name = "person")
@XmlType(name = "Person", namespace = "http://admin.express.lk")
@XmlRootElement
public class Person extends lk.express.bean.Entity implements HasAllowedDivisions {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "first_name")
	private String firstName;
	@Column(name = "middle_name")
	private String middleName;
	@Column(name = "last_name")
	private String lastName;

	@Temporal(TemporalType.DATE)
	@Column(name = "dob")
	private Date dob;
	@Column(name = "nic")
	private String nic;
	@Column(name = "gender")
	@Enumerated(EnumType.STRING)
	private Gender gender;
	@Column(name = "email")
	private String email;
	@Column(name = "mobile_telephone")
	private String mobileTelephone;
	@Column(name = "home_telephone")
	private String homeTelephone;
	@Column(name = "work_telephone")
	private String workTelephone;
	@Column(name = "house_number")
	private String houseNumber;
	@Column(name = "street")
	private String street;
	@Column(name = "city")
	private String city;
	@Column(name = "postal_code")
	private Integer postalCode;

	@Column(name = "allowed_divisions")
	private BigInteger allowedDivisions;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@XmlElement(nillable = true)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@XmlElement(nillable = true)
	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	@XmlElement(nillable = true)
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@XmlElement(nillable = true)
	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	@XmlElement(nillable = true)
	public String getNic() {
		return nic;
	}

	public void setNic(String nic) {
		this.nic = nic;
	}

	@XmlElement(nillable = true)
	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	@XmlElement(nillable = true)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@XmlElement(nillable = true)
	public String getMobileTelephone() {
		return mobileTelephone;
	}

	public void setMobileTelephone(String mobileTelephone) {
		this.mobileTelephone = mobileTelephone;
	}

	@XmlElement(nillable = true)
	public String getHomeTelephone() {
		return homeTelephone;
	}

	public void setHomeTelephone(String homeTelephone) {
		this.homeTelephone = homeTelephone;
	}

	@XmlElement(nillable = true)
	public String getWorkTelephone() {
		return workTelephone;
	}

	public void setWorkTelephone(String workTelephone) {
		this.workTelephone = workTelephone;
	}

	@XmlElement(nillable = true)
	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	@XmlElement(nillable = true)
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	@XmlElement(nillable = true)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@XmlElement(nillable = true)
	public Integer getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(Integer postalCode) {
		this.postalCode = postalCode;
	}

	@Override
	public BigInteger getAllowedDivisions() {
		return allowedDivisions;
	}

	@Override
	public void setAllowedDivisions(BigInteger allowedDivisions) {
		this.allowedDivisions = allowedDivisions;
	}

	public String getFullName() {
		return (firstName != null ? firstName : "") + (lastName != null ? (" " + lastName) : "")
				+ (nic != null ? (" (" + nic + ")") : "");
	};

	public void setFullName(String fullName) {
		// black hole
	}
}
