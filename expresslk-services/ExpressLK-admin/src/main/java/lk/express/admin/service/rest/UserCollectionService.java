package lk.express.admin.service.rest;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lk.express.CRUD;
import lk.express.auth.rest.RESTAuthenticationHandler;
import lk.express.db.HibernateUtil;
import lk.express.reservation.Booking;
import lk.express.service.RestService;

@Path("/admin/userCollection")
public class UserCollectionService extends RestService {

	private static final Logger logger = LoggerFactory.getLogger(UserCollectionService.class);

	private final RESTAuthenticationHandler authHandler;
	private @Context HttpHeaders httpHeaders;

	public UserCollectionService() {
		authHandler = new RESTAuthenticationHandler(Booking.class);
	}

	@GET
	@Path("/{userId}/{startTime}/{endTime}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getConductorCollection(@PathParam("userId") Integer userId, @PathParam("startTime") Long startTime,
			@PathParam("endTime") Long endTime) {
		if (authHandler.isAllowed(httpHeaders, CRUD.Read)) {
			try {
				Date start = new Date(startTime);
				Date end = new Date(endTime);
				String sql = "SELECT `u`.`username`, `bs`.`departure_time` AS `departureTime`, `bi`.`schedule_id` AS `scheduleId`, "
						+ "    `b`.`reference`, `bi`.`fare`, `b`.`booking_time` AS `bookingTime`,  SUM(`p`.`amount`) AS `totalPaid`, "
						+ "    `p`.`modes` AS `paymentMethods` " + "FROM  `bus_schedule` AS `bs` "
						+ "JOIN `booking_item` AS `bi` ON `bi`.`schedule_id` = `bs`.`id` "
						+ "JOIN `booking` AS `b` ON `b`.`id` = `bi`.`booking_id` "
						+ "JOIN `user` AS `u` ON `u`.`id` = `b`.`user_id` " + "LEFT JOIN ( "
						+ "    SELECT `p`.`booking_id`, SUM(`pr`.`amount`) AS `amount`, GROUP_CONCAT(`pr`.`mode` SEPARATOR ', ') AS `modes` "
						+ "    FROM `payment` AS `p` " + "    JOIN `payment_refund` AS `pr` ON `p`.`id` = `pr`.`id` "
						+ "    GROUP BY `p`.`booking_id`) AS `p` ON `p`.`booking_id` = `bi`.`booking_id` "
						+ "WHERE `bi`.`status_code` = 'CONF' "
						// + "AND `bs`.`departure_time` > :start AND `bs`.`departure_time` < :end "
						+ "AND `b`.`booking_time` >= :start AND `b`.`booking_time` <= :end  "
						+ "AND `u`.`id` = :userId AND `b`.`agent_id` IS NULL "
						+ "GROUP BY `b`.`id`,`b`.`id`,`u`.`username`, `bs`.`departure_time` , `bi`.`schedule_id`, "
						+ "`b`.`reference`, `b`.`chargeable`, `b`.`booking_time` , `p`.`modes`  "
						+ "ORDER BY `bs`.`departure_time` ASC";
				Session session = HibernateUtil.getCurrentSession();
				@SuppressWarnings("unchecked")
				List<UserCollection> collection = session.createSQLQuery(sql).setParameter("start", start)
						.setParameter("end", end).setParameter("userId", userId)
						.setResultTransformer(Transformers.aliasToBean(UserCollection.class)).list();
				return Response.status(OK).entity(collection).build();
			} catch (Exception e) {
				logger.error("Error while calculating user collection for the given user", e);
				HibernateUtil.rollback();
				return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
						.build();
			}
		} else {
			return authHandler.buildErrorResponse(httpHeaders, CRUD.Read);
		}
	}

	/**
	 * This method uses a ROLL UP query to include running total and grand total.
	 * Front end will filter out rows as required.
	 * 
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@GET
	@Path("/print/{userId}/{startTime}/{endTime}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getCollectionDataForPrint(@PathParam("userId") Integer userId,
			@PathParam("startTime") String startTime, @PathParam("endTime") String endTime) {
		if (authHandler.isAllowed(httpHeaders, CRUD.Read)) {
			try {

				DateFormat formatter;
				formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date start = formatter.parse(startTime);
				Date end = formatter.parse(endTime);
				String sql = "SELECT  brt.name AS routeName, bsh.departure_time AS departureTime, s.name AS supplier, CONCAT(c.name, ' - ', c2.name) AS sector, "
						+ " bip.seat_fare AS seatFare, bip.passenger_type AS passengerType, count(bip.seat_number) AS noOfSeats, "
						+ " bip.fare_payment_option  AS paymentOption, SUM(case when bip.fare_payment_option = 'Cash' then seat_fare else 0 end)  AS  collectedFare, "
						+ " sum(bip.price - bip.seat_fare)  AS  bookingFee, b.plate_number AS plateNumber "
						+ " FROM booking bk JOIN booking_item bi ON bk.id = bi.booking_id JOIN booking_item_passenger bip on bi.booking_id=bip.booking_item_id "
						+ " JOIN bus_schedule bsh ON bi.schedule_id = bsh.id JOIN bus_route brt ON bsh.bus_route_id = brt.id JOIN bus b ON bsh.bus_id = b.id "
						+ " JOIN supplier s ON b.supplier_id = s.id JOIN bus_stop bst ON bi.from_bus_stop_id = bst.id "
						+ " JOIN bus_stop bst2 ON bi.to_bus_stop_id = bst2.id JOIN city c ON bst.city_id = c.id JOIN city c2 ON bst2.city_id = c2.id"
						+ " WHERE bk.user_id = :userId AND bk.booking_time BETWEEN :start AND :end AND bk.status_code = 'CONF'"
						+ " group by routeName, departureTime, supplier, sector, seatFare , paymentOption  with ROLLUP";

				// + " ORDER BY departure_time" ;
				Session session = HibernateUtil.getCurrentSession();
				@SuppressWarnings("unchecked")
				List<UserCollectionPrint> collection = session.createSQLQuery(sql).setParameter("start", start)
						.setParameter("end", end).setParameter("userId", userId)
						.setResultTransformer(Transformers.aliasToBean(UserCollectionPrint.class)).list();
				return Response.status(OK).entity(collection).build();
			} catch (Exception e) {
				logger.error("Error while calculating user collection for the given user", e);
				HibernateUtil.rollback();
				return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
						.build();
			}
		} else {
			return authHandler.buildErrorResponse(httpHeaders, CRUD.Read);
		}
	}

	/**
	 * 
	 * 
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@GET
	@Path("/tripcollection/{scheduleID}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getTripCollectionDataForPrint(@PathParam("scheduleID") Integer scheduleID) {
		if (authHandler.isAllowed(httpHeaders, CRUD.Read)) {
			try {

				String sql = "SELECT distinct sector, seatFare, passengerType, noOfSeats, seatNumbers, paymentMode, collectedFare, username "
						+ " FROM ( SELECT  CONCAT(c.name, ' - ', c2.name) AS sector, bip.seat_fare AS seatFare, bip.passenger_type AS passengerType, "
						+ " COUNT(bip.seat_number) AS noOfSeats,GROUP_CONCAT(bip.seat_number ORDER BY cast(bip.seat_number as unsigned)) AS seatNumbers, bip.fare_payment_option AS paymentMode, "
						+ " SUM(CASE WHEN fare_payment_option = 'Cash' THEN seat_fare ELSE 0 END) AS collectedFare , u.id AS userId, u.username  username "
						+ " FROM booking bk JOIN booking_item bi ON bk.id = bi.booking_id JOIN booking_item_passenger bip ON bi.booking_id = bip.booking_item_id  "
						+ " JOIN  bus_schedule bsh ON bi.schedule_id = bsh.id  JOIN bus_route brt ON bsh.bus_route_id = brt.id JOIN bus b ON bsh.bus_id = b.id  "
						+ " JOIN supplier s ON b.supplier_id = s.id JOIN bus_stop bst ON bi.from_bus_stop_id = bst.id "
						+ " JOIN bus_stop bst2 ON bi.to_bus_stop_id = bst2.id JOIN city c ON bst.city_id = c.id  JOIN  city c2 ON bst2.city_id = c2.id "
						+ " JOIN user u ON u.id = bk.user_id WHERE bsh.id = :scheduleID  AND bk.status_code = 'CONF' GROUP BY sector , seatFare , "
						+ " paymentMode, username ORDER BY sector, username, paymentMode, passengerType ) as main  "
						+ " INNER JOIN  user_user_group UG1 ON main.userId = UG1.user_id AND UG1.user_group_id = 110 ";

				Session session = HibernateUtil.getCurrentSession();
				@SuppressWarnings("unchecked")
				List<tripCollection> collection = session.createSQLQuery(sql).setParameter("scheduleID", scheduleID)
						.setResultTransformer(Transformers.aliasToBean(tripCollection.class)).list();
				;
				return Response.status(OK).entity(collection).build();
			} catch (Exception e) {
				logger.error("Error while calculating user collection for the given user", e);
				HibernateUtil.rollback();
				return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
						.build();
			}
		} else {
			return authHandler.buildErrorResponse(httpHeaders, CRUD.Read);
		}
	}

	/**
	 * Lists all booking done by a non SLTB USER for a given schedule. user group
	 * 110 is fixed as SLTB USER. - DO NOT CHANGE -
	 * 
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@GET
	@Path("/tripcollection/web/{scheduleID}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getWebTripCollectionDataForPrint(@PathParam("scheduleID") Integer scheduleID) {
		if (authHandler.isAllowed(httpHeaders, CRUD.Read)) {
			try {
				String sql = "SELECT distinct sector, seatFare, passengerType, noOfSeats, seatNumbers, paymentMode, collectedFare, 'WEB' as username "
						+ " FROM ( SELECT  CONCAT(c.name, ' - ', c2.name) AS sector, bip.seat_fare AS seatFare, bip.passenger_type AS passengerType, "
						+ " COUNT(bip.seat_number) AS noOfSeats,GROUP_CONCAT(bip.seat_number ORDER BY cast(bip.seat_number as unsigned)) AS seatNumbers, bip.fare_payment_option AS paymentMode, "
						+ " SUM( seat_fare  ) AS collectedFare , u.id AS userId, u.username  username "
						+ " FROM booking bk JOIN booking_item bi ON bk.id = bi.booking_id JOIN booking_item_passenger bip ON bi.id = bip.booking_item_id  "
						+ " JOIN  bus_schedule bsh ON bi.schedule_id = bsh.id  JOIN bus_route brt ON bsh.bus_route_id = brt.id JOIN bus b ON bsh.bus_id = b.id  "
						+ " JOIN supplier s ON b.supplier_id = s.id JOIN bus_stop bst ON bi.from_bus_stop_id = bst.id "
						+ " JOIN bus_stop bst2 ON bi.to_bus_stop_id = bst2.id JOIN city c ON bst.city_id = c.id  JOIN  city c2 ON bst2.city_id = c2.id "
						+ " JOIN user u ON u.id = bk.user_id WHERE bsh.id = :scheduleID  AND bk.status_code = 'CONF' "
						+ " GROUP BY sector , username , passengerType  ORDER BY sector, username, paymentMode, passengerType ) as main  "
						+ " INNER JOIN  user_user_group UG1 ON main.userId = UG1.user_id AND UG1.user_group_id <> 110 "
						+ " WHERE UG1.user_id not in (select user_id from user_user_group UG2 where UG2.user_group_id = 110)";

				Session session = HibernateUtil.getCurrentSession();
				@SuppressWarnings("unchecked")
				List<tripCollection> collection = session.createSQLQuery(sql).setParameter("scheduleID", scheduleID)
						.setResultTransformer(Transformers.aliasToBean(tripCollection.class)).list();
				;
				return Response.status(OK).entity(collection).build();
			} catch (Exception e) {
				logger.error("Error while calculating user collection for the given user", e);
				HibernateUtil.rollback();
				return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
						.build();
			}
		} else {
			return authHandler.buildErrorResponse(httpHeaders, CRUD.Read);
		}
	}

	/**
	 * 
	 * 
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@GET
	@Path("/seatreservation/{scheduleID}/{busType}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getSeatReservation(@PathParam("scheduleID") Integer scheduleID,
			@PathParam("busType") Integer busType) {
		if (authHandler.isAllowed(httpHeaders, CRUD.Read)) {
			try {

				String sql = " select CAST(bseat.seat_number AS UNSIGNED) AS seatNumber, bseat.x AS seatX,"
						+ "    bseat.y AS seatY, " + "	   CASE  " + "			WHEN bseat.type='window' THEN '[W]'  "
						+ "            WHEN bseat.type='aisle' THEN '[A]'  "
						+ "            WHEN bseat.type='ordinary' THEN '[O]'  " + "		END AS seatType, "
						+ "        seat_report.reference, "
						+ "        seat_report.vCode, "
						+ "        seat_report.fromStop, " 
						+ "        seat_report.toStop, "
						+ "        seat_report.passengerType, " 
						+ "        seat_report.paymentMode, "
						+ "        seat_report.amountToCollect, "
						+ "        seat_report.amount, "
						+ "        seat_report.phone "
						+ " from  "
						+ " (select * "
						+ " From bus_seat bseat where bseat.bus_type_id=:busType ) as bseat  left outer join "
						+ " (SELECT   bip.seat_number AS seatNumber,  bk.reference AS reference, "
						+ "    p.mobile_telephone AS phone, p.nic AS nic,  p.name AS name, "
						+ "    bk.verification_code AS vCode,   CONCAT(bst.name, ' ', "
						+ "            DATE_FORMAT(bsbs.arrival_time, '%H:%i')) AS fromStop, "
						+ "    c2.name AS toStop,  bip.passenger_type AS passengerType, "
						+ "    bip.fare_payment_option AS paymentMode,  "
						+ " SUM(CASE "
						+ "        WHEN fare_payment_option = 'PayAtBus' THEN seat_fare  ELSE 0 "
						+ "    END) AS amountToCollect, " 
						+ " seat_fare AS amount,"
						+ "            bsbs.idx as stop_start, "  
						+ "            bsbs2.idx as stop_end  FROM  booking bk  JOIN "
						+ "    booking_item bi ON bk.id = bi.booking_id  JOIN "
						+ "    booking_item_passenger bip ON bi.id = bip.booking_item_id  JOIN "
						+ "    bus_schedule bsh ON bi.schedule_id = bsh.id  JOIN "
						+ "    bus_route brt ON bsh.bus_route_id = brt.id  JOIN "
						+ "    bus b ON bsh.bus_id = b.id  JOIN  supplier s ON b.supplier_id = s.id "
						+ "        JOIN   bus_stop bst ON bi.from_bus_stop_id = bst.id  JOIN "
						+ "    bus_stop bst2 ON bi.to_bus_stop_id = bst2.id  JOIN "
						+ "    city c ON bst.city_id = c.id  JOIN  city c2 ON bst2.city_id = c2.id "
						+ "        JOIN  client p ON p.id = bk.client_id  JOIN "
						+ "    bus_schedule_bus_stop bsbs ON bsh.id = bsbs.schedule_id "
						+ "        AND bst.id = bsbs.bus_stop_id  JOIN "
						+ "    bus_schedule_bus_stop bsbs2 ON bsh.id = bsbs2.schedule_id "
						+ "        AND bst2.id = bsbs2.bus_stop_id  WHERE  bi.status_code = 'CONF' "
						+ "        AND bi.schedule_id = :scheduleID "
						+ " GROUP BY bk.reference , bip.passenger_type,bip.seat_number ) as seat_report  "
						+ " on bseat.seat_number=seat_report.seatNumber  order by seatNumber,stop_start ";
				Session session = HibernateUtil.getCurrentSession();
				@SuppressWarnings("unchecked")
				List<tripCollection> collection = session.createSQLQuery(sql).setParameter("scheduleID", scheduleID)
						.setParameter("busType", busType)
						.setResultTransformer(Transformers.aliasToBean(SeatReservationCollection.class)).list();
				;
				return Response.status(OK).entity(collection).build();
			} catch (Exception e) {
				logger.error("Error while calculating user collection for the given user", e);
				HibernateUtil.rollback();
				return Response.status(INTERNAL_SERVER_ERROR).entity(new RestErrorResponse(MSG_INTERNAL_SERVER_ERROR))
						.build();
			}
		} else {
			return authHandler.buildErrorResponse(httpHeaders, CRUD.Read);
		}
	}

	

	

	public static final class SeatReservationCollection {
		private BigInteger seatNumber;
		private Integer seatX;
		private Integer seatY;
		private String seatType;
		private String reference;
		private String phone;
		private String nic;
		private String name;

		private String vCode;
		private String fromStop;
		private String toStop;
		private String passengerType;
		private String paymentMode;
		private Double amountToCollect;
		private Double amount;
		

		public String getReference() {
			return reference;
		}

		public void setReference(String reference) {
			this.reference = reference;
		}

		public BigInteger getSeatNumber() {
			return seatNumber;
		}

		public void setSeatNumber(BigInteger seatNumber) {
			this.seatNumber = seatNumber;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getFromStop() {
			return fromStop;
		}

		public void setFromStop(String fromStop) {
			this.fromStop = fromStop;
		}

		public String getToStop() {
			return toStop;
		}

		public void setToStop(String toStop) {
			this.toStop = toStop;
		}

		public String getvCode() {
			return vCode;
		}

		public void setvCode(String vCode) {
			this.vCode = vCode;
		}

		public String getPaymentMode() {
			return paymentMode;
		}

		public void setPaymentMode(String paymentMode) {
			this.paymentMode = paymentMode;
		}

		public Double getAmountToCollect() {
			return amountToCollect;
		}

		public void setAmountToCollect(Double amountToCollect) {
			this.amountToCollect = amountToCollect;
		}
		
		public Double getAmount() {
			return amount ;
		}

		public void setAmount(Double amount) {
			this.amount = amount;
		}
		public String getPassengerType() {
			return passengerType;
		}

		public void setPassengerType(String passengerType) {
			this.passengerType = passengerType;
		}

		public String getSeatType() {
			return seatType;
		}

		public void setSeatType(String seatType) {
			this.seatType = seatType;
		}

		public String getNic() {
			return nic;
		}

		public void setNic(String nic) {
			this.nic = nic;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getSeatX() {
			return seatX;
		}

		public void setSeatX(Integer seatX) {
			this.seatX = seatX;
		}

		public Integer getSeatY() {
			return seatY;
		}

		public void setSeatY(Integer seatY) {
			this.seatY = seatY;
		}

	}

	public static final class tripCollection {

		private String sector;
		private String username;
		private String paymentOption;
		private BigInteger noOfSeats;
		private Double seatFare;
		private String passengerType;
		private String seatNumbers;
		private String paymentMode;
		private Double collectedFare;

		public String getSector() {
			return sector;
		}

		public void setSector(String sector) {
			this.sector = sector;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPaymentOption() {
			return paymentOption;
		}

		public void setPaymentOption(String paymentOption) {
			this.paymentOption = paymentOption;
		}

		public BigInteger getNoOfSeats() {
			return noOfSeats;
		}

		public void setNoOfSeats(BigInteger noOfSeats) {
			this.noOfSeats = noOfSeats;
		}

		public String getSeatNumbers() {
			return seatNumbers;
		}

		public void setSeatNumbers(String seatNumbers) {
			this.seatNumbers = seatNumbers;
		}

		public Double getCollectedFare() {
			return collectedFare;
		}

		public void setCollectedFare(Double collectedFare) {
			this.collectedFare = collectedFare;
		}

		public Double getSeatFare() {
			return seatFare;
		}

		public void setSeatFare(Double seatFare) {
			this.seatFare = seatFare;
		}

		public String getPassengerType() {
			return passengerType;
		}

		public void setPassengerType(String passengerType) {
			this.passengerType = passengerType;
		}

		public String getPaymentMode() {
			return paymentMode;
		}

		public void setPaymentMode(String paymentMode) {
			this.paymentMode = paymentMode;
		}

	}

	@XmlType(name = "UserCollection", namespace = "http://bean.express.lk")
	@XmlRootElement
	public static final class UserCollection {

		private String username;
		private Date departureTime;
		private Integer scheduleId;
		private String reference;
		private Double fare;
		private Double totalPaid;
		private Date bookingTime;
		private String paymentMethods;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public Date getDepartureTime() {
			return departureTime;
		}

		public void setDepartureTime(Date departureTime) {
			this.departureTime = departureTime;
		}

		public Integer getScheduleId() {
			return scheduleId;
		}

		public void setScheduleId(Integer scheduleId) {
			this.scheduleId = scheduleId;
		}

		public String getReference() {
			return reference;
		}

		public void setReference(String reference) {
			this.reference = reference;
		}

		public Double getFare() {
			return fare;
		}

		public void setFare(Double fare) {
			this.fare = fare;
		}

		public Double getTotalPaid() {
			return totalPaid;
		}

		public void setTotalPaid(Double totalPaid) {
			this.totalPaid = totalPaid;
		}

		public Date getBookingTime() {
			return bookingTime;
		}

		public void setBookingTime(Date bookingTime) {
			this.bookingTime = bookingTime;
		}

		public String getPaymentMethods() {
			return paymentMethods;
		}

		public void setPaymentMethods(String paymentMethods) {
			this.paymentMethods = paymentMethods;
		}
	}

	@XmlType(name = "UserCollectionPrint", namespace = "http://bean.express.lk")
	@XmlRootElement
	public static final class UserCollectionPrint {
		private String routeName;
		private Date departureTime;
		private String supplier;
		private String sector;
		private Double seatFare;
		private String passengerType;
		private BigInteger noOfSeats;
		private String paymentOption;
		private Double collectedFare;
		private Double bookingFee;
		private String plateNumber;

		public String getRouteName() {
			return routeName;
		}

		public void setRouteName(String routeName) {
			this.routeName = routeName;
		}

		public Date getDepartureTime() {
			return departureTime;
		}

		public void setDepartureTime(Date departureTime) {
			this.departureTime = departureTime;
		}

		public String getSupplier() {
			return supplier;
		}

		public void setSupplier(String supplier) {
			this.supplier = supplier;
		}

		public String getSector() {
			return sector;
		}

		public void setSector(String sector) {
			this.sector = sector;
		}

		public BigInteger getNoOfSeats() {
			return noOfSeats;
		}

		public void setNoOfSeats(BigInteger noOfSeats) {
			this.noOfSeats = noOfSeats;
		}

		public Double getCollectedFare() {
			return collectedFare;
		}

		public void setCollectedFare(Double collectedFare) {
			this.collectedFare = collectedFare;
		}

		public Double getBookingFee() {
			return bookingFee;
		}

		public void setBookingFee(Double bookingFee) {
			this.bookingFee = bookingFee;
		}

		public String getPlateNumber() {
			return plateNumber;
		}

		public void setPlateNumber(String plateNumber) {
			this.plateNumber = plateNumber;
		}

		public Double getSeatFare() {
			return seatFare;
		}

		public void setSeatFare(Double seatFare) {
			this.seatFare = seatFare;
		}

		public String getPassengerType() {
			return passengerType;
		}

		public void setPassengerType(String passengerType) {
			this.passengerType = passengerType;
		}

		public String getPaymentOption() {
			return paymentOption;
		}

		public void setPaymentOption(String paymentOption) {
			this.paymentOption = paymentOption;
		}

	}
}
