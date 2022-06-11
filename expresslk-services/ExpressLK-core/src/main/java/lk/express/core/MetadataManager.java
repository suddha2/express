package lk.express.core;

import java.io.InputStream;

import lk.express.io.file.FileManager;
import lk.express.meta.Metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetadataManager {

	private static final Logger logger = LoggerFactory.getLogger(MetadataManager.class);

	/**
	 * @param filename
	 * @return
	 */
	public static Metadata loadFromFile(String filename) {
		InputStream is = FileManager.readFile(filename);
		return Metadata.createFromStream(is);
	}

	/**
	 * @param processor
	 * @return
	 */
	public static Class<?> getClass(String processor) {
		Class<?> ret = null;
		try {
			ret = MetadataManager.class.getClassLoader().loadClass(processor);
		} catch (ClassNotFoundException e) {
			logger.error("Exception while loading processor class", e);
		}
		return ret;
	}
}
