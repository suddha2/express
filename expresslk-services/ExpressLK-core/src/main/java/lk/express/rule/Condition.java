package lk.express.rule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import lk.express.RuleProperty;
import lk.express.ValueType;
import lk.express.rule.bean.RuleConditionSingle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Condition<T> implements ICondition<T> {

	private static final Logger logger = LoggerFactory.getLogger(Condition.class);

	private String property;
	private Qualifier qualifier;
	private Object value;

	public Condition(String property, Qualifier qualifier, Object value) {
		this.property = property;
		this.qualifier = qualifier;
		this.value = value;
	}

	public Condition(RuleConditionSingle condition) {
		this.property = condition.getProperty();
		this.qualifier = condition.getQualifier();
		String valueStr = condition.getValueString();
		ValueType valueType = condition.getValueType();
		this.value = valueType.getObject(Arrays.asList(valueStr));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.IRule#matches(T)
	 */
	@Override
	public boolean matches(T t) {
		try {
			Class<?> clazz = t.getClass();
			Method method = null;
			// look for methods in the class
			for (Method m : clazz.getDeclaredMethods()) {
				RuleProperty rp = m.getAnnotation(RuleProperty.class);
				if (rp != null && rp.value() != null && rp.value().equals(property)) {
					method = m;
					break;
				}
			}
			// if not found
			if (method == null) {
				// look for methods in super classes
				for (Method m : clazz.getMethods()) {
					RuleProperty rp = m.getAnnotation(RuleProperty.class);
					if (rp != null && rp.value() != null && rp.value().equals(property)) {
						method = m;
						break;
					}
				}
			}
			if (method != null) {
				Object val = method.invoke(t);
				return qualifier.evaluate(val, value);
			}
		} catch (InvocationTargetException e) {
			logger.error("InvocationTargetException while condition matching", e);
		} catch (IllegalAccessException e) {
			logger.error("IllegalAccessException while condition matching", e);
		}

		return false;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public Qualifier getQualifier() {
		return qualifier;
	}

	public void setQualifier(Qualifier qaulifier) {
		this.qualifier = qaulifier;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "(" + property + " " + qualifier + " " + value + ")";
	}
}
