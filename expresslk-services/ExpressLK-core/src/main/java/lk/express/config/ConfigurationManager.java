package lk.express.config;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DatabaseConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.tree.NodeCombiner;
import org.apache.commons.configuration.tree.OverrideCombiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class ConfigurationManager {

	private static final String CONFIG_FILE = "app.conf";
	private static final String JDBC_TABLE = "config";
	private static final String JDBC_KEY = "config";
	private static final String JDBC_VALUE = "value";

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
	private static Configuration config;

	/**
	 * Returns the {@link Configuration} instance which should be used to access
	 * configuration values.
	 * 
	 * @return {@link Configuration} instance
	 */
	public static synchronized Configuration getConfiguration() {
		if (config == null) {
			config = createConfiguration();
		}
		return config;
	}

	private static Configuration createConfiguration() {

		// Create and initialize the node combiner
		NodeCombiner combiner = new OverrideCombiner();
		// Construct the combined configuration
		CombinedConfiguration config = new CombinedConfiguration(combiner);

		try {
			// Properties loaded as system variables have the highest priority
			SystemConfiguration sys = new SystemConfiguration();
			config.addConfiguration(sys);

			// Properties loaded from app.conf have the next priority
			PropertiesConfiguration prop = new PropertiesConfiguration(CONFIG_FILE);
			prop.setReloadingStrategy(new FileChangedReloadingStrategy());
			config.addConfiguration(prop);

			// Properties loaded from database have the lowest priority
			MysqlDataSource dataSource = new MysqlDataSource();
			dataSource.setURL(prop.getString("connection.url"));
			dataSource.setUser(prop.getString("connection.username"));
			dataSource.setPassword(prop.getString("connection.password"));
			DatabaseConfiguration jdbc = new DatabaseConfiguration(dataSource, JDBC_TABLE, JDBC_KEY, JDBC_VALUE);
			config.addConfiguration(jdbc);

		} catch (ConfigurationException e) {
			logger.error("Exception while initializing configurations", e);
			throw new RuntimeException(e);
		}
		return new ConfigurationDelegator(config);
	}
}
