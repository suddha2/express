/**
 * 
 */
package lk.express.textsearch.spell.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Tim Harrington
 * @date Oct 10, 2009
 *
 */
public class SpellcheckUtils {

	/**
	 * Console-based utility for presenting a list of potential replacement
	 * words and prompting the user to select one of them. Returns the word that
	 * was selected.
	 * 
	 * @param word
	 * @param suggestions
	 * @return
	 */
	public static String suggest(String word, String[] suggestions) {
		// housekeeping
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		String input;
		String selection = "";

		// loop until a selection is made
		while (true) {
			System.out.println("Your query contains \"" + word + "\".");
			System.out.println("Did you mean...");
			System.out.println("1. " + word + " (no change)");
			for (int i = 0; i < suggestions.length; i++) {
				System.out.println((i + 2) + ". " + suggestions[i]);
			}
			System.out.println();
			System.out.print("Please enter a selection: ");
			System.out.flush();
			try {
				input = stdin.readLine();
				System.out.println();
				int j;
				try {
					j = Integer.parseInt(input);
					if (Math.min(j, suggestions.length + 2 - j) > 0) {
						// the selection is okay
						if (j == 1) { // the original word was selected
							selection = word;
						} else {
							selection = suggestions[j - 2];
						}
						break;
					} else {
						System.out.println("Invalid selection. " + "Please try again.");
						System.out.println();
						continue;
					}
				} catch (NumberFormatException e) {
					System.out.println("Invalid selection. Please try again.");
					System.out.println();
					continue;
				}
			} catch (IOException e) {
				System.err.print("Spell check failed.");
				// give up
				selection = word;
				break;
			}
		}
		// System.out.println("You selected \"" + selection + "\".");
		return selection;
	}

	/**
	 * Console-based utility for presenting a list of potential replacement
	 * words and prompting the user to select one of them. Returns the word that
	 * was selected.
	 * 
	 * @param word
	 * @param suggestions
	 * @return
	 */
	public static String suggest(String word, Collection<String> suggestions) {
		String[] arr = suggestions.toArray(new String[suggestions.size()]);
		return suggest(word, arr);
	}

	/**
	 * Tokenizes a Lucene-syntax query then returns a list of unique terms
	 * present in the query.
	 * 
	 * TODO: ignore tokens that were a part of wildcard searches
	 * 
	 * @param query
	 * @return
	 */
	public static Set<String> getUniqueTokens(String query) {
		List<String> words = StringUtils.tokenize(query);
		Set<String> unique = new HashSet<String>();
		unique.addAll(words);
		return unique;
	}

}
