package lk.express.api.v2;

public class APICity extends APINameCodeEntity {

	/**
	 * @exclude
	 */
	public APICity() {

	}

	/**
	 * @exclude
	 */
	public APICity(lk.express.bean.City e) {
		super(e);
		this.id = e.getId();
	}
}
