package lk.express.rule;

public interface IRule<T> extends Comparable<IRule<T>> {

	boolean matches(T t);

	void apply(T t);

	float getSalience();

	String getScheme();
}
