package lk.express.search.util;

import lk.express.reservation.Booking;
import lk.express.util.Util;

public class SearchUtil {

	/**
	 * Generates a 6 character alpha numeric unique booking reference.
	 * 
	 * @return a unique booking reference
	 */
	public static String generateUniqueBookingReference() {
		return Util.generateUniqueReference(Booking.class, "reference");
	}
}
