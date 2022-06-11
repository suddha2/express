package lk.express.meta;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A parser for basic information of metadata. This class provides a faster
 * light-weight API for parsing the essential "boot-up" information from a
 * stand-alone metadata XML stream. This information can then be used in loading
 * the correct fully-equipped custom parser for the stream.
 *
 * As speed is the main concern here, we terminate the parsing after we
 * collected the necessary fields.
 *
 */
public class MetaBaseParser {

	private static final String XML_PROCESSOR = "processor";
	private static final String XML_RM_META = "http://express.lk/2015/meta";

	String processor;

	public MetaBaseParser(Document document) {
		parseMetaInfo(document);
	}

	/**
	 * @param document
	 */
	private void parseMetaInfo(Document document) {
		Element rootElement = document.getDocumentElement();
		Node procElement = rootElement.getElementsByTagNameNS(XML_RM_META, XML_PROCESSOR).item(0);
		processor = procElement.getTextContent().trim();
	}

	/**
	 * @return the processor
	 */
	public String getProcessor() {
		return processor;
	}
}
