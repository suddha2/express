package lk.express.supplier;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.accounting.IJournalEntry;
import lk.express.bean.HasAllowedDivisions;
import lk.express.bean.PaymentRefundMode;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "supplier")
@XmlType(name = "Supplier", namespace = "http://supplier.express.lk")
@XmlRootElement
public class Supplier extends lk.express.bean.Entity implements HasAllowedDivisions {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "name")
	private String name;
	@ManyToOne
	@JoinColumn(name = "supplier_group_id")
	private SupplierGroup group;
	@Column(name = "preferred_payment_mode")
	@Enumerated(EnumType.STRING)
	private PaymentRefundMode preferredPaymentMode;
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "supplierId", cascade = CascadeType.ALL)
	private List<SupplierContactPerson> contactPersonnel;
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "supplierId", cascade = CascadeType.ALL)
	private List<SupplierAccount> accounts;

	@Column(name = "allowed_divisions")
	private BigInteger allowedDivisions;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SupplierGroup getGroup() {
		return group;
	}

	public void setGroup(SupplierGroup group) {
		this.group = group;
	}

	public PaymentRefundMode getPreferredPaymentMode() {
		return preferredPaymentMode;
	}

	public void setPreferredPaymentMode(PaymentRefundMode preferredPaymentMode) {
		this.preferredPaymentMode = preferredPaymentMode;
	}

	public List<SupplierContactPerson> getContactPersonnel() {
		return contactPersonnel;
	}

	public void setContactPersonnel(List<SupplierContactPerson> contactPersonnel) {
		this.contactPersonnel = contactPersonnel;
	}

	public List<SupplierAccount> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<SupplierAccount> accounts) {
		this.accounts = accounts;
	}

	@Override
	public BigInteger getAllowedDivisions() {
		return allowedDivisions;
	}

	@Override
	public void setAllowedDivisions(BigInteger allowedDivisions) {
		this.allowedDivisions = allowedDivisions;
	}

	@JsonIgnore
	public String getAccountName() {
		return IJournalEntry.SUPPLIER_PREFIX + id;
	}
}
