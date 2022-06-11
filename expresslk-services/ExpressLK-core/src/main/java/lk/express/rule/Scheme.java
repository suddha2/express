package lk.express.rule;

import java.util.Collections;
import java.util.List;

public class Scheme<T> {

	private List<? extends IRule<T>> rules;

	private boolean applySingle;
	private String name;

	public Scheme(String name, List<? extends IRule<T>> rules) {
		this.name = name;
		this.rules = rules;
	}

	public void apply(T t) {
		Collections.sort(rules, Collections.reverseOrder());
		for (IRule<T> rule : rules) {
			if (rule.matches(t)) {
				rule.apply(t);
				if (applySingle) {
					break;
				}
			}
		}
	}

	public void setApplySingle(boolean applySingle) {
		this.applySingle = applySingle;
	}

	public String getName() {
		return name;
	}

	public List<? extends IRule<T>> getRules() {
		return rules;
	}
}
