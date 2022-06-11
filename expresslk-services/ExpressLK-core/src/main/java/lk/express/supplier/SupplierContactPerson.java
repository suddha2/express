package lk.express.supplier;

import java.math.BigInteger;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.admin.Person;
import lk.express.bean.HasAllowedDivisions;
import lk.express.bean.HasPerson;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "supplier_contact_person")
@XmlType(name = "SupplierContactPerson", namespace = "http://supplier.express.lk")
@XmlRootElement
public class SupplierContactPerson extends lk.express.bean.Entity implements HasAllowedDivisions, HasPerson {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "person_id")
	private Person person;
	@Column(name = "supplier_id")
	private Integer supplierId;
	@Column(name = "is_primary", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean isPrimary;
	@Column(name = "is_owner", columnDefinition = "bit")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean isOwner;
	@Column(name = "allowed_divisions")
	private BigInteger allowedDivisions;

	public SupplierContactPerson() {
		person = new Person();
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	public boolean isOwner() {
		return isOwner;
	}

	public void setOwner(boolean isOwner) {
		this.isOwner = isOwner;
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
