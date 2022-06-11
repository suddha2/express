package lk.express.bean;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import lk.express.admin.Division;
import lk.express.supplier.Conductor;
import lk.express.supplier.Driver;
import lk.express.supplier.Supplier;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity
@Table(name = "bus")
@XmlType(name = "Bus", namespace = "http://bean.express.lk")
@XmlRootElement
@XmlSeeAlso({ BusImage.class })
public class Bus extends lk.express.bean.Entity implements HasAllowedDivisions, BelongsToDivision {

	private static final long serialVersionUID = 1L;

	@XmlType(name = "NotificationMethod", namespace = "http://bean.express.lk")
	@XmlRootElement
	public enum NotificationMethod {
		Sms, Call, App, Printout
	}

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
	@ManyToOne
	@JoinColumn(name = "bus_type_id")
	private BusType busType;
	@ManyToOne
	@JoinColumn(name = "travel_class_id")
	private TravelClass travelClass;
	@ManyToOne
	@JoinColumn(name = "supplier_id")
	private Supplier supplier;
	@ManyToOne
	@JoinColumn(name = "division_id")
	private Division division;
	@Column(name = "notification_method")
	@Enumerated(EnumType.STRING)
	private NotificationMethod notificationMethod;
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

	// defaults
	@ManyToOne
	@JoinColumn(name = "driver_id")
	private Driver driver;
	@ManyToOne
	@JoinColumn(name = "conductor_id")
	private Conductor conductor;
	@ManyToOne
	@JoinColumn(name = "seating_profile_id")
	private SeatingProfile seatingProfile;

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

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "busId", cascade = CascadeType.ALL)
	private List<BusBusRoute> busRoutes;
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany()
	@JoinTable(name = "bus_amenity", joinColumns = { @JoinColumn(name = "bus_id") }, inverseJoinColumns = { @JoinColumn(name = "amenity_id") })
	private List<Amenity> amenities;

	public Bus() {
	}

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

	public BusType getBusType() {
		return busType;
	}

	public void setBusType(BusType busType) {
		this.busType = busType;
	}

	public TravelClass getTravelClass() {
		return travelClass;
	}

	public void setTravelClass(TravelClass travelClass) {
		this.travelClass = travelClass;
	}

	public List<Amenity> getAmenities() {
		if (amenities == null) {
			amenities = new ArrayList<>();
		}
		return amenities;
	}

	public void setAmenities(List<Amenity> amenities) {
		this.amenities = amenities;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@Override
	public Division getDivision() {
		return division;
	}

	@Override
	public void setDivision(Division division) {
		this.division = division;
	}

	public NotificationMethod getNotificationMethod() {
		return notificationMethod;
	}

	public void setNotificationMethod(NotificationMethod notificationMethod) {
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

	public List<BusBusRoute> getBusRoutes() {
		return busRoutes;
	}

	public void setBusRoutes(List<BusBusRoute> busRoutes) {
		this.busRoutes = busRoutes;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public Conductor getConductor() {
		return conductor;
	}

	public void setConductor(Conductor conductor) {
		this.conductor = conductor;
	}

	public SeatingProfile getSeatingProfile() {
		return seatingProfile;
	}

	public void setSeatingProfile(SeatingProfile seatingProfile) {
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

	@Override
	public BigInteger getAllowedDivisions() {
		return allowedDivisions;
	}

	@Override
	public void setAllowedDivisions(BigInteger allowedDivisions) {
		this.allowedDivisions = allowedDivisions;
	}

	public boolean hasAmenity(String code) {
		for (Amenity amenity : amenities) {
			if (amenity.getCode().equalsIgnoreCase(code)) {
				return true;
			}
		}
		return false;
	}
}
