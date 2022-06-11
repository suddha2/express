package lk.express;

import java.io.Serializable;

public abstract class Session implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final long DEFAULT_SESSION_TIMEOUT = 30 * 60 * 1000;

	protected String id;
	protected String locale;
	protected long startTime;
	protected long updateTime;
	protected long timeout = DEFAULT_SESSION_TIMEOUT;

	public Session(String id) {
		this.id = id;
		this.startTime = System.currentTimeMillis();
		this.updateTime = System.currentTimeMillis();
	}

	public abstract void init();

	public abstract void end();

	public void update() {
		updateTime = System.currentTimeMillis();
	}

	public boolean isExpired() {
		return System.currentTimeMillis() - updateTime > timeout;
	}

	public String getId() {
		return id;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public long getTimeout() {
		return timeout;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
}
