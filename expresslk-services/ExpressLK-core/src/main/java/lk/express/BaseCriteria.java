package lk.express;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "BaseCriteria", namespace = "http://express.lk")
public class BaseCriteria implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String SOURCE_CONDUCTOR_APP = "conductorApp";

	private Integer clientId;
	private String source;
	private String discountCode;
	private String ip;

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDiscountCode() {
		return discountCode;
	}

	public void setDiscountCode(String discountCode) {
		this.discountCode = discountCode;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public static boolean isConductorApp(BaseCriteria criteria) {
		return criteria != null && SOURCE_CONDUCTOR_APP.equals(criteria.source);
	}
}
