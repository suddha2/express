package lk.express.filter;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "FilterData", namespace = "http://filter.express.lk")
public class FilterData {

	private String type;
	private List<FilterValue> values;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<FilterValue> getValues() {
		if (values == null) {
			values = new ArrayList<>();
		}
		return values;
	}

	public void setValues(List<FilterValue> values) {
		this.values = values;
	}
}
