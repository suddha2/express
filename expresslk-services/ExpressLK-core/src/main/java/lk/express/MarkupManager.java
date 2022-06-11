package lk.express;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lk.express.rule.Scheme;
import lk.express.rule.bean.RuleMarkupRule;

public class MarkupManager extends DiscountMarkupTaxManager {

	public MarkupManager(List<RuleMarkupRule> rules) {
		super(rules);
	}

	@Override
	protected List<Scheme<ResultWrapper>> getRules(List<? extends lk.express.rule.bean.Rule> rules) {
		Map<String, List<MarkupRule>> iRulesMap = new HashMap<String, List<MarkupRule>>();
		for (lk.express.rule.bean.Rule rule : rules) {
			MarkupRule iRule = new MarkupRule((RuleMarkupRule) rule);
			List<MarkupRule> iRules = iRulesMap.get(iRule.getScheme());
			if (iRules == null) {
				iRules = new ArrayList<MarkupRule>();
				iRulesMap.put(iRule.getScheme(), iRules);
			}
			if (iRules.size() > 0) {
				MarkupRule jRule = iRules.get(0);
				if (jRule.isMargin() != iRule.isMargin()) {
					throw new RuntimeException("Both markup and margin rules cannot be in the same schema");
				}
			}
			iRules.add(iRule);
		}

		List<Scheme<ResultWrapper>> schemes = new ArrayList<Scheme<ResultWrapper>>();
		for (Entry<String, List<MarkupRule>> e : iRulesMap.entrySet()) {
			Scheme<ResultWrapper> scheme = new Scheme<ResultWrapper>(e.getKey(), e.getValue());
			scheme.setApplySingle(true);
			schemes.add(scheme);
		}

		schemes.sort(new Comparator<Scheme<ResultWrapper>>() {
			@Override
			public int compare(Scheme<ResultWrapper> o1, Scheme<ResultWrapper> o2) {
				MarkupRule r1 = (MarkupRule) o1.getRules().get(0);
				MarkupRule r2 = (MarkupRule) o2.getRules().get(0);
				if (r1.isMargin() && !r2.isMargin()) {
					return -1;
				} else if (r2.isMargin() && !r1.isMargin()) {
					return 1;
				}
				return 0;
			}
		});

		return schemes;
	}

}
