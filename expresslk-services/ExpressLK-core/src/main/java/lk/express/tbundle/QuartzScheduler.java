package lk.express.tbundle;

import java.util.Date;
import java.util.Map;

import lk.express.tbundle.ScheduleEntry.Interval;

import org.quartz.Job;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerKey;
import org.quartz.UnableToInterruptJobException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuartzScheduler implements Scheduler {

	private static final Logger logger = LoggerFactory.getLogger(QuartzScheduler.class);

	private org.quartz.Scheduler scheduler;

	public QuartzScheduler() {
		SchedulerFactory sfac = new StdSchedulerFactory();
		try {
			scheduler = sfac.getScheduler();
			scheduler.start();
		} catch (org.quartz.SchedulerException e) {
			logger.error("Exception while creating the schedular", e);
		}
	}

	private String getDescription(ScheduleEntry entry) {
		return entry.getName() + "(" + entry.getGroupName() + ")";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.tbundle.Scheduler#scheduleJob(java.lang.String,
	 * java.lang.String, java.lang.Class, java.util.Map, java.util.Date,
	 * java.util.Date, lk.express.tbundle.QuartzScheduleEntry.Interval, int)
	 */
	@Override
	public ScheduleEntry scheduleJob(String name, String groupName, Class<?> jobClass, Map<String, Object> params,
			Date startTime, Date endTime, Interval durationType, int repeatInterval) {

		if (!Job.class.isAssignableFrom(jobClass)) {
			throw new IllegalArgumentException("Job class must implement the Job interface.");
		}

		@SuppressWarnings("unchecked")
		QuartzScheduleEntry entry = new QuartzScheduleEntry(name, groupName, (Class<? extends Job>) jobClass, params,
				startTime, endTime, durationType, repeatInterval);
		try {
			scheduler.scheduleJob(entry.getJob(), entry.getTrigger());
		} catch (org.quartz.SchedulerException e) {
			logger.error("Exception while adding new job " + getDescription(entry), e);
		}
		return entry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.tbundle.Scheduler#scheduleJob(java.lang.String,
	 * java.lang.String, java.lang.Class, java.util.Map, java.util.Date)
	 */
	@Override
	public ScheduleEntry scheduleJob(String name, String groupName, Class<?> jobClass, Map<String, Object> params,
			Date at) {

		if (!Job.class.isAssignableFrom(jobClass)) {
			throw new IllegalArgumentException("Job class must implement the Job interface.");
		}

		@SuppressWarnings("unchecked")
		QuartzScheduleEntry entry = new QuartzScheduleEntry(name, groupName, (Class<? extends Job>) jobClass, params,
				at);
		try {
			scheduler.scheduleJob(entry.getJob(), entry.getTrigger());
		} catch (org.quartz.SchedulerException e) {
			logger.error("Exception while adding new job " + getDescription(entry), e);
		}
		return entry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lk.express.tbundle.Scheduler#unscheduleJob(lk.express.tbundle.ScheduleEntry
	 * )
	 */
	@Override
	public boolean unscheduleJob(ScheduleEntry entry) throws SchedulerException {
		QuartzScheduleEntry qEntry = (QuartzScheduleEntry) entry;
		try {
			return scheduler.unscheduleJob(qEntry.getTrigger().getKey());
		} catch (org.quartz.SchedulerException e) {
			logger.error("Exception while unscheduling job " + getDescription(entry), e);
			throw new SchedulerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lk.express.tbundle.Scheduler#interupt(lk.express.tbundle.ScheduleEntry)
	 */
	@Override
	public boolean interupt(ScheduleEntry entry) throws SchedulerException {
		QuartzScheduleEntry qEntry = (QuartzScheduleEntry) entry;
		try {
			return scheduler.interrupt(qEntry.getJob().getKey());
		} catch (UnableToInterruptJobException e) {
			logger.error("Exception while interupting job " + getDescription(entry), e);
			throw new SchedulerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lk.express.tbundle.Scheduler#trigger(lk.express.tbundle.ScheduleEntry)
	 */
	@Override
	public void trigger(ScheduleEntry entry) throws SchedulerException {
		QuartzScheduleEntry qEntry = (QuartzScheduleEntry) entry;
		try {
			scheduler.triggerJob(qEntry.getJob().getKey());
		} catch (org.quartz.SchedulerException e) {
			logger.error("Exception while pausing job " + getDescription(entry), e);
			throw new SchedulerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.tbundle.Scheduler#pause(lk.express.tbundle.ScheduleEntry)
	 */
	@Override
	public void pause(ScheduleEntry entry) throws SchedulerException {
		QuartzScheduleEntry qEntry = (QuartzScheduleEntry) entry;
		try {
			scheduler.pauseTrigger(qEntry.getTrigger().getKey());
		} catch (org.quartz.SchedulerException e) {
			logger.error("Exception while pausing job " + getDescription(entry), e);
			throw new SchedulerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.tbundle.Scheduler#pause(java.lang.String)
	 */
	@Override
	public void pause(String group) throws SchedulerException {
		try {
			GroupMatcher<TriggerKey> matcher = GroupMatcher.triggerGroupEquals(group);
			scheduler.pauseTriggers(matcher);
		} catch (org.quartz.SchedulerException e) {
			logger.error("Exception while pausing job group " + group, e);
			throw new SchedulerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.tbundle.Scheduler#pauseAll()
	 */
	@Override
	public void pauseAll() throws SchedulerException {
		try {
			scheduler.pauseAll();
		} catch (org.quartz.SchedulerException e) {
			logger.error("Exception while pausing all jobs", e);
			throw new SchedulerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lk.express.tbundle.Scheduler#resume(lk.express.tbundle.ScheduleEntry)
	 */
	@Override
	public void resume(ScheduleEntry entry) throws SchedulerException {
		QuartzScheduleEntry qEntry = (QuartzScheduleEntry) entry;
		try {
			scheduler.resumeTrigger(qEntry.getTrigger().getKey());
		} catch (org.quartz.SchedulerException e) {
			logger.error("Exception while resuming job " + getDescription(entry), e);
			throw new SchedulerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.tbundle.Scheduler#resume(java.lang.String)
	 */
	@Override
	public void resume(String group) throws SchedulerException {
		try {
			GroupMatcher<TriggerKey> matcher = GroupMatcher.triggerGroupEquals(group);
			scheduler.resumeTriggers(matcher);
		} catch (org.quartz.SchedulerException e) {
			logger.error("Exception while pausing job group " + group, e);
			throw new SchedulerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.tbundle.Scheduler#resumeAll()
	 */
	@Override
	public void resumeAll() throws SchedulerException {
		try {
			scheduler.resumeAll();
		} catch (org.quartz.SchedulerException e) {
			logger.error("Exception while resuming all jobs", e);
			throw new SchedulerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.tbundle.Scheduler#shutdown()
	 */
	@Override
	public void shutdown() {
		try {
			scheduler.shutdown();
		} catch (org.quartz.SchedulerException e) {
			logger.error("Exception while shutting down the schedular", e);
		}
	}
}
