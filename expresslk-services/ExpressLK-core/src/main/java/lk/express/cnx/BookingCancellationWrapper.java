package lk.express.cnx;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.express.RuleProperty;
import lk.express.reservation.Booking;
import lk.express.util.CurrencyUtil;

public class BookingCancellationWrapper {

	public static final String CHARGEABLE = "chargeable";
	public static final String IS_BEFORE_DEPARTURE = "isBeforeDeparture";
	public static final String DAYS_BEFORE_DEPARTURE = "daysBeforeDeparture";
	public static final String HOURS_BEFORE_DEPARTURE = "hoursBeforeDeparture";

	// inputs
	protected Booking booking;
	protected CancellationCriteria criteria;
	// outcome
	protected boolean allowed = true;
	protected List<BookingItemCancellation> charges = new ArrayList<BookingItemCancellation>();

	public Booking getBooking() {
		return booking;
	}

	public BookingCancellationWrapper(CancellationCriteria criteria, Booking booking) {
		this.criteria = criteria;
		this.booking = booking;
	}

	protected Date getDepartureTime() {
		return booking.getDepartureTime();
	}

	@RuleProperty(CHARGEABLE)
	public double getChargeable() {
		return booking.getChargeable();
	}

	@RuleProperty(IS_BEFORE_DEPARTURE)
	public boolean isBeforeDeparture() {
		Date now = new Date();
		return now.before(getDepartureTime());
	}

	@RuleProperty(DAYS_BEFORE_DEPARTURE)
	public int getDaysToDeparture() {
		Date dep = getDepartureTime();
		Date now = new Date();
		long diffTime = dep.getTime() - now.getTime();
		long diffDays = diffTime / (1000 * 60 * 60 * 24);
		return (int) diffDays;
	}

	@RuleProperty(HOURS_BEFORE_DEPARTURE)
	public int getHoursToDeparture() {
		Date dep = getDepartureTime();
		Date now = new Date();
		long diffTime = dep.getTime() - now.getTime();
		long diffHours = diffTime / (1000 * 60 * 60);
		return (int) diffHours;
	}

	public boolean isAllowed() {
		return allowed;
	}

	public void setNotAllowed() {
		this.allowed = false;
	}

	public List<BookingItemCancellation> getCharges() {
		return charges;
	}

	public void applyCharge(int ruleId, double amount) {
		BookingItemCancellation charge = new BookingItemCancellation();
		charge.setBookingId(booking.getId());
		charge.setCancellationSchemeId(ruleId);
		charge.setAmount(CurrencyUtil.round(amount));
		charges.add(charge);
	}
}
