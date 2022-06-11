package lk.express.bean;

import java.io.Serializable;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import lk.express.rule.bean.Rule;

/**
 * This is the super type of all the entities that are saved using Hibernate.
 * This class implements a set of useful methods all the Hibernate based
 * entities should possess.
 */
@XmlType(name = "Entity", namespace = "http://bean.express.lk")
@XmlSeeAlso({ LightEntity.class, Rule.class })
public abstract class Entity implements Serializable {

	private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	private static final long serialVersionUID = 1L;

	/**
	 * Returns the auto incremented long value that is used by Hibernate to
	 * distinguish between entities
	 * 
	 * @return auto incremented id value
	 */
	public abstract Integer getId();

	/**
	 * Validates whether the property values of the entity meats the required
	 * criteria
	 * 
	 * @throws IllegalPropertyValueException
	 */
	public void validate() throws IllegalPropertyValueException {
		Set<ConstraintViolation<Entity>> violations = validator.validate(this);
		if (!violations.isEmpty()) {
			throw new IllegalPropertyValueException(violations.toString());
		}
	}

	/**
	 * Fill this entity with some information from the old entity upon updating
	 * 
	 * @param old
	 */
	public void updateFromOld(Entity old) {
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[ " + getId() + " ]";
	}
}
