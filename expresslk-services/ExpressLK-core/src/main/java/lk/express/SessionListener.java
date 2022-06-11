package lk.express;

public interface SessionListener {

	public void sessionCreated(SessionEvent event);

	public void sessionDestroyed(SessionEvent event);
}
