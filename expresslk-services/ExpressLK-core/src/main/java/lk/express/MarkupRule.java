package lk.express;

import lk.express.rule.ApplicationType;
import lk.express.rule.ICondition;
import lk.express.rule.bean.RuleMarkupRule;

public class MarkupRule extends DiscountMarkupTaxRule {

	private boolean isMargin;

	public MarkupRule(int id, String rule, ICondition<ResultWrapper> condition, float salience, String scheme) {
		super(id, rule, condition, salience, scheme);
	}

	public MarkupRule(RuleMarkupRule ruleBean) {
		super(ruleBean);
		this.isMargin = ruleBean.isMargin();
	}

	public boolean isMargin() {
		return isMargin;
	}

	@Override
	public void apply(ResultWrapper wrapper) {
		//TODO : Change here to apply markup rules for child fare
		
		double fare = wrapper.getFare();
		double value = 0d;
		if (applicationType == ApplicationType.Absolute) {
			value = amount;
		} else {
			value = fare * amount;
		}

		if (isMargin) {
			wrapper.applyMargin(id, value);
		} else {
			wrapper.applyMarkup(id, value);
		}
	}

	@Override
	public String toString() {
		return "if " + condition + ", then apply " + (isMargin ? "margin" : "markup") + " of "
				+ (applicationType == ApplicationType.Percentage ? amount + "%" : amount) + ")";
	}
}
