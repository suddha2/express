package lk.express;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "Result", namespace = "http://express.lk")
@XmlSeeAlso({ ResultLeg.class })
public class Result extends BaseCostPrice {

	private static final long serialVersionUID = 1L;

	public static final String OUT_BOUND_PREFIX = "0:";
	public static final String IN_BOUND_PREFIX = "1:";

	private String resultIndex;

	public String getResultIndex() {
		return resultIndex;
	}

	public void setResultIndex(String resultIndex) {
		this.resultIndex = resultIndex;
	}
}
