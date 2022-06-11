package lk.express.bean;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "refund")
@PrimaryKeyJoinColumn(name = "id")
@XmlType(name = "Refund", namespace = "http://bean.express.lk")
@XmlRootElement
public class Refund extends PaymentRefund implements HasAllowedDivisions {

	private static final long serialVersionUID = 1L;

	@Column(name = "booking_id")
	private Integer bookingId;

	@Column(name = "allowed_divisions")
	private BigInteger allowedDivisions;
	@Column(name = "write_allowed_divisions")
	private BigInteger writeAllowedDivisions;

	public Refund() {
		type = PaymentRefundType.Refund;
	}

	public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
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
}
