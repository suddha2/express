package lk.express.cnx;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.express.Context;
import lk.express.accounting.AccountingException;
import lk.express.accounting.AccountingManager;
import lk.express.accounting.CompoundJournalEntry;
import lk.express.accounting.IJournalEntry;
import lk.express.accounting.JournalEntryFragment;
import lk.express.accounting.JournalEntryFragment.CrDr;
import lk.express.accounting.SingleJournalEntry;
import lk.express.admin.User;
import lk.express.db.dao.DAOFactory;
import lk.express.db.dao.GenericDAO;
import lk.express.db.dao.HasCodeDAO;
import lk.express.reservation.Booking;
import lk.express.reservation.BookingItem;
import lk.express.reservation.BookingItemPassenger;
import lk.express.reservation.BookingStatus;
import lk.express.reservation.Change;
import lk.express.reservation.ChangeType;
import lk.express.rule.IRule;
import lk.express.rule.Scheme;
import lk.express.rule.bean.RuleCancellationRule;

public class CancellationManager {

	private static final DAOFactory daoFac = DAOFactory.instance(DAOFactory.HIBERNATE);

	private final GenericDAO<Booking> bookingDAO = daoFac.getBookingDAO();
	private final HasCodeDAO<BookingStatus> bookingStatusDAO = daoFac.getBookingStatusDAO();
	private final HasCodeDAO<ChangeType> changeTypeDAO = daoFac.getChangeTypeDAO();

	private Scheme<BookingCancellationWrapper> scheme;

	public CancellationManager(Scheme<BookingCancellationWrapper> scheme) {
		this.scheme = scheme;
	}

	public CancellationManager(List<RuleCancellationRule> rules) {
		this.scheme = new Scheme<BookingCancellationWrapper>("CANCELLATION", getRules(rules));
	}

	protected List<IRule<BookingCancellationWrapper>> getRules(List<RuleCancellationRule> rules) {
		List<IRule<BookingCancellationWrapper>> iRules = new ArrayList<IRule<BookingCancellationWrapper>>();
		for (RuleCancellationRule rule : rules) {
			IRule<BookingCancellationWrapper> iRule = new CancellationRule(rule);
			iRules.add(iRule);
		}
		return iRules;
	}

	public List<BookingItemCancellation> calculateCancellationCharge(CancellationCriteria criteria) {
		Booking booking = bookingDAO.get(criteria.getBookingId());
		if (booking != null) {
			List<ItemCancellationCriteria> itemCriterias = criteria.getItemCriteria();
			List<BookingItemCancellation> charges = new ArrayList<BookingItemCancellation>();
			if (itemCriterias != null && !itemCriterias.isEmpty()) {
				// item cancellation
				for (ItemCancellationCriteria itemCriteria : itemCriterias) {
					for (BookingItem item : booking.getBookingItems()) {
						if (item.isActive() && item.getId() == itemCriteria.getBookingItemId()) {
							charges.addAll(calculateCancellationCharge(criteria, item));
							break;
						}
					}
				}
			} else {
				// item cancellation
				for (BookingItem item : booking.getBookingItems()) {
					if (item.isActive()) {
						charges.addAll(calculateCancellationCharge(criteria, item));
					}
				}
				// booking cancellation
				charges.addAll(calculateCancellationCharge(criteria, booking));
			}
			return charges;
		}
		return null;
	}

	private List<BookingItemCancellation> calculateCancellationCharge(CancellationCriteria criteria, Booking booking) {
		BookingCancellationWrapper wrapper = new BookingCancellationWrapper(criteria, booking);
		scheme.apply(wrapper);
		return wrapper.getCharges();
	}

	private List<BookingItemCancellation> calculateCancellationCharge(CancellationCriteria criteria, BookingItem item) {
		BookingItemCancellationWrapper wrapper = new BookingItemCancellationWrapper(criteria, item);
		scheme.apply(wrapper);
		return wrapper.getCharges();
	}

	public Booking cancel(CancellationCriteria criteria) throws AccountingException {
		Booking booking = bookingDAO.get(criteria.getBookingId());
		if (booking != null) {
			List<ItemCancellationCriteria> itemCriterias = criteria.getItemCriteria();
			if (itemCriterias != null && !itemCriterias.isEmpty()) {
				// item cancellation
				for (ItemCancellationCriteria itemCriteria : itemCriterias) {
					for (BookingItem item : booking.getBookingItems()) {
						if (item.isActive() && item.getId() == itemCriteria.getBookingItemId()) {
							cancelBookingItem(criteria, item);
							break;
						}
					}
				}
			} else {
				// item cancellation
				for (BookingItem item : booking.getBookingItems()) {
					if (item.isActive()) {
						cancelBookingItem(criteria, item);
					}
				}
				// booking cancellation
				cancelBooking(criteria, booking);
			}
			return bookingDAO.persist(booking);
		}
		return null;
	}

	/**
	 * Reverse the cancellation of a booking. Should be done only after ensuring
	 * that all the items are available.
	 * 
	 * @param booking
	 * @param remark
	 * @throws AccountingException
	 */
	public void reopenBooking(Booking booking, String newStatusCode, String remark) throws AccountingException {

		User user = Context.getSessionData().getUser();
		Integer userId = null;
		if (user != null) {
			userId = user.getId();
		}

		booking.setStatus(bookingStatusDAO.get(newStatusCode));
		booking.setCancellationCause(null);

		Change change = new Change();
		change.setBookingId(booking.getId());
		change.setChangeTime(new Date());
		change.setType(changeTypeDAO.get(ChangeType.REOPEN));
		change.setDescription(remark);
		change.setUserId(userId);
		booking.getChanges().add(change);

		for (BookingItem item : booking.getBookingItems()) {
			item.setStatus(bookingStatusDAO.get(newStatusCode));
			item.setCancellationCause(null);

			// accounting entries
			String supplierAccount = item.getSchedule().getBus().getSupplier().getAccountName();
			if (booking.isAccountable()) {
				IJournalEntry invInEntry = new SingleJournalEntry("Cost of seat for booking item " + item.getCode(),
						IJournalEntry.CoS, supplierAccount, item.getCost());
				AccountingManager.getInstance().record(invInEntry);

				List<JournalEntryFragment> fragments = new ArrayList<>();
				fragments.add(new JournalEntryFragment(IJournalEntry.RECEIVABLE, CrDr.Dr, item.getPriceBeforeCharge(),
						"Receivable for booking item " + item.getCode()));
				fragments.add(new JournalEntryFragment(IJournalEntry.REVENUE, CrDr.Cr, item.getPriceBeforeTax(),
						"Revenue for booking item " + item.getCode()));
				if (item.getPriceBeforeCharge() > item.getPriceBeforeTax()) {
					fragments.add(new JournalEntryFragment(IJournalEntry.SALES_TAX_PAYABLE, CrDr.Cr, item
							.getPriceBeforeCharge() - item.getPriceBeforeTax(), "Sales tax for booking item "
							+ item.getCode()));
				}
				IJournalEntry revenueEntry = new CompoundJournalEntry("Booking item " + item.getCode(),
						fragments.toArray(new JournalEntryFragment[0]));
				AccountingManager.getInstance().record(revenueEntry);

			} else {
				double commission = item.getPriceBeforeCharge() - item.getCost();
				if (commission > 0) {
					IJournalEntry commEntry = new SingleJournalEntry("Commission for booking item " + item.getCode(),
							supplierAccount, IJournalEntry.COMMISSION_REVENUE, commission);
					AccountingManager.getInstance().record(commEntry);
				}
			}

			Change itemChange = new Change();
			itemChange.setBookingId(booking.getId());
			itemChange.setBookingItemId(item.getId());
			itemChange.setChangeTime(new Date());
			itemChange.setType(changeTypeDAO.get(ChangeType.REOPEN));
			itemChange.setDescription(remark);
			change.setUserId(userId);
			item.getChanges().add(itemChange);
		}

		bookingDAO.persist(booking);
	}

	private void cancelBookingItem(CancellationCriteria criteria, BookingItem item) throws AccountingException {
		BookingStatus cancelled = bookingStatusDAO.get(BookingStatus.CANCELLED);

		if (criteria.isChargeCancellationFee()) {
			List<BookingItemCancellation> charges = calculateCancellationCharge(criteria, item);
			double cancellationCharge = 0;
			for (BookingItemCancellation charge : charges) {
				BookingItemCancellation cancellation = new BookingItemCancellation();
				cancellation.setBookingId(item.getBookingId());
				cancellation.setBookingItemId(item.getId());
				cancellation.setCancellationSchemeId(charge.getCancellationSchemeId());
				cancellation.setAmount(charge.getAmount());
				item.getCancellations().add(cancellation);

				cancellationCharge += charge.getAmount();
			}
			if (item.getBooking().isAccountable() && cancellationCharge > 0) {
				IJournalEntry cancellationChargeEntry = new SingleJournalEntry("Cancellation charge for item "
						+ item.getCode(), IJournalEntry.RECEIVABLE, IJournalEntry.CANC_CHARGE_REVENUE,
						cancellationCharge);
				AccountingManager.getInstance().record(cancellationChargeEntry);
			}
		}

		// reversing accounting entries
		String supplierAccount = item.getSchedule().getBus().getSupplier().getAccountName();
		if (item.getBooking().isAccountable()) {
			IJournalEntry invInEntry = new SingleJournalEntry("Cancellation of booking item " + item.getCode(),
					supplierAccount, IJournalEntry.CoS, item.getCost());
			AccountingManager.getInstance().record(invInEntry);

			List<JournalEntryFragment> fragments = new ArrayList<>();
			fragments.add(new JournalEntryFragment(IJournalEntry.RECEIVABLE, CrDr.Cr, item.getPriceBeforeCharge(),
					"Cancellation of booking item " + item.getCode()));
			fragments.add(new JournalEntryFragment(IJournalEntry.REVENUE, CrDr.Dr, item.getPriceBeforeTax(),
					"Cancellation of booking item " + item.getCode()));
			if (item.getPriceBeforeCharge() > item.getPriceBeforeTax()) {
				fragments.add(new JournalEntryFragment(IJournalEntry.SALES_TAX_PAYABLE, CrDr.Dr, item
						.getPriceBeforeCharge() - item.getPriceBeforeTax(), "Cancellation of booking item "
						+ item.getCode()));
			}
			IJournalEntry revenueEntry = new CompoundJournalEntry("Cancellation of booking item " + item.getCode(),
					fragments.toArray(new JournalEntryFragment[0]));
			AccountingManager.getInstance().record(revenueEntry);

		} else {
			double commission = item.getPriceBeforeCharge() - item.getCost();
			if (commission > 0) {
				IJournalEntry commEntry = new SingleJournalEntry(
						"Revert commission for booking item " + item.getCode(), IJournalEntry.COMMISSION_REVENUE,
						supplierAccount, commission);
				AccountingManager.getInstance().record(commEntry);
			}
		}

		Change change = generateChange(item.getBookingId(), criteria.getRemark());
		change.setBookingItemId(item.getId());
		item.getChanges().add(change);
		item.setStatus(cancelled);
		item.setCancellationCause(criteria.getCause());

		for (BookingItemPassenger passenger : item.getPassengers()) {
			passenger.setStatus(cancelled);
		}
	}

	private void cancelBooking(CancellationCriteria criteria, Booking booking) throws AccountingException {

		if (criteria.isChargeCancellationFee()) {
			List<BookingItemCancellation> charges = calculateCancellationCharge(criteria, booking);
			double cancellationCharge = 0;
			for (BookingItemCancellation charge : charges) {
				BookingItemCancellation cancellation = new BookingItemCancellation();
				cancellation.setBookingId(booking.getId());
				cancellation.setBookingItemId(null);
				cancellation.setCancellationSchemeId(charge.getCancellationSchemeId());
				cancellation.setAmount(charge.getAmount());
				booking.getCancellations().add(cancellation);

				cancellationCharge += charge.getAmount();
			}
			if (booking.isAccountable() && cancellationCharge > 0) {
				IJournalEntry cancellationChargeEntry = new SingleJournalEntry("Cancellation charge for booking "
						+ booking.getReference(), IJournalEntry.RECEIVABLE, IJournalEntry.CANC_CHARGE_REVENUE,
						cancellationCharge);
				AccountingManager.getInstance().record(cancellationChargeEntry);
			}
		}

		Change change = generateChange(booking.getId(), criteria.getRemark());
		booking.getChanges().add(change);

		booking.setStatus(bookingStatusDAO.get(BookingStatus.CANCELLED));
		booking.setCancellationCause(criteria.getCause());
	}

	private Change generateChange(int bookingId, String remark) {
		Change change = new Change();
		change.setBookingId(bookingId);
		change.setChangeTime(new Date());
		change.setType(changeTypeDAO.get(ChangeType.CANCEL));
		change.setDescription(remark);

		User user = Context.getSessionData().getUser();
		if (user != null) {
			change.setUserId(user.getId());
		}

		return change;
	}
}
