package lk.express;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "IBaseCostPrice", namespace = "http://express.lk")
public interface IBaseCostPrice {

	/**
	 * Returns the fare
	 * 
	 * @return fare
	 */
	public abstract double getFare();

	/**
	 * Sets the fare
	 * 
	 * @param fare
	 *            fare
	 */
	public void setFare(double fare);

	/**
	 * Returns the cost/base price
	 * 
	 * @return cost
	 */
	public abstract double getCost();

	/**
	 * Sets the cost/base price
	 * 
	 * @param cost
	 *            cost
	 */
	public void setCost(double cost);

	/**
	 * Returns the price
	 * 
	 * @return final price
	 */
	public abstract double getPrice();

	/**
	 * Sets the price
	 * 
	 * @param price
	 *            price
	 */
	public void setPrice(double price);

	/**
	 * Returns Child Fare
	 * 
	 * @return
	 */
	public abstract double getChildFare();

	public abstract double getChildCost();

	public abstract double getChildPrice();

	public void setChildCost(double price);

	public void setChildFare(double price);

	public void setChildPrice(double price);

}
