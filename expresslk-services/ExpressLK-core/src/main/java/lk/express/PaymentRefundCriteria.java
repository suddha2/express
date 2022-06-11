package lk.express;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.PaymentRefund.PaymentRefundType;
import lk.express.bean.PaymentRefundMode;
import lk.express.bean.VendorPaymentRefundMode;

@XmlRootElement
@XmlType(name = "PaymentRefundCriteria", namespace = "http://express.lk")
@XmlSeeAlso({ PaymentCriteria.class, RefundCriteria.class })
public class PaymentRefundCriteria {

	private int bookingId;
	private PaymentRefundMode mode;
	private VendorPaymentRefundMode vendorMode;
	protected PaymentRefundType type;
	private String reference;

	private double amount;
	private double actualAmount;
	private String actualCurrency;

	public int getBookingId() {
		return bookingId;
	}

	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}

	public PaymentRefundMode getMode() {
		return mode;
	}

	public void setMode(PaymentRefundMode mode) {
		this.mode = mode;
	}

	public VendorPaymentRefundMode getVendorMode() {
		return vendorMode;
	}

	public void setVendorMode(VendorPaymentRefundMode vendorMode) {
		this.vendorMode = vendorMode;
	}

	public PaymentRefundType getType() {
		return type;
	}

	public void setType(PaymentRefundType type) {
		this.type = type;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(double actualAmount) {
		this.actualAmount = actualAmount;
	}

	public String getActualCurrency() {
		return actualCurrency;
	}

	public void setActualCurrency(String actualCurrency) {
		this.actualCurrency = actualCurrency;
	}
}
