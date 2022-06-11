package lk.express.textsearch.spell;

import lk.express.textsearch.spell.util.StringUtils;

public class LevenshteinScorer implements Scorer {

	@Override
	public double score(String term1, String term2) {
		return 1 / StringUtils.levenshtein(term1, term2);
	}
}
