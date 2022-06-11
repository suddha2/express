package lk.express;

import java.util.Date;

import javax.xml.bind.annotation.XmlType;

/**
 * Temporary data structure to load seat reservation data from bookingItems and
 * heldItems.
 */
@XmlType(name = "Reservation", namespace = "http://express.lk")
public class Reservation {

	private boolean isDummy = false;
	// booking item
	private int scheduleId;
	private int fromBusStopId;
	private int toBusStopId;
	// booking item passenger
	private String seatNumber;
	private Boolean journeyPerformed;
	private String name;
	private String nic;
	private String gender;
	private String mobile;
	private String email;
	private String passengerType;
	// booking
	private Integer bookingId;
	private String bookingReference;
	private String bookingRemarks;
	private Integer bookingUserId;
	private Date bookingTime;
	private String bookingStatusCode;
	private Integer bookingAgentId;
	private Double bookingChargeable;
	private Double bookingPayments;
	private String bookingPaymentModes;
	private Double bookingRefunds;

	public boolean isDummy() {
		return isDummy;
	}

	public void setDummy(boolean isDummy) {
		this.isDummy = isDummy;
	}

	public int getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
	}

	public int getFromBusStopId() {
		return fromBusStopId;
	}

	public void setFromBusStopId(int fromBusStopId) {
		this.fromBusStopId = fromBusStopId;
	}

	public int getToBusStopId() {
		return toBusStopId;
	}

	public void setToBusStopId(int toBusStopId) {
		this.toBusStopId = toBusStopId;
	}

	public String getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}

	public Boolean getJourneyPerformed() {
		return journeyPerformed;
	}

	public void setJourneyPerformed(Boolean journeyPerformed) {
		this.journeyPerformed = journeyPerformed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNic() {
		return nic;
	}

	public void setNic(String nic) {
		this.nic = nic;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}

	public String getBookingReference() {
		return bookingReference;
	}

	public void setBookingReference(String bookingReference) {
		this.bookingReference = bookingReference;
	}

	public String getBookingRemarks() {
		return bookingRemarks;
	}

	public void setBookingRemarks(String bookingRemarks) {
		this.bookingRemarks = bookingRemarks;
	}

	public Integer getBookingUserId() {
		return bookingUserId;
	}

	public void setBookingUserId(Integer bookingUserId) {
		this.bookingUserId = bookingUserId;
	}

	public Date getBookingTime() {
		return bookingTime;
	}

	public void setBookingTime(Date bookingTime) {
		this.bookingTime = bookingTime;
	}

	public String getBookingStatusCode() {
		return bookingStatusCode;
	}

	public void setBookingStatusCode(String bookingStatusCode) {
		this.bookingStatusCode = bookingStatusCode;
	}

	public Integer getBookingAgentId() {
		return bookingAgentId;
	}

	public void setBookingAgentId(Integer bookingAgentId) {
		this.bookingAgentId = bookingAgentId;
	}

	public Double getBookingChargeable() {
		return bookingChargeable;
	}

	public void setBookingChargeable(Double bookingChargeable) {
		this.bookingChargeable = bookingChargeable;
	}

	public Double getBookingPayments() {
		return bookingPayments;
	}

	public void setBookingPayments(Double bookingPayments) {
		this.bookingPayments = bookingPayments;
	}

	public String getBookingPaymentModes() {
		return bookingPaymentModes;
	}

	public void setBookingPaymentModes(String bookingPaymentModes) {
		this.bookingPaymentModes = bookingPaymentModes;
	}

	public Double getBookingRefunds() {
		return bookingRefunds;
	}

	public void setBookingRefunds(Double bookingRefunds) {
		this.bookingRefunds = bookingRefunds;
	}

	public String getPassengerType() {
		return passengerType;
	}

	public void setPassengerType(String passengerType) {
		this.passengerType = passengerType;
	}
}
