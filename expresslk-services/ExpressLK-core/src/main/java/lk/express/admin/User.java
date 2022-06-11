package lk.express.admin;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.BelongsToDivision;
import lk.express.bean.Gender;
import lk.express.db.dao.DAOFactory;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "user")
@XmlType(name = "User", namespace = "http://admin.express.lk")
@XmlRootElement
public class User extends lk.express.bean.Entity implements BelongsToDivision {

	private static final long serialVersionUID = 1L;
	protected static final DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);

	public static final String SYS_USER = "_SYSTEM_";

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "username")
	private String username;
	@JsonIgnore
	@XmlTransient
	@Column(name = "password")
	private String password;
	@ManyToOne
	@JoinColumn(name = "account_status_code", referencedColumnName = "code")
	private AccountStatus status;
	@ManyToOne
	@JoinColumn(name = "division_id")
	private Division division;
	@Column(name = "accountable", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean accountable;
	@ManyToOne
	@JoinColumn(name = "agent_id")
	private Agent agent;

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

	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(cascade = { CascadeType.REFRESH })
	@JoinTable(name = "user_user_group", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "user_group_id") })
	private List<UserGroup> userGroups;

	public User() {
	}

	public User(String username) {
		this.username = username;
	}

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

	@JsonIgnore
	@XmlTransient
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}

	@Override
	public Division getDivision() {
		return division;
	}

	@Override
	public void setDivision(Division division) {
		this.division = division;
	}

	public Boolean getAccountable() {
		return accountable;
	}

	public void setAccountable(Boolean accountable) {
		this.accountable = accountable;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
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

	public List<UserGroup> getUserGroups() {
		if (userGroups == null) {
			userGroups = new ArrayList<>();
		}
		return userGroups;
	}

	public void setUserGroups(List<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}

	public boolean hasPermission(String code) {
		for (UserGroup ug : userGroups) {
			if (ug.hasPermission(code)) {
				return true;
			}
		}
		return false;
	}

	public BigInteger getVisibleDivisionsBitmask() {
		BigInteger bitmask = BigInteger.ZERO;
		for (UserGroup ug : userGroups) {
			bitmask = bitmask.or(ug.getVisibleDivisionsBitmask());
		}
		return bitmask;
	}

	public BigInteger getDivisionsBitmask() {
		return division.getBitmask();
	}
}
