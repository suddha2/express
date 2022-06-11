package lk.express;

public class Context {

	private static final ThreadLocal<SessionData> sessionData = new InheritableThreadLocal<SessionData>() {
		@Override
		protected SessionData initialValue() {
			return new SessionData();
		}
	};

	public static SessionData getSessionData() {
		return sessionData.get();
	}
}
