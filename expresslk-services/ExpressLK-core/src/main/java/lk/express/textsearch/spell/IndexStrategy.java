package lk.express.textsearch.spell;

import java.util.List;

public interface IndexStrategy {

	void execute(Index index, List<String> words);
}
