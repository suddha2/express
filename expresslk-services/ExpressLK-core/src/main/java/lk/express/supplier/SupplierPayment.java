package lk.express.supplier;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.PaymentRefundMode;

@Entity
@Table(name = "supplier_payment")
@XmlType(name = "SupplierPayment", namespace = "http://supplier.express.lk")
@XmlRootElement
public class SupplierPayment extends lk.express.bean.Entity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "supplier_id")
	private Supplier supplier;
	@Column(name = "amount")
	private double amount;
	@Column(name = "currency")
	private String currency;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date time;
	@Column(name = "mode")
	@Enumerated(EnumType.STRING)
	private PaymentRefundMode mode;
	@Column(name = "reference")
	private String reference;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
}
