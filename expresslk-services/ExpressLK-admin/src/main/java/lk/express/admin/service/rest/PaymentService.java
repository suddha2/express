package lk.express.admin.service.rest;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import lk.express.Context;
import lk.express.bean.Entity;
import lk.express.bean.Payment;
import lk.express.db.dao.DAOFactory;
import lk.express.db.dao.GenericDAO;
import lk.express.db.dao.HasCodeDAO;
import lk.express.reservation.Booking;
import lk.express.reservation.BookingItem;
import lk.express.reservation.BookingItemPassenger;
import lk.express.reservation.BookingStatus;

@Path("/admin/payment")
public class PaymentService extends EntityService<Payment> {

	protected static final DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);

	public PaymentService() {
		super(Payment.class);
	}

	@Override
	protected <T extends Entity> void populateEntity(T object) {
		Payment payment = (Payment) object;
		if (payment.getUserId() == null || payment.getUserId() < 0) {
			payment.setUserId(Context.getSessionData().getUser().getId());
		}

		super.populateEntity(object);
	}

	@Override
	protected <T extends Entity> Response insertEntity(T t) {
		Response response = super.insertEntity(t);

		Payment payment = (Payment) t;
		GenericDAO<Booking> bookingDAO = daoFac.getBookingDAO();
		Booking booking = bookingDAO.get(payment.getBookingId());
		triggerPaymentAction(bookingDAO, booking);

		return response;
	}

	/**
	 * 
	 * @param bookingDAO
	 * @param booking
	 * 
	 * @FIXME duplicated code from
	 *        {@link SearchGateway#triggerPaymentAction(GenericDAO, Booking)
	 *        triggerPaymentAction of SearchGateway}
	 */
	protected void triggerPaymentAction(GenericDAO<Booking> bookingDAO, Booking booking) {

		if (booking.getChargeable() <= booking.getTotalPayment()) {
			HasCodeDAO<BookingStatus> bookingStatusDAO = daoFac.getBookingStatusDAO();
			BookingStatus confirmed = bookingStatusDAO.get(BookingStatus.CONFIRMED);
			boolean persist = false;

			if (booking.getStatus().getCode().equals(BookingStatus.TENTATIVE)) {
				booking.setStatus(confirmed);
				persist = true;
			}
			for (BookingItem item : booking.getBookingItems()) {
				if (item.getStatus().getCode().equals(BookingStatus.TENTATIVE)) {
					item.setStatus(confirmed);
					persist = true;
				}
				for (BookingItemPassenger pax : item.getPassengers()) {
					if (pax.getStatus().getCode().equals(BookingStatus.TENTATIVE)) {
						pax.setStatus(confirmed);
						persist = true;
					}
				}
			}

			if (persist) {
				bookingDAO.persist(booking);
			}
		}
	}
}
