package lk.express.rule;

public interface ICondition<T> {

	boolean matches(T t);
}