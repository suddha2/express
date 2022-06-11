package lk.express.reservation.listener;

import lk.express.notification.NotificationMessage;
import lk.express.reservation.Booking;
import lk.express.reservation.listener.BookingChangeEvent.BookingChangeType;

/**
 * @author dilantha
 *
 */
public class BookingNotifyMessageFactory {

	public static NotificationMessage getMessage(BookingChangeType type, Booking booking) {

		NotificationMessage message = new NotificationMessage();
		if (type.equals(BookingChangeType.BOOKING_CONFIRM)) {
			message.setTitle("Booking Confirmed");
			message.setBody("Booking Confirmed. Ready to go");
		}

		return message;
	}
}
