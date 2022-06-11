package lk.express.api.v2;

public abstract class APINamedEntity extends APIEntity {

	public String name;

	/**
	 * @exclude
	 */
	public APINamedEntity() {

	}

	/**
	 * @exclude
	 */
	public APINamedEntity(lk.express.bean.Entity e) {
		super(e);
	}
}
