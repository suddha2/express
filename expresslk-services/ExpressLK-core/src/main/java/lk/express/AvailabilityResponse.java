package lk.express;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "AvailabilityResponse", namespace = "http://express.lk")
@XmlSeeAlso({ AvailabilityResult.class })
public class AvailabilityResponse extends ExpResponse<AvailabilityResult> {

	public AvailabilityResponse() {
	}

	public AvailabilityResponse(int status) {
		super(status);
	}

	public AvailabilityResponse(String errorMessage) {
		super(errorMessage);
	}

	public AvailabilityResponse(AvailabilityResult data) {
		super(data);
	}
}
