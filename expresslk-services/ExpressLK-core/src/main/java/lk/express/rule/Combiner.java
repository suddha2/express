package lk.express.rule;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Combiner", namespace = "http://rule.express.lk")
@XmlRootElement
public enum Combiner {

	And("&", new Evaluator() {

		@Override
		public <T> boolean evaluate(ICondition<T> firstCondition, ICondition<T> secondCondition, T t) {
			if (!firstCondition.matches(t)) {
				return false;
			}
			return secondCondition.matches(t);
		}
	}),

	Or("|", new Evaluator() {

		@Override
		public <T> boolean evaluate(ICondition<T> firstCondition, ICondition<T> secondCondition, T t) {
			if (firstCondition.matches(t)) {
				return true;
			}
			return secondCondition.matches(t);
		}
	});

	private String symbol;
	private Evaluator evaluator;

	Combiner(String symbol, Evaluator evaluator) {
		this.symbol = symbol;
		this.evaluator = evaluator;
	}

	public String getSymbol() {
		return symbol;
	}

	@Override
	public String toString() {
		return symbol;
	}

	public static Combiner bySymbol(String symbol) {
		for (Combiner c : values()) {
			if (c.symbol.equals(symbol)) {
				return c;
			}
		}
		return null;
	}

	public <T> boolean combine(ICondition<T> firstCondition, ICondition<T> secondCondition, T t) {
		return this.evaluator.evaluate(firstCondition, secondCondition, t);
	}

	private interface Evaluator {
		public <T> boolean evaluate(ICondition<T> firstCondition, ICondition<T> secondCondition, T t);
	}
}