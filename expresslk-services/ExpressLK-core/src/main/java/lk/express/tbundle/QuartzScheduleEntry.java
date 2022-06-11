package lk.express.tbundle;

import static org.quartz.CalendarIntervalScheduleBuilder.calendarIntervalSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;
import java.util.Map;

import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Trigger;

public class QuartzScheduleEntry implements ScheduleEntry {

	private String name;
	private String groupName;
	private Class<? extends Job> jobClass;
	private Date startTime;
	private Date endTime;
	private Interval durationType;
	private int duration;

	private Trigger trigger;
	private JobDetail job;

	/**
	 * 
	 * @param name
	 * @param groupName
	 * @param jobClass
	 * @param params
	 * @param startTime
	 * @param endTime
	 * @param durationType
	 * @param repeatInterval
	 */
	public QuartzScheduleEntry(String name, String groupName, Class<? extends Job> jobClass,
			Map<String, Object> params, Date startTime, Date endTime, Interval durationType, int repeatInterval) {
		this.name = name;
		this.groupName = groupName;
		this.jobClass = jobClass;
		this.startTime = startTime;
		this.endTime = endTime;
		this.durationType = durationType;

		IntervalUnit intervalUnit = getIntervalUnit(durationType);
		trigger = newTrigger().withIdentity(name, groupName)
				.withSchedule(calendarIntervalSchedule().withInterval(repeatInterval, intervalUnit)).startAt(startTime)
				.endAt(endTime).build();
		job = newJob(jobClass).withIdentity(name, groupName).build();
		job.getJobDataMap().putAll(params);
	}

	/**
	 * 
	 * @param name
	 * @param groupName
	 * @param jobClass
	 * @param params
	 * @param at
	 */
	public QuartzScheduleEntry(String name, String groupName, Class<? extends Job> jobClass,
			Map<String, Object> params, Date at) {
		this.name = name;
		this.groupName = groupName;
		this.jobClass = jobClass;
		this.startTime = at;

		trigger = newTrigger().withIdentity(name, groupName).startAt(at).build();
		job = newJob(jobClass).withIdentity(name, groupName).build();
		job.getJobDataMap().putAll(params);
	}

	/**
	 * 
	 * @param durationType
	 * @return
	 */
	private IntervalUnit getIntervalUnit(Interval durationType) {
		IntervalUnit ret = null;
		switch (durationType) {
		case MILLISECOND:
			ret = IntervalUnit.MILLISECOND;
			break;
		case SECOND:
			ret = IntervalUnit.SECOND;
			break;
		case MINUTE:
			ret = IntervalUnit.MINUTE;
			break;
		case HOUR:
			ret = IntervalUnit.HOUR;
			break;
		case WEEK:
			ret = IntervalUnit.WEEK;
			break;
		case MONTH:
			ret = IntervalUnit.MONTH;
			break;
		case YEAR:
			ret = IntervalUnit.YEAR;
			break;
		case DAY:
		default: // default is day
			ret = IntervalUnit.DAY;
			break;
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.tbundle.ScheduleEntry#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.tbundle.ScheduleEntry#getGroupName()
	 */
	@Override
	public String getGroupName() {
		return groupName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.tbundle.ScheduleEntry#getJobClass()
	 */
	@Override
	public Class<? extends Job> getJobClass() {
		return jobClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.tbundle.ScheduleEntry#getStartTime()
	 */
	@Override
	public Date getStartTime() {
		return startTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.tbundle.ScheduleEntry#getEndTime()
	 */
	@Override
	public Date getEndTime() {
		return endTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.tbundle.ScheduleEntry#getDurationType()
	 */
	@Override
	public Interval getDurationType() {
		return durationType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.tbundle.ScheduleEntry#getDuration()
	 */
	@Override
	public int getDuration() {
		return duration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lk.express.tbundle.ScheduleEntry#isActive()
	 */
	@Override
	public boolean isActive() {
		return trigger.mayFireAgain();
	}

	/**
	 * @return the trigger
	 */
	public Trigger getTrigger() {
		return trigger;
	}

	/**
	 * @return the job
	 */
	public JobDetail getJob() {
		return job;
	}
}
