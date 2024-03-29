package lk.express.textsearch.spell.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * String tokenization utilities.
 */
public class StringUtils {

	private final static int maxTokenLength = 20;
	private final static int minWordLength = 1;
	private final static Pattern wordRE = Pattern.compile("[a-zA-Z]+(['\\-][a-zA-Z]+)?");
	private final static String delims = " \t\n\r\f.()\"',-:;/\\?!@";

	/**
	 * Returns the list of tokens in the given text as they appear in order.
	 */
	public static List<String> tokenize(String text) {
		List<String> tokens = new LinkedList<String>();
		StringTokenizer tokenizer = new StringTokenizer(text, delims);
		while (tokenizer.hasMoreTokens()) {
			String word = tokenizer.nextToken().toLowerCase();

			if (wordRE.matcher(word).matches() && word.length() >= minWordLength && word.length() < maxTokenLength) {
				tokens.add(word);
			}
		}
		return tokens;
	}

	public static ArrayList<String> bigrams(String term) {
		ArrayList<String> bigrams = new ArrayList<String>();

		bigrams.add("$" + term.charAt(0));

		for (int i = 0; i < term.length() - 1; i++) {
			String bigram = term.substring(i, i + 2);

			if (!bigrams.contains(bigram))
				bigrams.add(bigram);
		}

		bigrams.add(term.charAt(term.length() - 1) + "$");

		return bigrams;
	}

	public static List<String> kgrams(int k, String term) {
		List<String> kgrams = new ArrayList<String>();
		kgrams.add("$" + term.substring(0, k - 1));
		for (int i = 0; i <= term.length() - k; i++) {
			String kgram = term.substring(i, i + k);
			if (!kgrams.contains(kgram)) {
				kgrams.add(kgram);
			}
		}
		kgrams.add(term.substring(term.length() - k + 1, term.length()) + "$");

		return kgrams;
	}

	// TODO: expand rule
	public static Boolean isValidTerm(String term) {
		if (term.length() <= 1)
			return false;

		return true;
	}

	public static double jaccard(List<String> kgrams1, List<String> kgrams2) {
		int intersectionSize = 0, unionSize = kgrams1.size() + kgrams2.size();

		for (String kgram1 : kgrams1) {
			for (String kgram2 : kgrams2) {
				if (kgram1.equals(kgram2)) {
					intersectionSize++;
					unionSize--;
				}
			}
		}

		return ((double) intersectionSize) / unionSize;
	}

	public static double jaccard(String term1, String term2) {
		ArrayList<String> bigrams1 = bigrams(term1), bigrams2 = bigrams(term2);
		return jaccard(bigrams1, bigrams2);
	}

	public static String removeVowels(String word) {
		String middle = word.substring(1, word.length() - 1);
		return word.charAt(0)
				+ middle.replaceAll("a", "").replaceAll("e", "").replaceAll("i", "").replaceAll("o", "")
						.replaceAll("u", "") + word.charAt(word.length() - 1);

	}

	/**
	 * Returns the Levenshtein edit distance between the two given Strings.
	 */
	public static double levenshtein(String a, String b) {
		return new EditDistance().score(a, b);
	}

	/**
	 * Find the (Levenshtein) edit distance between two Strings or Character
	 * arrays.
	 * 
	 * This class is excerpted from JavaNLP.
	 * 
	 * @author Dan Klein
	 */
	private static class EditDistance {

		protected double[][] score = null;

		protected void clear(int sourceLength, int targetLength) {
			if (score == null || score.length < sourceLength + 1 || score[0].length < targetLength + 1) {
				score = new double[sourceLength + 1][targetLength + 1];
			}
			for (int i = 0; i < score.length; i++) {
				Arrays.fill(score[i], worst());
			}
		}

		// CONSTRAINT SEMIRING START

		protected double best() {
			return 0.0;
		}

		protected double worst() {
			return Double.POSITIVE_INFINITY;
		}

		protected double unit() {
			return 1.0;
		}

		protected double better(double x, double y) {
			if (x < y) {
				return x;
			}
			return y;
		}

		protected double combine(double x, double y) {
			return x + y;
		}

		// CONSTRAINT SEMIRING END

		// COST FUNCTION BEGIN

		protected double insertCost(Object o) {
			return unit();
		}

		protected double deleteCost(Object o) {
			return unit();
		}

		protected double substituteCost(Object source, Object target) {
			if (source.equals(target)) {
				return best();
			}
			return unit();
		}

		double transposeCost(Object s1, Object s2, Object t1, Object t2) {
			if (s1.equals(t2) && s2.equals(t1)) {
				return unit();
			}
			return worst();
		}

		// COST FUNCTION END

		double score(Object[] source, int sPos, Object[] target, int tPos) {
			double bscore = score[sPos][tPos];
			if (bscore == worst()) {
				if (sPos == 0 && tPos == 0) {
					bscore = best();
				} else {
					if (sPos > 0) {
						bscore = better(bscore,
								(combine(score(source, sPos - 1, target, tPos), deleteCost(source[sPos - 1]))));
					}
					if (tPos > 0) {
						bscore = better(bscore,
								(combine(score(source, sPos, target, tPos - 1), insertCost(target[tPos - 1]))));
					}
					if (sPos > 0 && tPos > 0) {
						bscore = better(
								bscore,
								(combine(score(source, sPos - 1, target, tPos - 1),
										substituteCost(source[sPos - 1], target[tPos - 1]))));
					}
					if (sPos > 1 && tPos > 1) {
						bscore = better(
								bscore,
								(combine(
										score(source, sPos - 2, target, tPos - 2),
										transposeCost(source[sPos - 2], source[sPos - 1], target[tPos - 2],
												target[tPos - 1]))));
					}
				}
				score[sPos][tPos] = bscore;
			}
			return bscore;
		}

		@SuppressWarnings("unused")
		public double score(Object[] source, Object[] target) {
			clear(source.length, target.length);
			return score(source, source.length, target, target.length);
		}

		public double score(String sourceStr, String targetStr) {
			Object[] source = Characters.asCharacterArray(sourceStr);
			Object[] target = Characters.asCharacterArray(targetStr);
			clear(source.length, target.length);
			return score(source, source.length, target, target.length);
		}
	}

	/**
	 * Character utilities. So far mainly performs a mapping of char to a
	 * Singleton (interned) Character for each character. <i>Note: current
	 * implementation is correct only for BMP characters.</i>
	 *
	 * @author Dan Klein
	 */
	private static class Characters {

		/** Only static methods */
		private Characters() {
		}

		private static Character[] canonicalCharacters = null;

		public static Character getCharacter(char c) {
			if (canonicalCharacters == null) {
				canonicalCharacters = new Character[65536];
			}
			Character cC = canonicalCharacters[c];
			if (cC == null) {
				cC = new Character(c);
				canonicalCharacters[c] = cC;
			}
			return cC;
		}

		public static Character[] asCharacterArray(String s) {
			Character[] split = new Character[s.length()];
			for (int i = 0; i < split.length; i++) {
				split[i] = getCharacter(s.charAt(i));
			}
			return split;
		}

		@SuppressWarnings("unused")
		public static List<Character> asCharacterList(String s) {
			return Arrays.asList(asCharacterArray(s));
		}
	}

}
