package lk.express.bean;

public class IllegalPropertyValueException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IllegalPropertyValueException() {
		super();
	}

	public IllegalPropertyValueException(String s) {
		super(s);
	}

	public IllegalPropertyValueException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalPropertyValueException(Throwable cause) {
		super(cause);
	}
}
