package lk.express.reservation.listener;

import lk.express.notification.NotificationCriteria;
import lk.express.notification.NotificationCriteria.NotificationType;
import lk.express.notification.NotificationGateway;
import lk.express.notification.NotificationMessage;
import lk.express.reservation.Booking;

/**
 * @author dilantha
 * 
 */
public class BookingChangeNotifier implements BookingChangeListener {

	@Override
	public void onChange(BookingChangeEvent event) {
		Booking booking = event.getBooking();
		NotificationMessage msg = BookingNotifyMessageFactory.getMessage(event.getType(), booking);

		NotificationCriteria criteria = new NotificationCriteria();
		criteria.setMessage(msg);
		criteria.getTypes().add(NotificationType.EMAIL);
		criteria.getTypes().add(NotificationType.SMS);

		NotificationGateway.getInstance().sendNotification(criteria);
	}

}
