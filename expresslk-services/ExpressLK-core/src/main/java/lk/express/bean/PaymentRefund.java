package lk.express.bean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity
@Table(name = "payment_refund")
@Inheritance(strategy = InheritanceType.JOINED)
@XmlType(name = "PaymentRefund", namespace = "http://bean.express.lk")
@XmlRootElement
@XmlSeeAlso({ Payment.class, Refund.class })
public class PaymentRefund extends lk.express.bean.Entity {

	private static final long serialVersionUID = 1L;

	@XmlType(name = "PaymentRefundType", namespace = "http://bean.express.lk")
	public enum PaymentRefundType {
		Payment, Refund
	}

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "amount")
	private double amount;
	@Column(name = "actual_amount")
	private double actualAmount;
	@Column(name = "actual_currency")
	private String actualCurrency;
	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	protected PaymentRefundType type;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date time;
	@Column(name = "mode")
	@Enumerated(EnumType.STRING)
	private PaymentRefundMode mode;
	@Column(name = "vendor_mode")
	@Enumerated(EnumType.STRING)
	private VendorPaymentRefundMode vendorMode;
	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "reference")
	private String reference;

	@Override
	@XmlElement(nillable = true)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@XmlElement(nillable = true)
	public double getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            amount in LKR
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * In LKR
	 * 
	 * @return
	 */
	@XmlElement(nillable = true)
	public double getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(double actualAmount) {
		this.actualAmount = actualAmount;
	}

	@XmlElement(nillable = true)
	public String getActualCurrency() {
		return actualCurrency;
	}

	public void setActualCurrency(String actualCurrency) {
		this.actualCurrency = actualCurrency;
	}

	@XmlElement(nillable = true)
	public PaymentRefundType getType() {
		return type;
	}

	public void setType(PaymentRefundType type) {
		this.type = type;
	}

	@XmlElement(nillable = true)
	public PaymentRefundMode getMode() {
		return mode;
	}

	public void setMode(PaymentRefundMode mode) {
		this.mode = mode;
	}

	@XmlElement(nillable = true)
	public VendorPaymentRefundMode getVendorMode() {
		return vendorMode;
	}

	public void setVendorMode(VendorPaymentRefundMode vendorMode) {
		this.vendorMode = vendorMode;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@XmlElement(nillable = true)
	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	@XmlElement(nillable = true)
	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
}
