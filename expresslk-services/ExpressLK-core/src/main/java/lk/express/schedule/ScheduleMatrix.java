package lk.express.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.express.db.dao.DAOFactory;
import lk.express.db.dao.GenericDAO;
import lk.express.schedule.ScheduleChangeEvent.ScheduleChangeType;

public class ScheduleMatrix {

	private static ScheduleMatrix instance;

	private GenericDAO<BusSchedule> scheduleDAO = DAOFactory.instance(DAOFactory.HIBERNATE).getBusScheduleDAO();
	private Map<ScheduleChangeType, List<ScheduleChangeListener>> listenerMap = new HashMap<ScheduleChangeType, List<ScheduleChangeListener>>();

	public static synchronized ScheduleMatrix getInstance() {
		if (instance == null) {
			instance = new ScheduleMatrix();
		}
		return instance;
	}

	public ScheduleManager createScheduleManager(int scheduleId) {
		return new ScheduleManager(scheduleDAO.get(scheduleId));
	}

	public void addSchduleChangeListener(ScheduleChangeType type, ScheduleChangeListener listner) {
		List<ScheduleChangeListener> listeners = listenerMap.get(type);
		if (listeners == null) {
			listeners = new ArrayList<ScheduleChangeListener>();
			listenerMap.put(type, listeners);
		}
		listeners.add(listner);
	}

	public void removeSchduleChangeListener(ScheduleChangeType type, ScheduleChangeListener listner) {
		List<ScheduleChangeListener> listeners = listenerMap.get(type);
		if (listeners != null) {
			listeners.remove(listner);
		}
	}

	// default visibility to limit access
	void fireScheduleChangeEvent(ScheduleChangeEvent e) {
		ScheduleChangeType type = e.getChangeType();
		List<ScheduleChangeListener> listeners = listenerMap.get(type);
		if (listeners != null) {
			for (ScheduleChangeListener listener : listeners) {
				listener.onChange(e);
			}
		}
	}
}
