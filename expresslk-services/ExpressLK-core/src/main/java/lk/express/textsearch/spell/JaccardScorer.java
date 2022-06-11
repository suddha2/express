package lk.express.textsearch.spell;

import java.util.List;

import lk.express.textsearch.spell.util.StringUtils;

public class JaccardScorer implements Scorer {

	private int k;

	public JaccardScorer(int k) {
		this.k = k;
	}

	@Override
	public double score(String term1, String term2) {
		List<String> kgrams1 = StringUtils.kgrams(k, term1), kgrams2 = StringUtils.kgrams(k, term2);
		return StringUtils.jaccard(kgrams1, kgrams2);
	}
}
