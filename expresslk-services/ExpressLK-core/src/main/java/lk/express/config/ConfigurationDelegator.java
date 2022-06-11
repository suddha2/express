package lk.express.config;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class ConfigurationDelegator implements Configuration {

	private org.apache.commons.configuration.Configuration delegate;

	public ConfigurationDelegator(org.apache.commons.configuration.Configuration config) {
		this.delegate = config;
	}

	@Override
	public Configuration subset(String prefix) {
		return new ConfigurationDelegator(delegate.subset(prefix));
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public boolean containsKey(String key) {
		return delegate.containsKey(key);
	}

	@Override
	public void addProperty(String key, Object value) {
		delegate.addProperty(key, value);
	}

	@Override
	public void setProperty(String key, Object value) {
		delegate.setProperty(key, value);
	}

	@Override
	public void clearProperty(String key) {
		delegate.clearProperty(key);
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public Object getProperty(String key) {
		return delegate.getProperty(key);
	}

	@Override
	public Iterator<String> getKeys(String prefix) {
		return delegate.getKeys(prefix);
	}

	@Override
	public Iterator<String> getKeys() {
		return delegate.getKeys();
	}

	@Override
	public Properties getProperties(String key) {
		return delegate.getProperties(key);
	}

	@Override
	public boolean getBoolean(String key) {
		return delegate.getBoolean(key);
	}

	@Override
	public boolean getBoolean(String key, boolean defaultValue) {
		return delegate.getBoolean(key, defaultValue);
	}

	@Override
	public Boolean getBoolean(String key, Boolean defaultValue) {
		return delegate.getBoolean(key, defaultValue);
	}

	@Override
	public byte getByte(String key) {
		return delegate.getByte(key);
	}

	@Override
	public byte getByte(String key, byte defaultValue) {
		return delegate.getByte(key, defaultValue);
	}

	@Override
	public Byte getByte(String key, Byte defaultValue) {
		return delegate.getByte(key, defaultValue);
	}

	@Override
	public double getDouble(String key) {
		return delegate.getDouble(key);
	}

	@Override
	public double getDouble(String key, double defaultValue) {
		return delegate.getDouble(key, defaultValue);
	}

	@Override
	public Double getDouble(String key, Double defaultValue) {
		return delegate.getDouble(key, defaultValue);
	}

	@Override
	public float getFloat(String key) {
		return delegate.getFloat(key);
	}

	@Override
	public float getFloat(String key, float defaultValue) {
		return delegate.getFloat(key, defaultValue);
	}

	@Override
	public Float getFloat(String key, Float defaultValue) {
		return delegate.getFloat(key, defaultValue);
	}

	@Override
	public int getInt(String key) {
		return delegate.getInt(key);
	}

	@Override
	public int getInt(String key, int defaultValue) {
		return delegate.getInt(key, defaultValue);
	}

	@Override
	public Integer getInteger(String key, Integer defaultValue) {
		return delegate.getInteger(key, defaultValue);
	}

	@Override
	public long getLong(String key) {
		return delegate.getLong(key);
	}

	@Override
	public long getLong(String key, long defaultValue) {
		return delegate.getLong(key, defaultValue);
	}

	@Override
	public Long getLong(String key, Long defaultValue) {
		return delegate.getLong(key, defaultValue);
	}

	@Override
	public short getShort(String key) {
		return delegate.getShort(key);
	}

	@Override
	public short getShort(String key, short defaultValue) {
		return delegate.getShort(key, defaultValue);
	}

	@Override
	public Short getShort(String key, Short defaultValue) {
		return delegate.getShort(key, defaultValue);
	}

	@Override
	public BigDecimal getBigDecimal(String key) {
		return delegate.getBigDecimal(key);
	}

	@Override
	public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
		return delegate.getBigDecimal(key, defaultValue);
	}

	@Override
	public BigInteger getBigInteger(String key) {
		return delegate.getBigInteger(key);
	}

	@Override
	public BigInteger getBigInteger(String key, BigInteger defaultValue) {
		return delegate.getBigInteger(key, defaultValue);
	}

	@Override
	public String getString(String key) {
		return delegate.getString(key);
	}

	@Override
	public String getString(String key, String defaultValue) {
		return delegate.getString(key, defaultValue);
	}

	@Override
	public String[] getStringArray(String key) {
		return delegate.getStringArray(key);
	}

	@Override
	public List<Object> getList(String key) {
		return delegate.getList(key);
	}

	@Override
	public List<Object> getList(String key, List<?> defaultValue) {
		return delegate.getList(key, defaultValue);
	}
}
