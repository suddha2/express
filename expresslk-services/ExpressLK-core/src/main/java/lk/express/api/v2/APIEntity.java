package lk.express.api.v2;

public abstract class APIEntity {

	public Integer id;

	/**
	 * @exclude
	 */
	public APIEntity() {

	}

	/**
	 * @exclude
	 */
	public APIEntity(lk.express.bean.Entity e) {
		// id = e.getId();
	}
}
