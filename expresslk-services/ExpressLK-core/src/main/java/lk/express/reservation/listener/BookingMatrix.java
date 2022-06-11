package lk.express.reservation.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.express.reservation.listener.BookingChangeEvent.BookingChangeType;

/**
 * In retrospect , movie "The matrix" :-)
 * 
 * @author dilantha
 * 
 */
public class BookingMatrix {

	private static BookingMatrix instance;
	public static Map<BookingChangeType, List<BookingChangeListener>> listenerMap = new HashMap<BookingChangeType, List<BookingChangeListener>>();

	private BookingMatrix() {

	}

	public static BookingMatrix getInstance() {
		if (instance == null) {
			instance = new BookingMatrix();
		}

		return instance;
	}

	public void addBookingChangeListener(BookingChangeType type, BookingChangeListener listner) {
		List<BookingChangeListener> listeners = listenerMap.get(type);
		if (listeners == null) {
			listeners = new ArrayList<BookingChangeListener>();
			listenerMap.put(type, listeners);
		}
		listeners.add(listner);
	}

	public void removeSchduleChangeListener(BookingChangeType type, BookingChangeListener listner) {
		List<BookingChangeListener> listeners = listenerMap.get(type);
		if (listeners != null) {
			listeners.remove(listner);
		}
	}

	public void fireScheduleChangeEvent(BookingChangeEvent e) {
		BookingChangeType type = e.getType();
		List<BookingChangeListener> listeners = listenerMap.get(type);
		if (listeners != null) {
			for (BookingChangeListener listener : listeners) {
				listener.onChange(e);
			}
		}
	}
}
