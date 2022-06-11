package lk.express.search.api.v2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.express.ClientDetail;
import lk.express.ConfirmationCriteria;
import lk.express.PassengerDetail;
import lk.express.PersonDetail;
import lk.express.SeatAllocations;
import lk.express.SeatAllocations.Allocation;
import lk.express.bean.Gender;
import lk.express.bean.PassengerType;

/**
 * Booking confirmation request
 *
 * @version v2
 */
public class APIConfirmCriteria extends APICriteria {

	/**
	 * Client details<br>
	 * Client may or may not be a passenger
	 */
	public APIClientDetails client;
	/**
	 * List of passenger details
	 */
	public List<APIPassengerDetails> passengers;
	/**
	 * Seat allocation details
	 */
	public List<APISeatAllocations> seatAllocations;

	/**
	 * @exclude
	 */
	@Override
	public String validate() {
		String error = client.validate();
		if (error != null) {
			return error;
		}
		for (APIPassengerDetails passenger : passengers) {
			error = passenger.validate();
			if (error != null) {
				return error;
			}
		}
		for (APISeatAllocations seatAllocation : seatAllocations) {
			error = seatAllocation.validate();
			if (error != null) {
				return error;
			}
		}
		return null;
	}

	/**
	 * @exclude
	 */
	public ConfirmationCriteria getCriteria() {

		ConfirmationCriteria criteria = new ConfirmationCriteria();
		criteria.setClient(client.getClientDetail());

		List<PassengerDetail> passDetails = new ArrayList<>();
		for (APIPassengerDetails passenger : passengers) {
			PassengerDetail pass = passenger.getPassengerDetail();
			pass.setPassengerType(PassengerType.Adult);
			passDetails.add(pass);
		}
		criteria.setPassengers(passDetails);

		List<SeatAllocations> allocs = new ArrayList<>();
		for (APISeatAllocations seatAllocation : seatAllocations) {
			SeatAllocations alloc = seatAllocation.getSeatAllocations();
			allocs.add(alloc);
		}
		criteria.setItemSeatAllocations(allocs);

		return criteria;
	}

	/**
	 * Generic person details
	 * 
	 * @version v2
	 */
	public static abstract class APIPersonDetails extends APICriteria {

		public String name;
		/**
		 * Accepted values: Male, Female, Other
		 */
		public String gender;
		/**
		 * Timestamp for the DoB
		 */
		public long dob;
		public String nic;
		public String mobile;
		public String email;

		/**
		 * @exclude
		 */
		protected void fillPersonDetails(PersonDetail details) {
			details.setFirstName(name);
			if (gender != null) {
				details.setGender(Gender.valueOf(gender));
			}
			if (dob > 0) {
				details.setDob(new Date(dob));
			}
			details.setNic(nic);
			details.setMobile(mobile);
			details.setEmail(email);
		}
	}

	/**
	 * Client details
	 * 
	 * @version v2
	 */
	public static class APIClientDetails extends APIPersonDetails {

		/**
		 * @exclude
		 */
		@Override
		public String validate() {
			if (name == null || name.isEmpty()) {
				return "Client [name] cannot be empty";
			}
			if (nic == null || nic.isEmpty()) {
				return "Client [nic] cannot be empty";
			}
			if (mobile == null || mobile.isEmpty()) {
				return "Client [mobile] cannot be empty";
			}
			return null;
		}

		/**
		 * @exclude
		 */
		public ClientDetail getClientDetail() {
			ClientDetail details = new ClientDetail();
			fillPersonDetails(details);
			return details;
		}
	}

	/**
	 * Passenger details
	 * 
	 * @version v2
	 */
	public static class APIPassengerDetails extends APIPersonDetails {

		/**
		 * 0 based index of the passenger
		 */
		public int index;

		/**
		 * @exclude
		 */
		public PassengerDetail getPassengerDetail() {
			PassengerDetail details = new PassengerDetail();
			details.setIndex(index);
			fillPersonDetails(details);
			return details;
		}
	}

	/**
	 * Seat allocations for a sector
	 * 
	 * @version v2
	 */
	public static class APISeatAllocations extends APICriteria {

		/**
		 * Acknowledgement ID of the hold result for the sector
		 */
		public int heldItemId;
		/**
		 * Seat allocations for the sector
		 */
		public List<APIAllocation> allocations;

		/**
		 * @exclude
		 */
		@Override
		public String validate() {
			if (heldItemId <= 0) {
				return "Wrong [heldItemId] value";
			}
			String error;
			for (APIAllocation allocation : allocations) {
				error = allocation.validate();
				if (error != null) {
					return error;
				}
			}
			return null;
		}

		/**
		 * @exclude
		 */
		public SeatAllocations getSeatAllocations() {
			SeatAllocations seatAllocs = new SeatAllocations();
			seatAllocs.setHeldItemId(heldItemId);
			List<Allocation> allocs = new ArrayList<>();
			for (APIAllocation allocation : allocations) {
				allocs.add(allocation.getAllocation());
			}
			seatAllocs.setAllocations(allocs);
			return seatAllocs;
		}
	}

	/**
	 * Seat allocation
	 * 
	 * @version v2
	 */
	public static class APIAllocation extends APICriteria {

		/**
		 * Seat number
		 */
		public String seatNumber;
		/**
		 * Passenger index
		 */
		public int passengerIndex;

		/**
		 * @exclude
		 */
		@Override
		public String validate() {
			if (seatNumber == null || seatNumber.isEmpty()) {
				return "Client [seatNumber] cannot be empty";
			}
			if (passengerIndex < 0) {
				return "Wrong [passengerIndex] value";
			}
			return null;
		}

		/**
		 * @exclude
		 */
		public Allocation getAllocation() {
			Allocation allocation = new Allocation();
			allocation.setSeatNumber(seatNumber);
			allocation.setPassengerIndex(passengerIndex);
			return allocation;
		}
	}
}
