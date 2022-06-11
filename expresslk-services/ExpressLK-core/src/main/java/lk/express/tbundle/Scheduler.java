package lk.express.tbundle;

import java.util.Date;
import java.util.Map;

import lk.express.tbundle.ScheduleEntry.Interval;

public interface Scheduler {

	ScheduleEntry scheduleJob(String name, String groupName, Class<?> jobClass, Map<String, Object> params,
			Date startTime, Date endTime, Interval durationType, int repeatInterval);

	ScheduleEntry scheduleJob(String name, String groupName, Class<?> jobClass, Map<String, Object> params, Date at);

	boolean unscheduleJob(ScheduleEntry entry) throws SchedulerException;

	boolean interupt(ScheduleEntry entry) throws SchedulerException;

	void trigger(ScheduleEntry entry) throws SchedulerException;

	void pause(ScheduleEntry entry) throws SchedulerException;

	void pause(String group) throws SchedulerException;

	void pauseAll() throws SchedulerException;

	void resume(ScheduleEntry entry) throws SchedulerException;

	void resume(String group) throws SchedulerException;

	void resumeAll() throws SchedulerException;

	void shutdown();
}