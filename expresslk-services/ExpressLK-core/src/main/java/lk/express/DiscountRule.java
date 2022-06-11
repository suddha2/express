package lk.express;

import lk.express.rule.ApplicationType;
import lk.express.rule.ICondition;
import lk.express.rule.bean.Rule;

public class DiscountRule extends DiscountMarkupTaxRule {

	public DiscountRule(int id, String rule, ICondition<ResultWrapper> condition, float salience, String scheme) {
		super(id, rule, condition, salience, scheme);
	}

	public DiscountRule(Rule ruleBean) {
		super(ruleBean);
	}

	@Override
	public void apply(ResultWrapper wrapper) {
		double gross = wrapper.getGrossPrice();
		double discount = 0d;
		if (applicationType == ApplicationType.Absolute) {
			discount = amount;
		} else {
			discount = gross * amount;
		}
		wrapper.applyDiscount(id, discount);
	}

	@Override
	public String toString() {
		return "if " + condition + ", then apply discount of "
				+ (applicationType == ApplicationType.Percentage ? amount + "%" : amount) + ")";
	}
}
