package lk.express;

import lk.express.rule.ApplicationType;
import lk.express.rule.ICondition;
import lk.express.rule.bean.Rule;

public class ChargeRule extends DiscountMarkupTaxRule {

	public ChargeRule(int id, String rule, ICondition<ResultWrapper> condition, float salience, String scheme) {
		super(id, rule, condition, salience, scheme);
	}

	public ChargeRule(Rule ruleBean) {
		super(ruleBean);
	}

	@Override
	public void apply(ResultWrapper wrapper) {
		double pbc = wrapper.getPriceBeforeCharges();
		double charge = 0d;
		if (applicationType == ApplicationType.Absolute) {
			charge = amount;
		} else {
			charge = pbc * amount;
		}
		wrapper.applyCharge(id, charge);
	}

	@Override
	public String toString() {
		return "if " + condition + ", then apply charge of "
				+ (applicationType == ApplicationType.Percentage ? amount + "%" : amount) + ")";
	}

}
