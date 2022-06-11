package lk.express.api.v2;

import lk.express.bean.Gender;

public class APIPassenger extends APINamedEntity {

	public String nic;
	public Gender gender;
	public String email;
	public String mobileTelephone;

	/**
	 * @exclude
	 */
	public APIPassenger() {

	}

	/**
	 * @exclude
	 */
	public APIPassenger(lk.express.reservation.Passenger e) {
		super(e);
		name = e.getName();

		nic = e.getNic();
		gender = e.getGender();
		email = e.getEmail();
		mobileTelephone = e.getMobileTelephone();
	}
}
