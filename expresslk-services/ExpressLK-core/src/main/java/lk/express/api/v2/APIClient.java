package lk.express.api.v2;

public class APIClient extends APINamedEntity {

	public String nic;
	public String mobileTelephone;
	public String email;

	/**
	 * @exclude
	 */
	public APIClient() {

	}

	/**
	 * @exclude
	 */
	public APIClient(lk.express.bean.Client e) {
		super(e);
		name = e.getName();

		nic = e.getNic();
		mobileTelephone = e.getMobileTelephone();
		email = e.getEmail();
	}
}
