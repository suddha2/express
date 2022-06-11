package lk.express.core;

public class NameCodePairImpl<T,S> {

	private T name;
	private S code;
	
	public NameCodePairImpl(T name , S code)
	{
		this.name = name;
		this.code = code;
	}

	public T getName() {
		return name;
	}

	public void setName(T name) {
		this.name = name;
	}

	public S getCode() {
		return code;
	}

	public void setCode(S code) {
		this.code = code;
	}
	
	
}
