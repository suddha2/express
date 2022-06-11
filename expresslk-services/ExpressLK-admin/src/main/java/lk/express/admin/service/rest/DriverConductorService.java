package lk.express.admin.service.rest;

import lk.express.bean.Entity;
import lk.express.bean.HasPerson;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public abstract class DriverConductorService<T extends Entity & HasPerson> extends HasPersonService<T> {

	public DriverConductorService(Class<T> clazz) {
		super(clazz);
	}

	@Override
	protected void searchByFullName(String value, Criteria criteria, String likePredicate, String alias) {

		String[] nameParts = value.split("\\s+");

		if (nameParts.length == 1) {
			criteria.add(Restrictions.or(
					getCriteria(String.class, PERSON_ALIAS + "." + "firstName", nameParts[0], likePredicate),
					getCriteria(String.class, PERSON_ALIAS + "." + "lastName", nameParts[0], likePredicate),
					getCriteria(String.class, "nickName", nameParts[0], likePredicate)));
		} else if (nameParts.length == 2) {
			criteria.add(getCriteria(String.class, PERSON_ALIAS + "." + "firstName", nameParts[0], likePredicate));
			criteria.add(getCriteria(String.class, PERSON_ALIAS + "." + "lastName", nameParts[1], likePredicate));
		} else if (nameParts.length > 2) {
			criteria.add(getCriteria(String.class, PERSON_ALIAS + "." + "firstName", nameParts[0], likePredicate));
			criteria.add(getCriteria(String.class, PERSON_ALIAS + "." + "lastName", nameParts[1], likePredicate));
			String nick = nameParts[2].replace("(", "").replace(")", "");
			criteria.add(getCriteria(String.class, "nickName", nick, likePredicate));
		}
	}
}
