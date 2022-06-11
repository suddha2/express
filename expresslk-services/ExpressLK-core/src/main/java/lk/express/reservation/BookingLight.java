package lk.express.reservation;

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

import lk.express.bean.HasAllowedDivisions;
import lk.express.bean.LightEntity;

@Entity
@Table(name = "booking_light")
@XmlType(name = "BookingLight", namespace = "http://reservation.express.lk")
@XmlRootElement
public class BookingLight extends LightEntity implements HasAllowedDivisions {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "reference")
	private String reference;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "booking_time")
	private Date bookingTime;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "expiry_time")
	private Date expiryTime;
	@Column(name = "chargeable")
	private double chargeable;
	@Column(name = "status_code")
	private String status;
	@Column(name = "cancellation_cause")
	private String cancellationCause;
	@Column(name = "client_id")
	private Integer client;
	@Column(name = "user_id")
	private Integer user;
	@Column(name = "agent_id")
	private Integer agent;
	@Column(name = "remarks", columnDefinition = "text")
	private String remarks;

	@Column(name = "division_id")
	private Integer division;
	@Column(name = "allowed_divisions")
	private BigInteger allowedDivisions;
	@Column(name = "write_allowed_divisions")
	private BigInteger writeAllowedDivisions;

	// Joined
	@Column(name = "client_nic")
	private String clientNic;
	@Column(name = "client_mobile_telephone")
	private String clientMobileTelephone;
	@Column(name = "client_name")
	private String clientName;
	@Column(name = "from_city")
	private String fromCity;
	@Column(name = "to_city")
	private String toCity;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Date getBookingTime() {
		return bookingTime;
	}

	public void setBookingTime(Date bookingTime) {
		this.bookingTime = bookingTime;
	}

	public Date getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(Date expiryTime) {
		this.expiryTime = expiryTime;
	}

	public double getChargeable() {
		return chargeable;
	}

	public void setChargeable(double chargeable) {
		this.chargeable = chargeable;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCancellationCause() {
		return cancellationCause;
	}

	public void setCancellationCause(String cancellationCause) {
		this.cancellationCause = cancellationCause;
	}

	public Integer getClient() {
		return client;
	}

	public void setClient(Integer client) {
		this.client = client;
	}

	public Integer getUser() {
		return user;
	}

	public void setUser(Integer user) {
		this.user = user;
	}

	public Integer getAgent() {
		return agent;
	}

	public void setAgent(Integer agent) {
		this.agent = agent;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getDivision() {
		return division;
	}

	public void setDivision(Integer division) {
		this.division = division;
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
	public BigInteger getWriteAllowedDivisions() {
		return writeAllowedDivisions;
	}

	@Override
	public void setWriteAllowedDivisions(BigInteger writeAllowedDivisions) {
		this.writeAllowedDivisions = writeAllowedDivisions;
	}

	public String getClientNic() {
		return clientNic;
	}

	public void setClientNic(String clientNic) {
		this.clientNic = clientNic;
	}

	public String getClientMobileTelephone() {
		return clientMobileTelephone;
	}

	public void setClientMobileTelephone(String clientMobileTelephone) {
		this.clientMobileTelephone = clientMobileTelephone;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getFromCity() {
		return fromCity;
	}

	public void setFromCity(String fromCity) {
		this.fromCity = fromCity;
	}

	public String getToCity() {
		return toCity;
	}

	public void setToCity(String toCity) {
		this.toCity = toCity;
	}
}
