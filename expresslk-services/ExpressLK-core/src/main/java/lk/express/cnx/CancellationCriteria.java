package lk.express.cnx;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "CancellationCriteria", namespace = "http://cnx.express.lk")
public class CancellationCriteria {

	private int bookingId;
	private List<ItemCancellationCriteria> itemCriteria;
	private boolean chargeCancellationFee = true;
	private CancellationCause cause;

	private String remark;

	public int getBookingId() {
		return bookingId;
	}

	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}

	public List<ItemCancellationCriteria> getItemCriteria() {
		return itemCriteria;
	}

	public void setItemCriteria(List<ItemCancellationCriteria> itemCriteria) {
		this.itemCriteria = itemCriteria;
	}

	public boolean isChargeCancellationFee() {
		return chargeCancellationFee;
	}

	public void setChargeCancellationFee(boolean chargeCancellationFee) {
		this.chargeCancellationFee = chargeCancellationFee;
	}

	public CancellationCause getCause() {
		return cause;
	}

	public void setCause(CancellationCause cause) {
		this.cause = cause;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
