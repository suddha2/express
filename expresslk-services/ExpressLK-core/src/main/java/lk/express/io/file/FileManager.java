package lk.express.io.file;

import java.io.InputStream;

/**
 * This class handles the basic files reading.
 */
public class FileManager {

	public static InputStream readFile(String filename) {
		InputStream is = FileManager.class.getClassLoader().getResourceAsStream("/" + filename);
		if (is == null) { // fixes unit tests
			is = FileManager.class.getClassLoader().getResourceAsStream(filename);
		}
		return is;
	}
}
