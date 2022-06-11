package lk.express.db;

public class DBException extends RuntimeException {

	private static final long serialVersionUID = -3264374806892927740L;

	public DBException(String message) {
		super(message);
	}

	public DBException(Throwable root) {
		super(root);
	}

	public DBException(String message, Throwable root) {
		super(message, root);
	}
}
