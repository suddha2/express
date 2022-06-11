package lk.express.textsearch.spell;

import java.util.List;

public class PrefixStrategy implements IndexStrategy {

	private int k;

	public PrefixStrategy(int k) {
		this.k = k;
	}

	@Override
	public void execute(Index index, List<String> words) {
		for (String word : words) {
			int len = word.length();
			if (word.length() > k) {
				for (int a = k; a < len; a++) {
					index.index(word.substring(0, a));
				}
			}
			index.index(word);
		}
	}
}
