package lk.express;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "ICostPrice", namespace = "http://express.lk")
public interface ICostPrice extends IBaseCostPrice {

	/**
	 * Sets the price
	 * 
	 * @param price
	 *            price
	 */
	@Override
	@Deprecated
	public void setPrice(double price);

	/**
	 * Returns the price after applying all the markups
	 * 
	 * @return price after markup
	 */
	public abstract double getGrossPrice();

	/**
	 * Returns the price after applying all the discounts. This is also the
	 * price before taxes
	 * 
	 * @return price after discounts/price before taxes
	 */
	public abstract double getPriceBeforeTax();

	/**
	 * Returns the prices after applying taxes, before applying any charges such
	 * as agent commissions
	 * 
	 * @return prices after applying taxes
	 */
	public abstract double getPriceBeforeCharges();

	/**
	 * Returns a map of markup rule id and markup amount applied
	 * 
	 * @return applied markup map
	 */
	public abstract Map<Integer, Double> getMarkups();

	/**
	 * Apply a margin
	 * 
	 * @param ruleId
	 *            margin rule id
	 * @param margin
	 *            margin amount
	 */
	public void applyMargin(Integer ruleId, Double margin);

	/**
	 * Apply a markup
	 * 
	 * @param ruleId
	 *            markup rule id
	 * @param markup
	 *            markup amount
	 */
	public void applyMarkup(Integer ruleId, Double markup);

	/**
	 * Returns a map of discount rule id and discount amount applied
	 * 
	 * @return applied discounts map
	 */
	public abstract Map<Integer, Double> getDiscounts();

	/**
	 * Apply a discount
	 * 
	 * @param ruleId
	 *            discount rule id
	 * @param discount
	 *            discount amount
	 */
	public void applyDiscount(Integer ruleId, Double discount);

	/**
	 * Returns a map of tax rule id and tax amount applied
	 * 
	 * @return applied tax map
	 */
	public abstract Map<Integer, Double> getTaxes();

	/**
	 * Apply a tax
	 * 
	 * @param ruleId
	 *            tax rule id
	 * @param tax
	 *            tax amount
	 */
	public void applyTax(Integer ruleId, Double tax);

	/**
	 * Returns a map of charge id and charge amount
	 * 
	 * @return applied tax map
	 */
	public abstract Map<Integer, Double> getCharges();

	/**
	 * Apply charges such as agent commission
	 * 
	 * @param ruleId
	 *            charge rule id
	 * @param charge
	 *            charge amount
	 */
	public void applyCharge(Integer ruleId, Double charge);
}