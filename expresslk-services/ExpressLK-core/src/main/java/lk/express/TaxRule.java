package lk.express;

import lk.express.rule.ApplicationType;
import lk.express.rule.ICondition;
import lk.express.rule.bean.Rule;

public class TaxRule extends DiscountMarkupTaxRule {

	public TaxRule(int id, String rule, ICondition<ResultWrapper> condition, float salience, String scheme) {
		super(id, rule, condition, salience, scheme);
	}

	public TaxRule(Rule ruleBean) {
		super(ruleBean);
	}

	@Override
	public void apply(ResultWrapper wrapper) {
		double pbt = wrapper.getPriceBeforeTax();
		double tax = 0d;
		if (applicationType == ApplicationType.Absolute) {
			tax = amount;
		} else {
			tax = pbt * amount;
		}
		wrapper.applyTax(id, tax);
	}

	@Override
	public String toString() {
		return "if " + condition + ", then apply tax of "
				+ (applicationType == ApplicationType.Percentage ? amount + "%" : amount) + ")";
	}
}
