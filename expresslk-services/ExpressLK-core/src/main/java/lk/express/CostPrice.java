package lk.express;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import lk.express.util.CurrencyUtil;

@XmlRootElement
@XmlType(name = "CostPrice", namespace = "http://express.lk")
@XmlSeeAlso({ ResultSector.class })
public class CostPrice extends BaseCostPrice implements ICostPrice {

	private static final long serialVersionUID = 1L;

	private enum State {
		New, CostAdded, MarginApplied, MarkupApplied, DiscountApplied, TaxApplied, ChargesApplied;
	}

	private State state = State.New;
	private Double grossPrice;
	private Double priceBeforeTax;
	private Double priceBeforeCharges;
	private Map<Integer, Double> markups = new HashMap<Integer, Double>();
	private Map<Integer, Double> discounts = new HashMap<Integer, Double>();
	private Map<Integer, Double> taxes = new HashMap<Integer, Double>();
	private Map<Integer, Double> charges = new HashMap<Integer, Double>();

	private Map<Integer, Boolean> isMargin = new HashMap<Integer, Boolean>();

	@Override
	public double getGrossPrice() {
		return grossPrice;
	}

	@Deprecated
	public void setGrossPrice(double grossPrice) {
		this.grossPrice = grossPrice;
	}

	@Override
	public double getPriceBeforeTax() {
		return priceBeforeTax;
	}

	@Deprecated
	public void setPriceBeforeTax(double priceBeforeTax) {
		this.priceBeforeTax = priceBeforeTax;
	}

	@Override
	public double getPriceBeforeCharges() {
		return priceBeforeCharges;
	}

	@Deprecated
	public void setPriceBeforeCharges(double priceBeforeCharges) {
		this.priceBeforeCharges = priceBeforeCharges;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.ICostPrice#getMarkups()
	 */
	@Override
	public Map<Integer, Double> getMarkups() {
		return markups;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.ICostPrice#getDiscounts()
	 */
	@Override
	public Map<Integer, Double> getDiscounts() {
		return discounts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.ICostPrice#getTaxes()
	 */
	@Override
	public Map<Integer, Double> getTaxes() {
		return taxes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.ICostPrice#getCharges()
	 */
	@Override
	public Map<Integer, Double> getCharges() {
		return charges;
	}

	public void setMarkups(Map<Integer, Double> markups) {
		this.markups = markups;
	}

	public void setDiscounts(Map<Integer, Double> discounts) {
		this.discounts = discounts;
	}

	public void setTaxes(Map<Integer, Double> taxes) {
		this.taxes = taxes;
	}

	public void setCharges(Map<Integer, Double> charges) {
		this.charges = charges;
	}

	public Map<Integer, Boolean> getIsMargin() {
		return isMargin;
	}

	@Override
	public void setCost(double cost) {
		if (state.ordinal() > State.CostAdded.ordinal()) {
			throw new IllegalStateException("Margin already applied!");
		}
		this.cost = cost;
		grossPrice = priceBeforeTax = priceBeforeCharges = price = this.cost;
		state = State.CostAdded;
	}

	@Override
	public void setChildCost(double cost) {
		if (state.ordinal() > State.CostAdded.ordinal()) {
			throw new IllegalStateException("Margin already applied!");
		}
		this.childCost  = this.childPrice = cost;
		//grossPrice = priceBeforeTax = priceBeforeCharges = price = this.cost;
		//state = State.CostAdded;
	}
	
	@Override
	public void applyMargin(Integer ruleId, Double margin) {
		if (state.ordinal() > State.MarginApplied.ordinal()) {
			throw new IllegalStateException("Markup already applied!");
		}
		margin = CurrencyUtil.round(margin);
		markups.put(ruleId, margin);
		isMargin.put(ruleId, true);
		cost = CurrencyUtil.round(cost - margin);
		// Compute child fare
		childCost = CurrencyUtil.round(childCost - margin);
		state = State.MarginApplied;
	}

	@Override
	public void applyMarkup(Integer ruleId, Double markup) {
		if (state.ordinal() > State.MarkupApplied.ordinal()) {
			throw new IllegalStateException("Discount already applied!");
		}
		markup = CurrencyUtil.round(markup);
		markups.put(ruleId, markup);
		isMargin.put(ruleId, false);
		grossPrice = CurrencyUtil.round(grossPrice + markup);
		
		// Compute child fare
		childPrice = CurrencyUtil.round(this.childCost + markup);
		priceBeforeTax = priceBeforeCharges = price = grossPrice;
		
		
		
		state = State.MarkupApplied;
	}

	@Override
	public void applyDiscount(Integer ruleId, Double discount) {
		if (state.ordinal() > State.DiscountApplied.ordinal()) {
			throw new IllegalStateException("Tax already applied!");
		}
		discount = CurrencyUtil.round(discount);
		discounts.put(ruleId, discount);
		priceBeforeTax = CurrencyUtil.round(priceBeforeTax - discount);
		priceBeforeCharges = price = priceBeforeTax;
		state = State.DiscountApplied;
	}

	@Override
	public void applyTax(Integer ruleId, Double tax) {
		if (state.ordinal() > State.ChargesApplied.ordinal()) {
			throw new IllegalStateException("Charges already applied!");
		}
		tax = CurrencyUtil.round(tax);
		taxes.put(ruleId, tax);
		priceBeforeCharges = CurrencyUtil.round(priceBeforeCharges + tax);
		price = priceBeforeCharges;
		state = State.TaxApplied;
	}

	@Override
	public void applyCharge(Integer ruleId, Double charge) {
		charge = CurrencyUtil.round(charge);
		charges.put(ruleId, charge);
		price = CurrencyUtil.round(price + charge);
		state = State.ChargesApplied;
	}
}
