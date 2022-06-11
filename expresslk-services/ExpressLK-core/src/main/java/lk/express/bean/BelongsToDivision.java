package lk.express.bean;

import lk.express.Context;
import lk.express.admin.Division;

public interface BelongsToDivision {

	Division getDivision();

	void setDivision(Division division);

	default void populateDivision() {
		if (getDivision() == null) {
			setDivision(Context.getSessionData().getUser().getDivision());
		}
	}
}
