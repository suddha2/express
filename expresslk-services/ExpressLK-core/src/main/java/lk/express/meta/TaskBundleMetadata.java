package lk.express.meta;

import java.util.List;

import lk.express.metaxml.tbundle.TaskBundleType;
import lk.express.metaxml.tbundle.TaskType;

import org.w3c.dom.Document;

public class TaskBundleMetadata extends Metadata {

	private static final String TBUNDLE_PKG = "lk.express.metaxml.tbundle";

	private List<TaskType> tasks;

	public TaskBundleMetadata(Document document) {
		super(document, TBUNDLE_PKG);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.meta.Metadata#initialize()
	 */
	@Override
	protected void initialize() {
		TaskBundleType tbundle = (TaskBundleType) jaxbRootObject;
		tasks = tbundle.getTasks().getTask();
	}

	public List<TaskType> getTasks() {
		return tasks;
	}
}
