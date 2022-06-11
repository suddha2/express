package lk.express.textsearch.spell;

public abstract class KGramSpellingCorrector implements SpellingCorrector {

	protected int k;
	protected Index index;

	public KGramSpellingCorrector(int k, IndexBuilder builder) {
		this.k = k;
		this.index = builder.build();
	}

	@Override
	public boolean exists(String term) {
		return index.exists(term);
	}
}
