package lk.express.tbundle;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lk.express.Context;
import lk.express.admin.User;
import lk.express.bean.BusHeldItem;
import lk.express.bean.Coupon;
import lk.express.bean.Payment;
import lk.express.bean.PaymentRefund;
import lk.express.bean.PaymentRefundMode;
import lk.express.cnx.BookingCancellationWrapper;
import lk.express.cnx.CancellationCause;
import lk.express.cnx.CancellationCriteria;
import lk.express.cnx.CancellationManager;
import lk.express.config.Configuration;
import lk.express.config.ConfigurationManager;
import lk.express.db.HibernateUtil;
import lk.express.db.dao.DAOFactory;
import lk.express.db.dao.GenericDAO;
import lk.express.db.dao.HasCodeDAO;
import lk.express.reservation.Booking;
import lk.express.reservation.BookingStatus;
import lk.express.rule.Scheme;

import org.hibernate.criterion.Restrictions;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceReleaser implements Job {

	private static final int DEFAULT_RESOURCE_HELD_DURATION = 10 * 60; // seconds
	private static final String CONFIG_RESOURCE_HELD_DURATION = "RESOURCE_HELD_DURATION";

	private static final Logger logger = LoggerFactory.getLogger(ResourceReleaser.class);
	private static final Configuration config = ConfigurationManager.getConfiguration();
	private static final DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			HibernateUtil.beginTransaction();
			Context.getSessionData().empty().setUsername(User.SYS_USER);

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, -config.getInt(CONFIG_RESOURCE_HELD_DURATION, DEFAULT_RESOURCE_HELD_DURATION));

			@SuppressWarnings("unchecked")
			List<BusHeldItem> heldItems = HibernateUtil.getCurrentSession().createCriteria(BusHeldItem.class)
					.add(Restrictions.lt("timestamp", cal.getTime())).list();

			GenericDAO<BusHeldItem> heldBusItemDAO = daoFac.getHeldBusItemDAO();
			for (BusHeldItem heldItem : heldItems) {
				logger.info("Releasing unconfirmed held item {}", new Object[] { heldItem.getId() });
				heldBusItemDAO.delete(heldItem);
			}

			HasCodeDAO<BookingStatus> bookingStatusDAO = daoFac.getBookingStatusDAO();
			@SuppressWarnings("unchecked")
			List<Booking> tentativeBookings = HibernateUtil.getCurrentSession().createCriteria(Booking.class)
					.add(Restrictions.lt("expiryTime", new Date()))
					.add(Restrictions.eq("status", bookingStatusDAO.get(BookingStatus.TENTATIVE))).list();

			CancellationManager manager = new CancellationManager((Scheme<BookingCancellationWrapper>) null);
			GenericDAO<PaymentRefund> paymentDAO = null;
			GenericDAO<Coupon> couponDAO = null;

			for (Booking tentativeBooking : tentativeBookings) {
				logger.info("Cencelling expired tentative booking {}", new Object[] { tentativeBooking.getId() });

				for (Payment payment : tentativeBooking.getPayments()) {
					if (payment.getMode().equals(PaymentRefundMode.Coupon)) {
						if (paymentDAO == null) {
							paymentDAO = daoFac.getPaymentRefundDAO();
							couponDAO = daoFac.getCouponDAO();
						}
						logger.info("Removing coupon {} from cancelled booking {}",
								new Object[] { payment.getReference(), tentativeBooking.getId() });
						Coupon coupon = couponDAO.findUnique(new Coupon(payment.getReference()));
						coupon.setActive(true);
						couponDAO.persist(coupon);

						paymentDAO.delete(payment);
					}
				}

				CancellationCriteria criteria = new CancellationCriteria();
				criteria.setBookingId(tentativeBooking.getId());
				criteria.setChargeCancellationFee(false);
				criteria.setCause(CancellationCause.NonPayment);
				criteria.setRemark("Cancellation due to payment not being received by expiry time.");

				manager.cancel(criteria);
			}

			HibernateUtil.commit();
		} catch (Exception e) {
			logger.error("Exception while releasing unconfirmed held items and tentative bookings", e);
			HibernateUtil.rollback();
		}
	}
}
