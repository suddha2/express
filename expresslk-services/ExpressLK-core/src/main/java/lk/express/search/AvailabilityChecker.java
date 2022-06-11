package lk.express.search;

import java.util.Collection;
import java.util.List;

import lk.express.Reservation;

/**
 * Allows checking availability for a schedule
 */
public interface AvailabilityChecker {

	/**
	 * Returns the list of all seats of the bus
	 * 
	 * @return all seat numbers
	 */
	List<String> getAllSeats();

	/**
	 * Returns the list of seats available for reservation (some may already be
	 * reserved)
	 * 
	 * @return unavailable seat numbers
	 */
	List<String> getAvailableSeats();

	/**
	 * Returns the list of seats unavailable for reservation
	 * 
	 * @return unavailable seat numbers
	 */
	List<String> getUnavailableSeats();

	/**
	 * Returns the list of reserved seats.
	 * 
	 * @return reserved seat numbers
	 */
	List<Reservation> getReservedSeats();

	/**
	 * Returns the list of vacant seat numbers.
	 * 
	 * @param reservedSeats
	 *            List of reserved seats if we already have that, {@code null}
	 *            otherwise
	 * 
	 * @return vacant seat numbers
	 */
	List<String> getVacantSeats(List<String> reservedSeats);

	/**
	 * Returns whether a given seat is vacant
	 * 
	 * @param seatNumbers
	 *            seat numbers
	 * @return whether a given seat is vacant
	 */
	boolean isVacant(List<String> seatNumbers);

	List<String> getSeatNumbers(Collection<Reservation> reservations);

}