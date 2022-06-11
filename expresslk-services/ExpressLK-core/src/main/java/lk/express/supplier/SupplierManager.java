package lk.express.supplier;

import java.util.Date;
import java.util.List;

import lk.express.db.HibernateUtil;
import lk.express.schedule.BusSchedule;

import org.hibernate.Query;
import org.hibernate.Session;

public class SupplierManager {

	public static List<BusSchedule> getSupplierSchedules(Supplier supplier, Date fromTime, Date toTime) {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session
				.createQuery(
						"FROM BusSchedule schedule WHERE schedule.bus.supplier.id = :supplierId "
								+ "AND schedule.departureTime >= :fromTime AND schedule.departureTime` <= :toTime")
				.setParameter("supplierId", supplier.getId()).setParameter("fromTime", fromTime)
				.setParameter("toTime", toTime);
		@SuppressWarnings("unchecked")
		List<BusSchedule> schedules = query.list();
		return schedules;
	}
}
