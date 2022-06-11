package lk.express.supplier;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.bean.HasAllowedDivisions;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "supplier_account")
@XmlType(name = "SupplierAccount", namespace = "http://supplier.express.lk")
@XmlRootElement
public class SupplierAccount extends lk.express.bean.Entity implements HasAllowedDivisions {

	private static final long serialVersionUID = 1L;

	@XmlType(name = "AccountType", namespace = "http://supplier.express.lk")
	public enum AccountType {
		Savings, Current;
	}

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "supplier_id")
	private Integer supplierId;
	@Column(name = "number")
	private String number;
	@Column(name = "name")
	private String name;
	@Column(name = "bank")
	private String bank;
	@Column(name = "branch")
	private String branch;
	@Column(name = "swift")
	private String swift;
	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private AccountType type;
	@Column(name = "is_primary", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean isPrimary;

	@Column(name = "allowed_divisions")
	private BigInteger allowedDivisions;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getSwift() {
		return swift;
	}

	public void setSwift(String swift) {
		this.swift = swift;
	}

	public AccountType getType() {
		return type;
	}

	public void setType(AccountType type) {
		this.type = type;
	}

	public Boolean getIsPrimary() {
		return isPrimary;
	}

	public void setIsPrimary(Boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	@Override
	public BigInteger getAllowedDivisions() {
		return allowedDivisions;
	}

	@Override
	public void setAllowedDivisions(BigInteger allowedDivisions) {
		this.allowedDivisions = allowedDivisions;
	}
}
