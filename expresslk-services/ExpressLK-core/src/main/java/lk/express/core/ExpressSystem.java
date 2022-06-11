package lk.express.core;

import lk.express.meta.TaskBundleMetadata;
import lk.express.tbundle.TaskBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpressSystem {

	private static final Logger logger = LoggerFactory.getLogger(ExpressSystem.class);

	private static ExpressSystem system;

	private TaskBundle taskBundle;

	public synchronized static ExpressSystem getSystem() {
		if (system == null) {
			try {
				system = new ExpressSystem();
				system.initializeSelf();
			} catch (Exception e) {
				logger.error("", e);
			} catch (Error e) {
				e.printStackTrace();
			}
		}
		return system;
	}

	private void initializeSelf() {
		TaskBundleMetadata tbMetadata = (TaskBundleMetadata) MetadataManager
				.loadFromFile("files/metadata/taskBundle.xml");
		taskBundle = new TaskBundle(tbMetadata);
	}

	public void shutdown() {
		taskBundle.shutdown();
	}

	/**
	 * @return the taskBundle
	 */
	public TaskBundle getTaskBundle() {
		return taskBundle;
	}
}
