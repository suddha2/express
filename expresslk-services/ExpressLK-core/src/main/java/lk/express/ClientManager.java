package lk.express;

import java.util.List;

import lk.express.db.HibernateUtil;
import lk.express.reservation.Booking;

import org.hibernate.Query;
import org.hibernate.Session;

public class ClientManager {

	public static List<Booking> getRecentBookings(Integer clientId, int position, int pageSize) {

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.createQuery("FROM Booking booking WHERE booking.client.id = :clientId")
				.setParameter("clientId", clientId).setFirstResult(position).setMaxResults(pageSize);
		@SuppressWarnings("unchecked")
		List<Booking> bookings = query.list();
		return bookings;
	}
}
