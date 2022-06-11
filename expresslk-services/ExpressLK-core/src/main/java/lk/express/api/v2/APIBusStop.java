package lk.express.api.v2;

public class APIBusStop extends APINamedEntity {

	public APICity city;

	/**
	 * @exclude
	 */
	public APIBusStop() {

	}

	/**
	 * @exclude
	 */
	public APIBusStop(lk.express.bean.BusStop e) {
		super(e);
		name = e.getName();

		city = new APICity(e.getCity());
	}
}
