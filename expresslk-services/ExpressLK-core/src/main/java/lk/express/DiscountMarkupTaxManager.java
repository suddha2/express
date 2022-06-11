package lk.express;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lk.express.rule.IRule;
import lk.express.rule.Scheme;
import lk.express.rule.bean.RuleChargeRule;
import lk.express.rule.bean.RuleDiscountRule;
import lk.express.rule.bean.RuleMarkupRule;
import lk.express.rule.bean.RuleTaxRule;

public class DiscountMarkupTaxManager {

	protected List<Scheme<ResultWrapper>> schemes;

	public DiscountMarkupTaxManager(List<? extends lk.express.rule.bean.Rule> rules) {
		this.schemes = getRules(rules);
	}

	protected List<Scheme<ResultWrapper>> getRules(List<? extends lk.express.rule.bean.Rule> rules) {
		Map<String, List<IRule<ResultWrapper>>> iRulesMap = new HashMap<String, List<IRule<ResultWrapper>>>();
		for (lk.express.rule.bean.Rule rule : rules) {
			IRule<ResultWrapper> iRule = null;
			if (rule instanceof RuleDiscountRule) {
				iRule = new DiscountRule(rule);
			} else if (rule instanceof RuleMarkupRule) {
				iRule = new MarkupRule((RuleMarkupRule) rule);
			} else if (rule instanceof RuleTaxRule) {
				iRule = new TaxRule(rule);
			} else if (rule instanceof RuleChargeRule) {
				iRule = new ChargeRule(rule);
			}
			List<IRule<ResultWrapper>> iRules = iRulesMap.get(iRule.getScheme());
			if (iRules == null) {
				iRules = new ArrayList<IRule<ResultWrapper>>();
				iRulesMap.put(iRule.getScheme(), iRules);
			}
			iRules.add(iRule);
		}

		List<Scheme<ResultWrapper>> schemes = new ArrayList<Scheme<ResultWrapper>>();
		for (Entry<String, List<IRule<ResultWrapper>>> e : iRulesMap.entrySet()) {
			Scheme<ResultWrapper> scheme = new Scheme<ResultWrapper>(e.getKey(), e.getValue());
			scheme.setApplySingle(true);
			schemes.add(scheme);
		}
		return schemes;
	}

	public void apply(ResultWrapper wrapper) {
		for (Scheme<ResultWrapper> scheme : schemes) {
			scheme.apply(wrapper);
		}
	}
}
