package lk.express.accounting;

public class AccountingException extends Exception {

	private static final long serialVersionUID = 1L;

	public AccountingException(String message) {
		super(message);
	}

	public AccountingException(String message, Throwable cause) {
		super(message, cause);
	}

	public AccountingException(Throwable throwable) {
		super(throwable);
	}
}
