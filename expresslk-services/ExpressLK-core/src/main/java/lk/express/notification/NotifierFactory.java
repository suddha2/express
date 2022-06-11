package lk.express.notification;

import lk.express.notification.NotificationCriteria.NotificationType;
import lk.express.notification.email.EmailNotifier;

public class NotifierFactory {

	public static INotifier getNotifier(NotificationType type) {
		INotifier notifier = null;
		if (NotificationType.EMAIL.equals(type)) {
			notifier = EmailNotifier.getInstance();
		}

		return notifier;
	}

}
