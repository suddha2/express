package lk.express;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "SearchResponse", namespace = "http://express.lk")
@XmlSeeAlso({ SearchResult.class })
public class SearchResponse extends ExpResponse<SearchResult> {

	public SearchResponse() {
	}

	public SearchResponse(int status) {
		super(status);
	}

	public SearchResponse(String errorMessage) {
		super(errorMessage);
	}

	public SearchResponse(SearchResult data) {
		super(data);
	}
}
