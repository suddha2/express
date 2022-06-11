package lk.express.filter;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "LegFilterData", namespace = "http://filter.express.lk")
public class LegFilterData {

	private List<FilterData> data;

	public List<FilterData> getData() {
		if (data == null) {
			data = new ArrayList<>();
		}
		return data;
	}

	public void setData(List<FilterData> data) {
		this.data = data;
	}
}
