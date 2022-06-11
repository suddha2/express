package lk.express.bean;

import java.util.Calendar;
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

import lk.express.db.dao.DAOFactory;
import lk.express.db.dao.GenericDAO;
import lk.express.util.Util;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "coupon")
@XmlType(name = "Coupon", namespace = "http://bean.express.lk")
@XmlRootElement
public class Coupon extends lk.express.bean.Entity {

	protected static final DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "number")
	private String number;
	@Column(name = "amount")
	private Double amount;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "issue_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date issueTime;
	@Column(name = "active", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean active = true;
	@Temporal(TemporalType.DATE)
	@Column(name = "expiry_date")
	private Date expiryDate;
	@Column(name = "client_id")
	private Integer clientId;

	public Coupon() {
	}

	public Coupon(String number) {
		this.number = number;
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Date getIssueTime() {
		return issueTime;
	}

	public void setIssueTime(Date issueTime) {
		this.issueTime = issueTime;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	/**
	 * Returns whether the coupon is active and not expired
	 * 
	 * @return
	 */
	@JsonIgnore
	public boolean isValid() {
		return active && (expiryDate == null || !expiryDate.before(new Date()));
	}

	@JsonIgnore
	public static Coupon createCoupon(double amount, Integer clientId) {
		Coupon coupon = new Coupon();
		coupon.number = Util.generateUniqueReference(Coupon.class, "number");
		coupon.issueTime = new Date();
		coupon.amount = amount;
		coupon.clientId = clientId;

		Calendar expiry = Calendar.getInstance();
		expiry.add(Calendar.YEAR, 1);
		coupon.expiryDate = expiry.getTime();

		GenericDAO<Coupon> couponDAO = daoFac.getCouponDAO();
		return couponDAO.persist(coupon);
	}
}
