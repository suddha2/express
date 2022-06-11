package lk.express.bean;

import lk.express.admin.Person;

public interface HasPerson {

	Person getPerson();

	default String getFullName() {
		return getPerson().getFullName();
	}

	default void setFullName(String fullName) {
		getPerson().setFullName(fullName);
	}
}
