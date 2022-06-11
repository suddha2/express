package lk.express.rule;

import lk.express.rule.bean.RuleCondition;
import lk.express.rule.bean.RuleConditionGroup;
import lk.express.rule.bean.RuleConditionSingle;

public class ConditionGroup<T> implements ICondition<T> {

	private ICondition<T> firstCondition;
	private ICondition<T> secondCondition;
	private Combiner combiner;

	public ConditionGroup(ICondition<T> firstCondition, ICondition<T> secondCondition, Combiner combiner) {
		this.firstCondition = firstCondition;
		this.secondCondition = secondCondition;
		this.combiner = combiner;
	}

	public ConditionGroup(RuleConditionGroup condition) {
		this.firstCondition = getCondition(condition.getFirstRule());
		this.secondCondition = getCondition(condition.getSecondRule());
		this.combiner = condition.getCombiner();
	}

	private ICondition<T> getCondition(RuleCondition condition) {
		ICondition<T> ret = null;
		if (condition instanceof RuleConditionSingle) {
			ret = new Condition<T>((RuleConditionSingle) condition);
		} else if (condition instanceof RuleConditionGroup) {
			ret = new ConditionGroup<T>((RuleConditionGroup) condition);
		}
		return ret;
	}

	@Override
	public boolean matches(T t) {
		return combiner.combine(firstCondition, secondCondition, t);
	}

	@Override
	public String toString() {
		return "(" + firstCondition + " " + combiner + " " + secondCondition + ")";
	}
}
