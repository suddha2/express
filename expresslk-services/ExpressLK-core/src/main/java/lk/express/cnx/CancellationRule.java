package lk.express.cnx;

import lk.express.rule.ApplicationType;
import lk.express.rule.ICondition;
import lk.express.rule.Rule;
import lk.express.rule.bean.RuleCancellationRule;

public class CancellationRule extends Rule<BookingCancellationWrapper> {

	protected boolean allowed;
	protected ApplicationType applicationType = ApplicationType.Percentage;
	protected float amount;

	public CancellationRule(int id, String rule, ICondition<BookingCancellationWrapper> condition, float salience,
			String scheme) {
		super(id, rule, condition, salience, scheme);
	}

	public CancellationRule(RuleCancellationRule ruleBean) {
		super(ruleBean);
		this.amount = ruleBean.getAmount();
		this.applicationType = ruleBean.getApplicationType();
	}

	/**
	 * Returns the absolute discount amount in the case of an absolute discount
	 * scheme or discount percentage in the case of a percentage based discount
	 * scheme.<br>
	 * Returned value is 0.1 in the case of a 10% discount
	 * 
	 * @return amount
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

	public boolean isAllowed() {
		return allowed;
	}

	public void setAllowed(boolean allowed) {
		this.allowed = allowed;
	}

	@Override
	public void apply(BookingCancellationWrapper wrapper) {
		double chargeable = wrapper.getChargeable();
		double charge = 0d;
		if (applicationType == ApplicationType.Absolute) {
			charge = amount;
		} else {
			charge = chargeable * amount;
		}
		wrapper.applyCharge(id, charge);
	}
}
