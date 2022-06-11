package lk.express.rule;

import lk.express.rule.bean.RuleConditionGroup;
import lk.express.rule.bean.RuleConditionSingle;

public abstract class Rule<T> implements IRule<T> {

	protected int id;
	protected String rule;
	protected float salience;
	protected String scheme;
	protected ICondition<T> condition;

	public Rule(int id, String rule, ICondition<T> condition, float salience, String scheme) {
		this.id = id;
		this.rule = rule;
		this.salience = salience;
		this.condition = condition;
		this.scheme = scheme;
	}

	public Rule(lk.express.rule.bean.Rule ruleBean) {
		this.id = ruleBean.getId();
		this.rule = ruleBean.getName();
		this.salience = ruleBean.getSalience();
		this.scheme = ruleBean.getScheme();
		if (ruleBean.getCondition() instanceof RuleConditionSingle) {
			condition = new Condition<T>((RuleConditionSingle) ruleBean.getCondition());
		} else if (ruleBean.getCondition() instanceof RuleConditionGroup) {
			condition = new ConditionGroup<T>((RuleConditionGroup) ruleBean.getCondition());
		}
	}

	@Override
	public boolean matches(T t) {
		return condition == null || condition.matches(t);
	}

	@Override
	public abstract void apply(T t);

	@Override
	public float getSalience() {
		return salience;
	}

	public void setSalience(float salience) {
		this.salience = salience;
	}

	@Override
	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	@Override
	public int compareTo(IRule<T> o) {
		return ((Float) salience).compareTo(o.getSalience());
	}
}
