package lk.express.api.v2;

public class APIBusRoute extends APINamedEntity {

	public String routeNumber;
	public String displayNumber;
	public APICity fromCity;
	public APICity toCity;

	/**
	 * @exclude
	 */
	public APIBusRoute() {

	}

	/**
	 * @exclude
	 */
	public APIBusRoute(lk.express.bean.BusRoute e) {
		super(e);
		name = e.getName();

		routeNumber = e.getRouteNumber();
		displayNumber = e.getDisplayNumber();
		fromCity = new APICity(e.getFromCity());
		toCity = new APICity(e.getToCity());
	}
}
