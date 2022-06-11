package lk.express.api.v2;

import lk.express.bean.HasNameCode;

public abstract class APINameCodeEntity extends APINamedEntity {

	public String code;

	/**
	 * @exclude
	 */
	public APINameCodeEntity() {

	}

	/**
	 * @exclude
	 */
	public <T extends lk.express.bean.Entity & HasNameCode> APINameCodeEntity(T e) {
		super(e);
		name = e.getName();
		code = e.getCode();
	}
}
