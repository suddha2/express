package lk.express;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.SerializationUtils;

@XmlRootElement
@XmlType(name = "RepriceCriteria", namespace = "http://express.lk")
public class RepriceCriteria {

	// fields to update existing values in the search criteria
	private Integer clientId;
	private String source;
	private String discountCode;

	private String resultIndex;

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

	public String getResultIndex() {
		return resultIndex;
	}

	public void setResultsIndex(String resultIndex) {
		this.resultIndex = resultIndex;
	}

	public SearchCriteria updateSearchCriteria(SearchCriteria oldCriteria) {
		SearchCriteria clone = (SearchCriteria) SerializationUtils.clone(oldCriteria);
		if (clientId != null) {
			clone.setClientId(clientId);
		}
		if (source != null) {
			clone.setSource(source);
		}
		if (discountCode != null) {
			clone.setDiscountCode(discountCode);
		}

		return clone;
	}
}
