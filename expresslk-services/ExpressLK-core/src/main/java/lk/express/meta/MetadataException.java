package lk.express.meta;

public class MetadataException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MetadataException(String arg0) {
		super(arg0);
	}

	public MetadataException(Exception e) {
		super(e);
	}
}
