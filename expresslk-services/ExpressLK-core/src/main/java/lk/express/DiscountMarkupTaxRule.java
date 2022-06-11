package lk.express;

import lk.express.rule.ApplicationType;
import lk.express.rule.ICondition;
import lk.express.rule.Rule;

public abstract class DiscountMarkupTaxRule extends Rule<ResultWrapper> {

	protected ApplicationType applicationType = ApplicationType.Percentage;
	protected float amount;

	public DiscountMarkupTaxRule(int id, String rule, ICondition<ResultWrapper> condition, float salience, String scheme) {
		super(id, rule, condition, salience, scheme);
	}

	public DiscountMarkupTaxRule(lk.express.rule.bean.Rule ruleBean) {
		super(ruleBean);
		this.amount = ruleBean.getAmount();
		this.applicationType = ruleBean.getApplicationType();
	}

	/**
	 * Returns the absolute discount amount in the case of an absolute discount
	 * scheme or discount percentage in the case of a percentage based discount
	 * scheme.<br>
	 * Returned value is 0.1 in the case of a 10% discount
	 */
	public float getAmount() {
		return amount;
	}

	/**
	 * Sets the absolute discount amount in the case of an absolute discount
	 * scheme or discount percentage in the case of a percentage based discount
	 * scheme.<br>
	 * Value should be 0.1 in the case of a 10% discount
	 * 
	 * @param amount
	 */
	public void setAmount(float amount) {
		this.amount = amount;
	}

	public ApplicationType getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(ApplicationType applicationType) {
		this.applicationType = applicationType;
	}
}