package lk.express.search;

public enum SearchType {
	Bus;

	public static SearchType byName(String name) {
		return SearchType.valueOf(name);
	}
}
