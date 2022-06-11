package lk.express.api.v2;

import java.util.ArrayList;
import java.util.List;

public class APIBus extends APINamedEntity {

	public String plateNumber;
	public String busType;
	public APITravelClass travelClass;
	public String supplier;

	public List<APIAmenity> amenities = new ArrayList<>();

	/**
	 * @exclude
	 */
	public APIBus() {

	}

	/**
	 * @exclude
	 */
	public APIBus(lk.express.bean.Bus e) {
		super(e);
		name = e.getName();

		plateNumber = e.getPlateNumber();
		busType = e.getBusType().getType();
		travelClass = new APITravelClass(e.getTravelClass());
		supplier = e.getSupplier().getName();
		for (lk.express.bean.Amenity a : e.getAmenities()) {
			amenities.add(new APIAmenity(a));
		}
	}
}
