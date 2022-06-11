package lk.express.schedule;

public class IncompatibleScheduleChangeException extends Exception {

	private static final long serialVersionUID = 1L;

	public IncompatibleScheduleChangeException() {
		super();
	}

	public IncompatibleScheduleChangeException(String message) {
		super(message);
	}
}
