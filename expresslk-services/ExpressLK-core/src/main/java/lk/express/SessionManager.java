package lk.express;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import lk.express.config.Configuration;
import lk.express.config.ConfigurationManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManager {

	private static final int RETRIES = 5;

	private static final long DEFAULT_SESSION_CLEAN_INTERVAL = 60; // seconds
	private static final String CONFIG_SESSION_CLEAN_INTERVAL = "SESSION_CLEAN_INTERVAL";

	private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
	private static final Configuration config = ConfigurationManager.getConfiguration();

	private final Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();
	private final List<SessionListener> listeners = new ArrayList<SessionListener>();
	private final Thread cleaner = new Thread(new SessionCleaner());

	public SessionManager() {
		cleaner.start();
	}

	/**
	 * Creates an instance of the passed subclass of the {@link Session} class
	 * with given sessionId. <br>
	 * If the passed session id is {@code null}/empty a random session id is
	 * generated. If the passed session id is already in use {@code null} is
	 * returned.
	 * 
	 * @param sessionId
	 *            session id
	 * @param clazz
	 *            class of the session to be instantiated
	 * @return the generated session or {@code null} if the session id is
	 *         already in use.
	 */
	public <T extends Session> T createSession(String sessionId, Class<T> clazz) {
		String id;
		for (int a = 0; a < RETRIES; a++) {
			if (sessionId == null || sessionId.trim().isEmpty()) {
				id = generateRandomId();
			} else {
				if (a == 0) {
					id = sessionId;
				} else {
					return null;
				}
			}

			synchronized (sessions) {
				if (!sessions.containsKey(id)) {
					T session = null;
					try {
						Constructor<T> c = clazz.getConstructor(String.class);
						session = c.newInstance(id);
						session.init();
						logger.info("Session created: {}", new Object[] { id });
						sessions.put(id, session);
						for (SessionListener listener : listeners) {
							listener.sessionCreated(new SessionEventImpl(session));
						}
					} catch (Exception e) {
						logger.error("Error while creating session", e);
						throw new RuntimeException(e);
					}
					return session;
				}
			}
		}
		return null;
	}

	private Session getSession(String sessionId) {
		return sessions.get(sessionId);
	}

	/**
	 * Returns the valid session identified by the passed session id. If no
	 * session exists by the session id or the session is expired, {@code null}
	 * is returned.
	 * 
	 * @param sessionId
	 *            session id
	 * @return valid session or {@code null}
	 */
	public Session getValidSession(String sessionId) {
		Session session = getSession(sessionId);
		if (session != null) {
			if (session.isExpired()) {
				logger.info("Expired session: {}", new Object[] { sessionId });
				endSession(sessionId);
				return null;
			} else {
				logger.debug("Valid sesison: {}", new Object[] { sessionId });
				updateSession(session);
			}
		}
		return session;
	}

	/**
	 * Ends the session identified by the passed session id.
	 * 
	 * @param sessionId
	 *            session id
	 */
	public void endSession(String sessionId) {
		logger.info("Ending session: {}", new Object[] { sessionId });
		Session session = sessions.remove(sessionId);
		if (session != null) {
			session.end();
		}
		for (SessionListener listener : listeners) {
			listener.sessionDestroyed(new SessionEventImpl(session));
		}
	}

	public void updateSession(Session session) {
		session.update();
	}

	public void addSessionListener(SessionListener listener) {
		listeners.add(listener);
	}

	public void removeSessionListener(SessionListener listener) {
		listeners.remove(listener);
	}

	private String generateRandomId() {
		return UUID.randomUUID().toString();
	}

	/*
	 * This thread periodically cleans expired sessions
	 */
	private class SessionCleaner implements Runnable {

		@Override
		public void run() {
			while (true) {
				Iterator<Entry<String, Session>> iterator = sessions.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, Session> entry = iterator.next();
					// if expired remove session
					if (entry.getValue().isExpired()) {
						endSession(entry.getKey());
					}
				}
				try {
					// sleep for a specified time before cleaning again
					Thread.sleep(config.getLong(CONFIG_SESSION_CLEAN_INTERVAL, DEFAULT_SESSION_CLEAN_INTERVAL) * 1000);
				} catch (InterruptedException e) {
					logger.error("Exception in session cleaner thread", e);
				}
			}
		}
	}
}
