package lk.express.api.v2;

public class APIAgent extends APINamedEntity {

	/**
	 * @exclude
	 */
	public APIAgent() {

	}

	/**
	 * @exclude
	 */
	public APIAgent(lk.express.admin.Agent e) {
		super(e);
		name = e.getName();
	}
}
