package lk.express.auth;

public class AuthenticationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AuthenticationException() {
		super();
	}

	public AuthenticationException(String message) {
		super(message);
	}

	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthenticationException(Throwable cause) {
		super(cause);
	}
}
