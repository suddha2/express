package lk.express.textsearch.spell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lk.express.textsearch.spell.util.Counter;
import lk.express.textsearch.spell.util.StringUtils;

public class IndexImpl implements Index {

	private static final float matchingWeight = 0.7f;

	private int k;
	private Map<String, List<String>> index;
	private Counter<String> frequencyCounter;

	public IndexImpl(int k) {
		this.k = k;

		index = new HashMap<String, List<String>>();
		frequencyCounter = new Counter<String>();
	}

	@Override
	public void index(String term) {
		if (!hasSeenTerm(term)) {
			if (term.length() >= k - 1) {
				List<String> kGrams = StringUtils.kgrams(k, term);
				addTerm(term, kGrams);
			}
		}
		incrementSeenCount(term);
	}

	@Override
	public boolean exists(String term) {
		return hasSeenTerm(term);
	}

	@Override
	public List<String> corrections(String term, int n, Scorer scorer) {
		Counter<String> scoreKeeper = new Counter<String>();

		if (term.length() >= k) {
			List<String> kgrams = StringUtils.kgrams(k, term);

			Set<String> all = new HashSet<String>();
			for (String kgram : kgrams) {
				all.addAll(getTerms(kgram));
			}

			List<String> top = frequencyCounter.topK(1);
			double max = frequencyCounter.getCount(top.get(0));
			for (String word : all) {
				double matchingScore = scorer.score(word, term);
				double frequencyScore = frequencyCounter.getCount(word) / max;
				scoreKeeper.setCount(word, matchingScore * matchingWeight + frequencyScore * (1 - matchingWeight));
			}

			return scoreKeeper.topK(n);
		} else {
			return new ArrayList<String>();
		}
	}

	private void addTerm(String term, List<String> kgrams) {
		for (String kgram : kgrams) {
			List<String> list = index.get(kgram);
			if (list == null) {
				list = new ArrayList<String>();
				index.put(kgram, list);
			}
			list.add(term);
		}
	}

	private List<String> getTerms(String kgram) {
		List<String> terms = index.get(kgram);
		return (terms == null ? new ArrayList<String>() : terms);
	}

	private void incrementSeenCount(String term) {
		frequencyCounter.incrementCount(term);
	}

	private boolean hasSeenTerm(String term) {
		return frequencyCounter.containsKey(term);
	}
}
