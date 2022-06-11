package lk.express.textsearch.spell;

import java.util.List;

public interface Index {

	/**
	 * Indexes a given term
	 * 
	 * @param term
	 *            term to be indexed
	 */
	public abstract void index(String term);

	/**
	 * Provides an n number of possible correction in the order scored by the
	 * passed {@link Scorer}
	 * 
	 * @param term
	 *            term to provide corrections for
	 * @param n
	 *            number of corrections to provide
	 * @param scorer
	 *            scorer to score corrections
	 * @return list of corrections
	 */
	public abstract List<String> corrections(String term, int n, Scorer scorer);

	/**
	 * Returns whether the term exists in the index
	 * 
	 * @param term
	 *            term to check for
	 * @return whether the term exists in the index
	 */
	public boolean exists(String term);

}