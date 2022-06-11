package lk.express.textsearch.spell;

import java.util.List;

public class KGramJaccardSpellingCorrector extends KGramSpellingCorrector {

	public KGramJaccardSpellingCorrector(int k, IndexBuilder builder) {
		super(k, builder);
	}

	@Override
	public List<String> corrections(String word, int count) {
		return index.corrections(word, count, new JaccardScorer(k));
	}
}
