package lk.express;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "BaseCostPrice", namespace = "http://express.lk")
@XmlSeeAlso({ CostPrice.class, Result.class })
public class BaseCostPrice implements IBaseCostPrice, Serializable {

	private static final long serialVersionUID = 1L;

	protected Double fare;
	protected Double cost;
	protected Double price;

	// Child Fare variables
	protected Double childFare;
	protected Double childCost;
	protected Double childPrice;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.ICostPrice#getFare()
	 */
	@Override
	@XmlElement(nillable = true)
	public double getChildFare() {
		return childFare;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.ICostPrice#getFare()
	 */
	@Override
	@XmlElement(nillable = true)
	public double getChildCost() {
		return childCost;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.ICostPrice#getFare()
	 */
	@Override
	@XmlElement(nillable = true)
	public double getChildPrice() {
		return childPrice;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.ICostPrice#getFare()
	 */
	@Override
	@XmlElement(nillable = true)
	public double getFare() {
		return fare;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.ICostPrice#setFare()
	 */
	@Override
	public void setFare(double fare) {
		this.fare = fare;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.ICostPrice#getCost()
	 */
	@Override
	@XmlElement(nillable = true)
	public double getCost() {
		return cost;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.ICostPrice#setCost()
	 */
	@Override
	public void setCost(double cost) {
		this.cost = cost;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.ICostPrice#getPrice()
	 */
	@Override
	@XmlElement(nillable = true)
	public double getPrice() {
		return price;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.ICostPrice#setPrice()
	 */
	@Override
	public void setPrice(double price) {
		this.price = price;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.ICostPrice#setCost()
	 */
	@Override
	public void setChildFare(double fare) {
		this.childFare = fare;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.ICostPrice#setCost()
	 */
	@Override
	public void setChildPrice(double price) {
		this.childPrice = price;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.ICostPrice#setCost()
	 */
	@Override
	public void setChildCost(double cost) {
		this.childCost = cost;
	}
}
