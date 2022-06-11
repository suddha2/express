package lk.express;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "ReleaseResponse", namespace = "http://express.lk")
public class ReleaseResponse extends ExpResponse<Void> {

	public ReleaseResponse() {
	}

	public ReleaseResponse(int status) {
		super(status);
	}

	public ReleaseResponse(String errorMessage) {
		super(errorMessage);
	}
}
