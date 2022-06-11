package lk.express.tbundle;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.datatype.XMLGregorianCalendar;

import lk.express.Parameter;
import lk.express.meta.TaskBundleMetadata;
import lk.express.metaxml.params.ParameterType;
import lk.express.metaxml.params.ParametersType;
import lk.express.metaxml.tbundle.OneTimeTaskType;
import lk.express.metaxml.tbundle.RecurringTaskType;
import lk.express.metaxml.tbundle.TaskType;
import lk.express.tbundle.ScheduleEntry.Interval;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskBundle {

	private static final Logger logger = LoggerFactory.getLogger(TaskBundle.class);

	private Scheduler scheduler;
	private Map<String, ScheduleEntry> scheduleEntries = new HashMap<String, ScheduleEntry>();

	public TaskBundle(TaskBundleMetadata metadata) {
		initializeScheduler();
		for (TaskType task : metadata.getTasks()) {
			try {
				String name = task.getName();
				if (name == null || name.isEmpty() || scheduleEntries.containsKey(name)) {
					throw new RuntimeException("Invalid or duplicate task name");
				}
				ScheduleEntry entry = scheduleTask(task);
				scheduleEntries.put(name, entry);
			} catch (Exception e) {
				logger.error("Exception while scheduling task - " + task.getName(), e);
			}
		}
	}

	public void shutdown() {
		scheduler.shutdown();
	}

	public List<ScheduleEntry> getActiveSchedules() {
		return scheduleEntries.values().stream().filter(se -> se.isActive()).collect(Collectors.toList());
	}

	private void initializeScheduler() {
		scheduler = new QuartzScheduler();
	}

	private ScheduleEntry scheduleTask(TaskType task) throws Exception {
		if (task instanceof RecurringTaskType) {
			return scheduleTask((RecurringTaskType) task);
		} else {
			return scheduleTask((OneTimeTaskType) task);
		}
	}

	private ScheduleEntry scheduleTask(RecurringTaskType task) throws Exception {

		Map<String, Object> params = getParams(task.getParams());
		Class<?> jobClass = getClass(task.getJobClass());
		Date startTime = getTime(task.getStartTime());
		Date endTime = getTime(task.getEndTime());
		Interval interval = getInterval(task.getDurationType());

		return scheduler.scheduleJob(task.getName(), task.getGroupName(), jobClass, params, startTime, endTime,
				interval, task.getRepeatInterval());
	}

	private ScheduleEntry scheduleTask(OneTimeTaskType task) throws Exception {
		Map<String, Object> params = getParams(task.getParams());
		Class<?> jobClass = getClass(task.getJobClass());
		Date at = getTime(task.getAt());

		return scheduler.scheduleJob(task.getName(), task.getGroupName(), jobClass, params, at);
	}

	private Map<String, Object> getParams(ParametersType parameters) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		if (parameters != null) {
			for (ParameterType parameter : parameters.getParameter()) {
				Parameter param = new Parameter(parameter);
				params.put(param.getParam(), param.getObject());
			}
		}
		return params;
	}

	private Class<?> getClass(String strClass) throws ClassNotFoundException {
		return getClass().getClassLoader().loadClass(strClass);
	}

	private Date getTime(XMLGregorianCalendar cal) {
		if (cal != null) {
			return cal.toGregorianCalendar().getTime();
		}
		return null;
	}

	private Interval getInterval(lk.express.metaxml.tbundle.Interval durationType) {
		return Interval.fromValue(durationType.name());
	}
}
