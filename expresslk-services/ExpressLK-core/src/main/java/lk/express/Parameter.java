package lk.express;

import java.util.List;

import lk.express.metaxml.params.ParameterType;

public class Parameter {

	private String param;
	private ValueType type;
	private List<String> values;

	public Parameter() {
	}

	public Parameter(ParameterType meta) {
		param = meta.getName();
		type = ValueType.valueOf(meta.getType());
		values = meta.getValues().getValue();
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public ValueType getType() {
		return type;
	}

	public void setType(ValueType type) {
		this.type = type;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public Object getObject() {
		return type.getObject(values);
	}
}