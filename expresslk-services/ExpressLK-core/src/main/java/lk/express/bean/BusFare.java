package lk.express.bean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.supplier.Supplier;
import lk.express.supplier.SupplierGroup;

@Entity
@Table(name = "bus_fare")
@XmlType(name = "BusFare", namespace = "http://bean.express.lk")
@XmlRootElement
public class BusFare extends lk.express.bean.Entity implements HasValidPeriod {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "bus_sector_id")
	private BusSector busSector;
	@ManyToOne
	@JoinColumn(name = "travel_class_id")
	private TravelClass travelClass;
	@ManyToOne
	@JoinColumn(name = "supplier_group_id")
	private SupplierGroup group;
	@ManyToOne
	@JoinColumn(name = "supplier_id")
	private Supplier supplier;
	@ManyToOne
	@JoinColumn(name = "bus_id")
	private Bus bus;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_time")
	private Date startTime;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_time")
	private Date endTime;
	@Column(name = "currency_code")
	private String currencyCode;
	@Column(name = "infant_fare")
	private double infantFare;
	@Column(name = "child_fare")
	private double childFare;
	@Column(name = "adult_fare")
	private double adultFare;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Override
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public double getInfantFare() {
		return infantFare;
	}

	public void setInfantFare(double infantFare) {
		this.infantFare = infantFare;
	}

	public double getChildFare() {
		return childFare;
	}

	public void setChildFare(double childFare) {
		this.childFare = childFare;
	}

	public double getAdultFare() {
		return adultFare;
	}

	public void setAdultFare(double adultFare) {
		this.adultFare = adultFare;
	}

	public BusSector getBusSector() {
		return busSector;
	}

	public void setBusSector(BusSector busSector) {
		this.busSector = busSector;
	}

	public TravelClass getTravelClass() {
		return travelClass;
	}

	public void setTravelClass(TravelClass travelClass) {
		this.travelClass = travelClass;
	}

	public SupplierGroup getGroup() {
		return group;
	}

	public void setGroup(SupplierGroup group) {
		this.group = group;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Bus getBus() {
		return bus;
	}

	public void setBus(Bus bus) {
		this.bus = bus;
	}
}
