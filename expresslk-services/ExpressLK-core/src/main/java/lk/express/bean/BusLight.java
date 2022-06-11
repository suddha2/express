package lk.express.bean;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Entity
@Table(name = "bus_light")
@XmlType(name = "BusLight", namespace = "http://bean.express.lk")
@XmlRootElement
public class BusLight extends LightEntity implements HasAllowedDivisions {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "plate_number")
	private String plateNumber;
	@Column(name = "name")
	private String name;
	@Column(name = "contact")
	private String contact;
	@Column(name = "admin_contact")
	private String adminContact;
	@Column(name = "bus_type_id")
	private Integer busType;
	@Column(name = "travel_class_id")
	private Integer travelClass;
	@Column(name = "supplier_id")
	private Integer supplier;
	@Column(name = "division_id")
	private Integer division;
	@Column(name = "notification_method")
	private String notificationMethod;
	@Column(name = "rating")
	private Float rating;

	@Column(name = "markup_only", columnDefinition = "bit")
	@org.hibernate.annotations.Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean markupOnly;
	@Column(name = "cash_on_departure_allowed", columnDefinition = "bit")
	@org.hibernate.annotations.Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean cashOnDepartureAllowed;

	@Column(name = "allowed_divisions")
	private BigInteger allowedDivisions;

	@Column(name = "driver_id")
	private Integer driver;
	@Column(name = "conductor_id")
	private Integer conductor;
	@Column(name = "seating_profile_id")
	private Integer seatingProfile;

	@Column(name = "seconds_before_tb_end")
	private Integer secondsBeforeTBEnd;
	@Column(name = "seconds_before_web_end")
	private Integer secondsBeforeWebEnd;
	@Column(name = "seconds_before_ticketing_active")
	private Integer secondsBeforeTicketingActive;

	@Column(name = "permit_number")
	private String permitNumber;
	@Temporal(TemporalType.DATE)
	@Column(name = "permit_issue_date")
	private Date permitIssueDate;
	@Temporal(TemporalType.DATE)
	@Column(name = "permit_expiry_date")
	private Date permitExpiryDate;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getAdminContact() {
		return adminContact;
	}

	public void setAdminContact(String adminContact) {
		this.adminContact = adminContact;
	}

	public Integer getBusType() {
		return busType;
	}

	public void setBusType(Integer busType) {
		this.busType = busType;
	}

	public Integer getTravelClass() {
		return travelClass;
	}

	public void setTravelClass(Integer travelClass) {
		this.travelClass = travelClass;
	}

	public Integer getSupplier() {
		return supplier;
	}

	public void setSupplier(Integer supplier) {
		this.supplier = supplier;
	}

	public Integer getDivision() {
		return division;
	}

	public void setDivision(Integer division) {
		this.division = division;
	}

	public String getNotificationMethod() {
		return notificationMethod;
	}

	public void setNotificationMethod(String notificationMethod) {
		this.notificationMethod = notificationMethod;
	}

	public Float getRating() {
		return rating;
	}

	public void setRating(Float rating) {
		this.rating = rating;
	}

	public boolean isMarkupOnly() {
		return markupOnly;
	}

	public void setMarkupOnly(boolean markupOnly) {
		this.markupOnly = markupOnly;
	}

	public boolean isCashOnDepartureAllowed() {
		return cashOnDepartureAllowed;
	}

	public void setCashOnDepartureAllowed(boolean cashOnDepartureAllowed) {
		this.cashOnDepartureAllowed = cashOnDepartureAllowed;
	}

	@Override
	public BigInteger getAllowedDivisions() {
		return allowedDivisions;
	}

	@Override
	public void setAllowedDivisions(BigInteger allowedDivisions) {
		this.allowedDivisions = allowedDivisions;
	}

	public Integer getDriver() {
		return driver;
	}

	public void setDriver(Integer driver) {
		this.driver = driver;
	}

	public Integer getConductor() {
		return conductor;
	}

	public void setConductor(Integer conductor) {
		this.conductor = conductor;
	}

	public Integer getSeatingProfile() {
		return seatingProfile;
	}

	public void setSeatingProfile(Integer seatingProfile) {
		this.seatingProfile = seatingProfile;
	}

	public Integer getSecondsBeforeTBEnd() {
		return secondsBeforeTBEnd;
	}

	public void setSecondsBeforeTBEnd(Integer secondsBeforeTBEnd) {
		this.secondsBeforeTBEnd = secondsBeforeTBEnd;
	}

	public Integer getSecondsBeforeWebEnd() {
		return secondsBeforeWebEnd;
	}

	public void setSecondsBeforeWebEnd(Integer secondsBeforeWebEnd) {
		this.secondsBeforeWebEnd = secondsBeforeWebEnd;
	}

	public Integer getSecondsBeforeTicketingActive() {
		return secondsBeforeTicketingActive;
	}

	public void setSecondsBeforeTicketingActive(Integer secondsBeforeTicketingActive) {
		this.secondsBeforeTicketingActive = secondsBeforeTicketingActive;
	}

	public String getPermitNumber() {
		return permitNumber;
	}

	public void setPermitNumber(String permitNumber) {
		this.permitNumber = permitNumber;
	}

	public Date getPermitIssueDate() {
		return permitIssueDate;
	}

	public void setPermitIssueDate(Date permitIssueDate) {
		this.permitIssueDate = permitIssueDate;
	}

	public Date getPermitExpiryDate() {
		return permitExpiryDate;
	}

	public void setPermitExpiryDate(Date permitExpiryDate) {
		this.permitExpiryDate = permitExpiryDate;
	}

}
