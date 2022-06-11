package lk.express.search;

public class GatewayFactory {

	public static ISearchGateway createSearchGateway(SearchType type) {
		switch (type) {
		case Bus:
			return new BusGateway();
		}
		return null;
	}
}
