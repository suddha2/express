package lk.express.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lk.express.ResultLeg;
import lk.express.ResultSector;
import lk.express.bean.Amenity;
import lk.express.bean.Bus;

@XmlRootElement
@XmlType(name = "AmenityFilter", namespace = "http://filter.express.lk")
public class AmenityFilter extends Filter<ResultLeg> {

	private String amenityCode;

	public String getAmenityCode() {
		return amenityCode;
	}

	public void setAmenityCode(String amenityCode) {
		this.amenityCode = amenityCode;
	}

	@Override
	public boolean test(ResultLeg leg) {
		for (ResultSector sector : leg.getSectors()) {
			Bus bus = sector.getSchedule().getBus();
			if (!bus.hasAmenity(amenityCode)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public List<FilterValue> getValues(List<? extends ResultLeg> list) {
		Set<Amenity> amenities = new HashSet<>();
		for (ResultLeg leg : list) {
			for (ResultSector sector : leg.getSectors()) {
				Bus bus = sector.getSchedule().getBus();
				amenities.addAll(bus.getAmenities());
			}
		}
		List<FilterValue> values = new ArrayList<>();
		for (Amenity amenity : amenities) {
			values.add(new FilterValue(amenity.getCode(), amenity.getName()));
		}
		return values;
	}
}
