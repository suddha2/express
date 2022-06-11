package lk.express;

public class SessionEventImpl implements SessionEvent {

	private Session session;

	public SessionEventImpl(Session session) {
		this.session = session;
	}

	@Override
	public Session getSession() {
		return session;
	}
}
