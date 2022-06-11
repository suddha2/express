package lk.express.admin;

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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.Gender;
import lk.express.bean.LightEntity;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "user_light")
@XmlType(name = "UserLight", namespace = "http://admin.express.lk")
@XmlRootElement
public class UserLight extends LightEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "username")
	private String username;
	@JsonIgnore
	@XmlTransient
	@Column(name = "password")
	private String password;
	@Column(name = "account_status_code")
	private String status;
	@Column(name = "division_id")
	private Integer division;
	@Column(name = "accountable", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean accountable;
	@Column(name = "agent_id")
	private Integer agent;

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

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getDivision() {
		return division;
	}

	public void setDivision(Integer division) {
		this.division = division;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getNic() {
		return nic;
	}

	public void setNic(String nic) {
		this.nic = nic;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileTelephone() {
		return mobileTelephone;
	}

	public void setMobileTelephone(String mobileTelephone) {
		this.mobileTelephone = mobileTelephone;
	}

	public String getHomeTelephone() {
		return homeTelephone;
	}

	public void setHomeTelephone(String homeTelephone) {
		this.homeTelephone = homeTelephone;
	}

	public String getWorkTelephone() {
		return workTelephone;
	}

	public void setWorkTelephone(String workTelephone) {
		this.workTelephone = workTelephone;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Integer getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(Integer postalCode) {
		this.postalCode = postalCode;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
