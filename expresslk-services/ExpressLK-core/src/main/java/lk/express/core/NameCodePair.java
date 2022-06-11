package lk.express.core;

public class NameCodePair {

	private String name;
	private String code;

	public NameCodePair() {
	}

	public NameCodePair(String name, String code) {
		this.name = name;
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "NameCodePair [name=" + name + ", code=" + code + "]";
	}
}
