package lk.express;

public class IrregularFareException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IrregularFareException(String message) {
		super(message);
	}
}
