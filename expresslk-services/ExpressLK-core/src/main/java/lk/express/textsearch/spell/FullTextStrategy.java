package lk.express.textsearch.spell;

import java.util.List;

public class FullTextStrategy implements IndexStrategy {

	@Override
	public void execute(Index index, List<String> words) {
		for (String word : words) {
			index.index(word);
		}
	}
}
