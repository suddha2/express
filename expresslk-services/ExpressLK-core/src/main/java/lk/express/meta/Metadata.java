package lk.express.meta;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import lk.express.core.MetadataManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Metadata {

	private static final Logger logger = LoggerFactory.getLogger(Metadata.class);

	// jaxb
	protected Object jaxbRootObject;

	/**
	 * @param document
	 * @param packageName
	 */
	public Metadata(Document document, String packageName) {
		try {
			JAXBContext jc = JAXBContext.newInstance(packageName);
			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement<?> element = (JAXBElement<?>) u.unmarshal(document.getDocumentElement());
			jaxbRootObject = element.getValue();
			initialize();
		} catch (JAXBException je) {
			logger.error("Exception while unmarchaling.", je);
			throw new MetadataException(je);
		}
	}

	public static Metadata createFromStream(InputStream stream) {
		Document doc = readStream(stream);
		return loadMetadata(doc);
	}

	private static Metadata loadMetadata(Document doc) {
		MetaBaseParser baseParser = new MetaBaseParser(doc);
		String processor = baseParser.getProcessor();
		if (processor == null) {
			throw new MetadataException("Processor not specified.");
		}
		Metadata metadata = null;
		try {
			Class<?> metadataClass = MetadataManager.getClass(processor);
			Constructor<?> constructor = metadataClass.getConstructor(Document.class);
			metadata = (Metadata) constructor.newInstance(doc);
		} catch (Exception e) {
			throw new MetadataException(e);
		}
		return metadata;
	}

	/**
	 * subclasses must implement this method to have their own processing
	 * components.
	 */
	protected void initialize() {
	}

	private static Document readStream(InputStream inputStream) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		docFactory.setValidating(true);
		Document doc = null;
		try {
			DocumentBuilder builder = docFactory.newDocumentBuilder();
			builder.setErrorHandler(null);
			doc = builder.parse(inputStream);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			logger.error("Exception while reading.", e);
			throw new MetadataException(e);
		}
		return doc;
	}
}