package lk.express.textsearch.spell;

import java.util.List;

/**
 * Basic interface for the spelling corrector.
 * 
 * @author dramage
 */
public interface SpellingCorrector {

	/**
	 * Returns list of possible spelling corrections for a given (possibly
	 * misspelled) word. The length of the returned list is up to the
	 * implementer, but enough high-scoring candidates should be included so
	 * that the best corrected spelling is present (high) in the list.
	 * 
	 * @param word
	 *            A single word whose spelling is to be checked.
	 * @param count
	 *            No. of corrections to provide
	 * @return A list of possible corrections, with better corrections ordered
	 *         first.
	 */
	public List<String> corrections(String word, int count);

	/**
	 * Returns whether a term exists in the index
	 * 
	 * @param term
	 *            single term to look for in the index
	 * @return whether the term exists in the index
	 */
	public boolean exists(String term);
}
