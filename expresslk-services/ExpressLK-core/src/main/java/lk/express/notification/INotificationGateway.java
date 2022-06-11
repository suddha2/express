package lk.express.notification;

/**
 * @author dilantha
 *
 */
public interface INotificationGateway {

	public void sendNotification(NotificationCriteria criterias);

}
