package lk.express.bean;

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
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "refund_request")
@XmlType(name = "RefundRequest", namespace = "http://bean.express.lk")
@XmlRootElement
public class RefundRequest extends lk.express.bean.Entity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "booking_id")
	private Integer bookingId;
	@Column(name = "mode_to_refund")
	@Enumerated(EnumType.STRING)
	private PaymentRefundMode modeToRefund;
	@Column(name = "amount_to_refund")
	private double amountToRefund;
	@Column(name = "payment_mode")
	@Enumerated(EnumType.STRING)
	private PaymentRefundMode paymentMode;
	@Column(name = "payment_reference")
	private String paymentReference;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date timestamp;
	@Column(name = "refunded", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean refunded;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}

	public PaymentRefundMode getModeToRefund() {
		return modeToRefund;
	}

	public void setModeToRefund(PaymentRefundMode modeToRefund) {
		this.modeToRefund = modeToRefund;
	}

	public double getAmountToRefund() {
		return amountToRefund;
	}

	public void setAmountToRefund(double amountToRefund) {
		this.amountToRefund = amountToRefund;
	}

	public PaymentRefundMode getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(PaymentRefundMode paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getPaymentReference() {
		return paymentReference;
	}

	public void setPaymentReference(String paymentReference) {
		this.paymentReference = paymentReference;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Boolean getRefunded() {
		return refunded;
	}

	public void setRefunded(Boolean refunded) {
		this.refunded = refunded;
	}
}
