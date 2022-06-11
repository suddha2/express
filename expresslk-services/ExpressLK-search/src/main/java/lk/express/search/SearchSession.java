package lk.express.search;

import lk.express.Session;
import lk.express.config.ConfigurationManager;

public class SearchSession extends Session {

	private static final long serialVersionUID = 1L;
	private static final String CONFIG_SESSION_TIMEOUT = "SESSION_TIMEOUT";

	private ISearchGateway gateway;

	public SearchSession(String id) {
		super(id);
		timeout = ConfigurationManager.getConfiguration().getLong(CONFIG_SESSION_TIMEOUT, DEFAULT_SESSION_TIMEOUT);
	}

	@Override
	public void init() {
	}

	@Override
	public void end() {
	}

	public ISearchGateway getGateway() {
		return gateway;
	}

	public void setGateway(ISearchGateway gateway) {
		this.gateway = gateway;
	}
}
