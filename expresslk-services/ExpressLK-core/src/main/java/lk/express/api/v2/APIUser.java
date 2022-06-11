package lk.express.api.v2;

public class APIUser extends APIEntity {

	public String username;

	/**
	 * @exclude
	 */
	public APIUser() {

	}

	/**
	 * @exclude
	 */
	public APIUser(lk.express.admin.User e) {
		super(e);
		username = e.getUsername();
	}
}
