package lk.express.admin.service.rest;

import static javax.ws.rs.core.Response.Status.CONFLICT;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import lk.express.bean.Entity;
import lk.express.db.HibernateUtil;
import lk.express.reservation.BookingItem;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/admin/bookingItem")
public class BookingItemService extends EntityService<BookingItem> {

	private static final Logger logger = LoggerFactory.getLogger(BookingItemService.class);

	public BookingItemService() {
		super(BookingItem.class);
	}

	@Override
	protected <T extends Entity> Response handleConstraintViolationExceptionOnPut(ConstraintViolationException e, T t) {
		String msg = e.getCause().getMessage();
		if (msg.contains("booking_item_ibfk_3") || msg.contains("booking_item_ibfk_4")) {
			logger.debug(
					"Constraint violation exception while inserting/updating entity: Selected bus stop is not on the route!",
					e);
			HibernateUtil.rollback();
			return Response.status(CONFLICT).entity(new RestErrorResponse("Selected bus stop is not on the route!"))
					.build();
		}
		return super.handleConstraintViolationExceptionOnDelete(e, t);
	}
}
