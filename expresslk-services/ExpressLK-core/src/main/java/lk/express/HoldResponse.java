package lk.express;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "HoldResponse", namespace = "http://express.lk")
@XmlSeeAlso({ HoldResult.class })
public class HoldResponse extends ExpResponse<HoldResult> {

	public static final int SEAT_NOT_AVAILABLE = -200;

	public HoldResponse() {
	}

	public HoldResponse(int status) {
		super(status);
	}

	public HoldResponse(HoldResult data) {
		super(data);
	}

	public HoldResponse(String errorMessage) {
		super(errorMessage);
	}
}
