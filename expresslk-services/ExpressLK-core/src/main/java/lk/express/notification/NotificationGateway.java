package lk.express.notification;

import lk.express.notification.NotificationCriteria.NotificationType;

/**
 * @author dilantha
 * 
 */
public class NotificationGateway implements INotificationGateway {

	private static NotificationGateway instance;

	private NotificationGateway() {

	}

	public static NotificationGateway getInstance() {
		if (instance == null) {
			instance = new NotificationGateway();
		}

		return instance;
	}

	@Override
	public void sendNotification(NotificationCriteria criteria) {

		for (NotificationType type : criteria.getTypes()) {
			NotifierFactory.getNotifier(type).send(criteria);
		}
	}

}
