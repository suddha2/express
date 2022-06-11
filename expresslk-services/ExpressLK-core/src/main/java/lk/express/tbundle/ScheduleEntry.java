package lk.express.tbundle;

import java.util.Date;

public interface ScheduleEntry {

	public enum Interval {
		MILLISECOND, SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, YEAR;

		public static Interval fromValue(String v) {
			return valueOf(v);
		}
	};

	/**
	 * @return the name
	 */
	String getName();

	/**
	 * @return the groupName
	 */
	String getGroupName();

	/**
	 * @return the jobClass
	 */
	Class<?> getJobClass();

	/**
	 * @return the startTime
	 */
	Date getStartTime();

	/**
	 * @return the endTime
	 */
	Date getEndTime();

	/**
	 * @return the durationType
	 */
	Interval getDurationType();

	/**
	 * @return the duration
	 */
	int getDuration();

	/**
	 * @return whether the job has more invocations
	 */
	boolean isActive();
}