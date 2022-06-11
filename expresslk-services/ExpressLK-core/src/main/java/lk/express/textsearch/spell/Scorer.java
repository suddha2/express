package lk.express.textsearch.spell;

public interface Scorer {

	/**
	 * Scores the similarity of the tow terms
	 * 
	 * @param term1
	 *            first term
	 * @param term2
	 *            second term
	 * @return
	 */
	double score(String term1, String term2);
}
