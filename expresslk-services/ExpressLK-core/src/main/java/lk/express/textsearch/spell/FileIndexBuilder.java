package lk.express.textsearch.spell;

import java.io.File;

import lk.express.textsearch.spell.util.IOUtils;
import lk.express.textsearch.spell.util.StringUtils;

public class FileIndexBuilder implements IndexBuilder {

	private int k;
	private String dataFile;
	private IndexStrategy strategy;

	public FileIndexBuilder(int k, String dataFile, IndexStrategy strategy) {
		this.k = k;
		this.dataFile = dataFile;
		this.strategy = strategy;
	}

	@Override
	public Index build() {
		Index index = new IndexImpl(k);
		File path = new File(dataFile);
		for (String line : IOUtils.readLines(IOUtils.openFile(path))) {
			strategy.execute(index, StringUtils.tokenize(line));
		}
		return index;
	}

}
