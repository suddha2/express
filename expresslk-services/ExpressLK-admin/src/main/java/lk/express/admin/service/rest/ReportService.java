package lk.express.admin.service.rest;

import static javax.ws.rs.core.Response.Status.OK;

import java.math.BigDecimal;
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

import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lk.express.auth.rest.RESTAuthenticationHandler;
import lk.express.db.HibernateUtil;
import lk.express.service.RestService;

@Path("/admin/report")
public class ReportService extends RestService {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

	// private final RESTAuthenticationHandler authHandler;
	private @Context HttpHeaders httpHeaders;

	public ReportService() {
		// authHandler = new RESTAuthenticationHandler("ExpressInvoiceReport");
	}

	@GET
	@Path("/expInvoice/{fromDate}/{toDate}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response geExpInvoiceData(@PathParam("fromDate") String fromDate, @PathParam("toDate") String toDate) {
		RESTAuthenticationHandler authHandler = new RESTAuthenticationHandler("ExpressInvoiceReport");
		if (!authHandler.isAllowed(httpHeaders, "execute")) {
			return authHandler.buildErrorResponse(httpHeaders, "ExpressInvoiceReport");
		}
		String sql = " SELECT 	expresslk_bb.bus_schedule.departure_time AS DepartureDateTime,  "
				+ "		expresslk_bb.bus_schedule.arrival_time AS ArrivalDatetime, "
				+ "		expresslk_bb.bus.plate_number AS BusNo,  expresslk_bb.bus_route.name AS Route, "
				+ "		fromStop.name as FromStop,  toStop.name as ToStop, "
				+ "		expresslk_bb.supplier.name AS Depot,  SUM(BIP_summary.NumberOfSeats) AS NumberSeats, "
				+ "		(SUM(BIP_summary.NumberOfSeats)*50) AS TotalExpressFee " + "FROM expresslk_bb.booking "
				+ " INNER JOIN expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id) "
				+ " INNER JOIN ( "
				+ "  SELECT expresslk_bb.booking_item_passenger.booking_item_id, count(expresslk_bb.booking_item_passenger.id) as NumberOfSeats "
				+ "  FROM expresslk_bb.booking "
				+ "  INNER JOIN expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id) "
				+ "  INNER JOIN expresslk_bb.booking_item_passenger ON (expresslk_bb.booking_item.id = expresslk_bb.booking_item_passenger.booking_item_id) "
				+ "  INNER JOIN expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id) "
				+ "  WHERE expresslk_bb.booking.booking_time BETWEEN concat(:fromDate,' 00:00')   AND concat(:toDate,' 23:59') "
				+ " AND expresslk_bb.booking.status_code = 'CONF' "
				+ "  GROUP BY expresslk_bb.booking_item_passenger.booking_item_id  ) "
				+ " AS BIP_summary ON (expresslk_bb.booking_item.id = BIP_summary.booking_item_id) "
				+ " INNER JOIN expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id) "
				+ " INNER JOIN expresslk_bb.bus_route ON (expresslk_bb.bus_schedule.bus_route_id = expresslk_bb.bus_route.id and bus_route.name LIKE '%[B]') "
				+ " INNER JOIN expresslk_bb.bus ON (expresslk_bb.bus_schedule.bus_id = expresslk_bb.bus.id) "
				+ " INNER JOIN expresslk_bb.supplier ON (expresslk_bb.bus.supplier_id = expresslk_bb.supplier.id) "
				+ " INNER JOIN expresslk_bb.bus_stop fromStop ON (expresslk_bb.booking_item.from_bus_stop_id = fromStop.id) "
				+ " INNER JOIN expresslk_bb.city fromCity ON (fromStop.city_id = fromCity.id) "
				+ " INNER JOIN expresslk_bb.bus_stop toStop ON (expresslk_bb.booking_item.to_bus_stop_id = toStop.id) "
				+ " INNER JOIN expresslk_bb.city toCity ON (toStop.city_id = toCity.id) "
				+ " WHERE expresslk_bb.booking.booking_time BETWEEN concat(:fromDate,' 00:00')   AND concat(:toDate,' 23:59') "
				+ " AND expresslk_bb.booking.status_code = 'CONF' "
				+ " AND expresslk_bb.booking_item.status_code = 'CONF' "
				+ " AND expresslk_bb.booking.user_id NOT IN (SELECT user_id FROM expresslk_bb.user_user_group WHERE user_group_id = 110) "
				+ " GROUP BY DepartureDateTime, BusNo  ORDER BY ArrivalDatetime ASC ";
		Session session = HibernateUtil.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<ExpressInvoiceData> collection = session.createSQLQuery(sql).setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate).setResultTransformer(Transformers.aliasToBean(ExpressInvoiceData.class))
				.list();
		return Response.status(OK).entity(collection).build();
	}

	@GET
	@Path("/approvedRefunds/{fromDate}/{toDate}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getApprovedRefunds(@PathParam("fromDate") String fromDate, @PathParam("toDate") String toDate) {
		RESTAuthenticationHandler authHandler = new RESTAuthenticationHandler("ApprovedRefundReport");

		if (!authHandler.isAllowed(httpHeaders, "execute")) {
			return authHandler.buildErrorResponse(httpHeaders, "ApprovedRefundReport");
		}
		String sql = " SELECT expresslk_bb.booking.reference as BookingReference, "
				+ " DATE(expresslk_bb.booking.booking_time) AS BookingDate, "
				+ " DATE(PaymentRefunds.RefundDatetime) AS RefundDate, " + " expresslk_bb.bus.plate_number AS BusNo, "
				+ " expresslk_bb.bus_route.name AS Route,  " + " SUM(BIP_summary.NumberOfSeats)  AS NumberSeats, "
				+ " REPLACE(FORMAT(SUM(expresslk_bb.booking_item.fare),2),',','') AS Fare,"
				+ " REPLACE(FORMAT((SUM(BIP_summary.NumberOfSeats)*35),2),',','') AS SltbBookingFee, "
				+ " REPLACE(FORMAT((SUM(BIP_summary.NumberOfSeats)*50),2),',','') AS ExpressBookingFee, "
				+ " REPLACE(FORMAT((SUM(expresslk_bb.booking_item.fare) + (SUM(BIP_summary.NumberOfSeats)*35) + (SUM(BIP_summary.NumberOfSeats)*50)),2),',','') AS TransactionTotal,  "
//				+ " REPLACE(FORMAT(( CASE "
//				+ "        WHEN SUM(PaymentRefunds.Amount) <= (SUM(expresslk_bb.booking_item.fare) + (SUM(BIP_summary.NumberOfSeats)*35) + (SUM(BIP_summary.NumberOfSeats)*50)) THEN SUM(PaymentRefunds.Amount) "
//				+ "        ELSE (SUM(expresslk_bb.booking_item.fare) + (SUM(BIP_summary.NumberOfSeats)*35) + (SUM(BIP_summary.NumberOfSeats)*50)) "
//				+ " END  ),2),',','') AS ApplicableRefund  "
				+ " REPLACE(FORMAT((SUM(expresslk_bb.booking_item.fare) + (SUM(BIP_summary.NumberOfSeats)*35) + (SUM(BIP_summary.NumberOfSeats)*50)),2),',','') AS ApplicableRefund  "
				+ " FROM expresslk_bb.booking "
				+ " INNER JOIN expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id) "
				+ " INNER JOIN  ( "
				+ "  SELECT expresslk_bb.booking_item_passenger.booking_item_id, count(expresslk_bb.booking_item_passenger.id) as NumberOfSeats "
				+ "  FROM expresslk_bb.booking "
				+ "  INNER JOIN expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id) "
				+ "  INNER JOIN expresslk_bb.booking_item_passenger ON (expresslk_bb.booking_item.id = expresslk_bb.booking_item_passenger.booking_item_id) "
				+ "  INNER JOIN expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id) "
				+ " WHERE expresslk_bb.booking.status_code = 'CANC' "
				+ "  GROUP BY expresslk_bb.booking_item_passenger.booking_item_id  ) "
				+ " AS BIP_summary ON (expresslk_bb.booking_item.id = BIP_summary.booking_item_id)  "
				+ " INNER JOIN expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id) "
				+ " INNER JOIN expresslk_bb.bus_route ON (expresslk_bb.bus_schedule.bus_route_id = expresslk_bb.bus_route.id and bus_route.name LIKE '%[B]') "
				+ " INNER JOIN expresslk_bb.bus ON (expresslk_bb.bus_schedule.bus_id = expresslk_bb.bus.id) "
				+ " INNER JOIN expresslk_bb.supplier ON (expresslk_bb.bus.supplier_id = expresslk_bb.supplier.id) "
				+ " INNER JOIN  ( "
				+ "        SELECT refund.booking_id AS refundBookingID, payment_refund.amount AS Amount, payment_refund.time as RefundDatetime "
				+ "        FROM payment_refund INNER JOIN refund ON (payment_refund.id = refund.id) "
				+ "        WHERE payment_refund.time BETWEEN concat(:fromDate,' 00:00')   AND concat(:toDate,' 23:59') "
				+ "	GROUP BY refundBookingID  "
				+ " ) AS PaymentRefunds on (expresslk_bb.booking.id = PaymentRefunds.refundBookingID)  "
				+ " WHERE expresslk_bb.booking.booking_time BETWEEN concat(:fromDate,' 00:00')   AND concat(:toDate,' 23:59') "
				+ " AND expresslk_bb.booking.status_code = 'CANC' "
				+ " AND expresslk_bb.booking_item.status_code = 'CANC' "
				+ " AND expresslk_bb.booking.user_id NOT IN (SELECT user_id FROM expresslk_bb.user_user_group WHERE user_group_id = 110) "
				+ " AND expresslk_bb.booking.agent_id is NULL GROUP BY BookingReference "
				+ " ORDER BY RefundDatetime ASC ";

		Session session = HibernateUtil.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<ApprovedRefundData> collection = session.createSQLQuery(sql).setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate).setResultTransformer(Transformers.aliasToBean(ApprovedRefundData.class))
				.list();
		return Response.status(OK).entity(collection).build();
	}

	@GET
	@Path("/approvedRefundsV2/{fromDate}/{toDate}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getApprovedRefundsV2(@PathParam("fromDate") String fromDate, @PathParam("toDate") String toDate) {
		RESTAuthenticationHandler authHandler = new RESTAuthenticationHandler("ApprovedRefundReport");

		if (!authHandler.isAllowed(httpHeaders, "execute")) {
			return authHandler.buildErrorResponse(httpHeaders, "ApprovedRefundReport");
		}
		String sql = " SELECT expresslk_bb.booking.reference as BookingReference, "
				+ " DATE(expresslk_bb.booking.booking_time) AS BookingDate, "
				+ " DATE(PaymentRefunds.RefundDatetime) AS RefundDate, " + " expresslk_bb.bus.plate_number AS BusNo, "
				+ " expresslk_bb.bus_route.name AS Route,  " + " SUM(BIP_summary.NumberOfSeats)  AS NumberSeats, "
				+ " REPLACE(FORMAT(SUM(expresslk_bb.booking_item.fare),2),',','') AS Fare,"
				+ " REPLACE(FORMAT((SUM(BIP_summary.NumberOfSeats)*35),2),',','') AS SltbBookingFee, "
				+ " REPLACE(FORMAT((SUM(BIP_summary.NumberOfSeats)*50),2),',','') AS ExpressBookingFee, "
				+ " REPLACE(FORMAT((SUM(expresslk_bb.booking_item.fare) + (SUM(BIP_summary.NumberOfSeats)*35) + (SUM(BIP_summary.NumberOfSeats)*50)),2),',','') AS TransactionTotal,  "
//				+ " REPLACE(FORMAT(( CASE "
//				+ "        WHEN SUM(PaymentRefunds.Amount) <= (SUM(expresslk_bb.booking_item.fare) + (SUM(BIP_summary.NumberOfSeats)*35) + (SUM(BIP_summary.NumberOfSeats)*50)) THEN SUM(PaymentRefunds.Amount) "
//				+ "        ELSE (SUM(expresslk_bb.booking_item.fare) + (SUM(BIP_summary.NumberOfSeats)*35) + (SUM(BIP_summary.NumberOfSeats)*50)) "
//				+ " END  ),2),',','') AS ApplicableRefund  "
				+ " REPLACE(FORMAT((SUM(expresslk_bb.booking_item.fare) + (SUM(BIP_summary.NumberOfSeats)*35) + (SUM(BIP_summary.NumberOfSeats)*50)),2),',','') AS ApplicableRefund  "
				+ " FROM expresslk_bb.booking "
				+ " INNER JOIN expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id) "
				+ " INNER JOIN  ( "
				+ "  SELECT expresslk_bb.booking_item_passenger.booking_item_id, count(expresslk_bb.booking_item_passenger.id) as NumberOfSeats "
				+ "  FROM expresslk_bb.booking "
				+ "  INNER JOIN expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id) "
				+ "  INNER JOIN expresslk_bb.booking_item_passenger ON (expresslk_bb.booking_item.id = expresslk_bb.booking_item_passenger.booking_item_id) "
				+ "  INNER JOIN expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id) "
				+ " WHERE expresslk_bb.booking.status_code = 'CANC' "
				+ "  GROUP BY expresslk_bb.booking_item_passenger.booking_item_id  ) "
				+ " AS BIP_summary ON (expresslk_bb.booking_item.id = BIP_summary.booking_item_id)  "
				+ " INNER JOIN expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id) "
				+ " INNER JOIN expresslk_bb.bus_route ON (expresslk_bb.bus_schedule.bus_route_id = expresslk_bb.bus_route.id and bus_route.name LIKE '%[B]') "
				+ " INNER JOIN expresslk_bb.bus ON (expresslk_bb.bus_schedule.bus_id = expresslk_bb.bus.id) "
				+ " INNER JOIN expresslk_bb.supplier ON (expresslk_bb.bus.supplier_id = expresslk_bb.supplier.id) "
				+ " INNER JOIN  ( "
				+ "        SELECT refund.booking_id AS refundBookingID, payment_refund.amount AS Amount, payment_refund.time as RefundDatetime "
				+ "        FROM payment_refund INNER JOIN refund ON (payment_refund.id = refund.id) "
				+ "        WHERE payment_refund.time BETWEEN concat(:fromDate,' 00:00')   AND concat(:toDate,' 23:59') "
				+ "	GROUP BY refundBookingID  "
				+ " ) AS PaymentRefunds on (expresslk_bb.booking.id = PaymentRefunds.refundBookingID)  "
				+ " WHERE  expresslk_bb.booking.status_code = 'CANC'  AND  (coalesce(expresslk_bb.booking.cancellation_cause,null,'-')!='NonPayment' )"
				+ " AND expresslk_bb.booking_item.status_code = 'CANC' "
				+ " AND expresslk_bb.booking.user_id NOT IN (SELECT user_id FROM expresslk_bb.user_user_group WHERE user_group_id = 110) "
				+ " AND expresslk_bb.booking.agent_id is NULL GROUP BY BookingReference "
				+ " ORDER BY RefundDatetime ASC ";

		Session session = HibernateUtil.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<ApprovedRefundData> collection = session.createSQLQuery(sql).setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate).setResultTransformer(Transformers.aliasToBean(ApprovedRefundData.class))
				.list();
		return Response.status(OK).entity(collection).build();
	}

	@GET
	@Path("/cashReconciliation/{fromDate}/{toDate}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getCashReconciliation(@PathParam("fromDate") String fromDate, @PathParam("toDate") String toDate) {
		RESTAuthenticationHandler authHandler = new RESTAuthenticationHandler("CashReconciliation");
		if (!authHandler.isAllowed(httpHeaders, "execute")) {
			return authHandler.buildErrorResponse(httpHeaders, "CashReconciliation");
		}
		String sql = " SELECT expresslk_bb.booking.reference AS BookingRef, expresslk_bb.booking.booking_time AS BookingDateTime, "
				+ " expresslk_bb.bus.plate_number AS BusNo,  expresslk_bb.bus_route.name AS Route, "
				+ " SUM(BIP_summary.NumberOfSeats) AS NumberSeats, "
				+ " REPLACE(FORMAT( SUM(expresslk_bb.booking_item.fare),2),',','') AS Fare, "
				+ " REPLACE(FORMAT( (SUM(BIP_summary.NumberOfSeats) * 35),2),',','')  AS SltbBookingFee, "
				+ " REPLACE(FORMAT( (SUM(BIP_summary.NumberOfSeats) * 50),2),',','')   AS ExpressBookingFee, "
				+ " REPLACE(FORMAT( (SUM(expresslk_bb.booking_item.fare) + (SUM(BIP_summary.NumberOfSeats) * 35) + (SUM(BIP_summary.NumberOfSeats) * 50)),2),',','') AS TransactionTotal, "
				+ " expresslk_bb.booking.status_code as BookingStatus, "
				+ " expresslk_bb.bus_schedule.departure_time as JourneyDate,"
				+ " PaymentRefunds.RefundDatetime as CancelledDate ," + " expresslk_bb.supplier.name as Depot "
				+ " FROM expresslk_bb.booking "
				+ " INNER JOIN expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id) "
				+ " INNER JOIN  "
				+ "   (SELECT expresslk_bb.booking_item_passenger.booking_item_id, count(expresslk_bb.booking_item_passenger.id) as NumberOfSeats "
				+ "   FROM expresslk_bb.booking "
				+ "   INNER JOIN expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id) "
				+ "   INNER JOIN expresslk_bb.booking_item_passenger ON (expresslk_bb.booking_item.id = expresslk_bb.booking_item_passenger.booking_item_id) "
				+ "   INNER JOIN expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id) "
				+ "   WHERE expresslk_bb.booking.booking_time BETWEEN   concat(:fromDate,' 00:00')   AND concat(:toDate,' 23:59')    "
				+ "   AND (coalesce(expresslk_bb.booking.cancellation_cause,null,'-')!='NonPayment' ) "
				+ "   GROUP BY expresslk_bb.booking_item_passenger.booking_item_id  ) "
				+ " AS BIP_summary ON (expresslk_bb.booking_item.id = BIP_summary.booking_item_id)  "
				+ " INNER JOIN expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id) "
				+ " INNER JOIN expresslk_bb.bus_route ON (expresslk_bb.bus_schedule.bus_route_id = expresslk_bb.bus_route.id and bus_route.name LIKE '%[B]') "
				+ " INNER JOIN expresslk_bb.bus ON (expresslk_bb.bus_schedule.bus_id = expresslk_bb.bus.id) "
				+ " INNER JOIN expresslk_bb.supplier ON (expresslk_bb.bus.supplier_id = expresslk_bb.supplier.id) "
				+ " LEFT OUTER JOIN  ( "
				+ " 	SELECT refund.booking_id AS refundBookingID, payment_refund.amount AS Amount, payment_refund.time as RefundDatetime "
				+ " 	FROM payment_refund INNER JOIN refund ON (payment_refund.id = refund.id) "
				+ " 	) AS PaymentRefunds on (expresslk_bb.booking.id = PaymentRefunds.refundBookingID) "
				+ " WHERE (coalesce(expresslk_bb.booking.cancellation_cause,null,'-')!='NonPayment' ) "
				+ " AND expresslk_bb.booking.booking_time BETWEEN concat(:fromDate,' 00:00')   AND concat(:toDate,' 23:59') "
				+ " AND expresslk_bb.booking.user_id NOT IN (SELECT user_id FROM expresslk_bb.user_user_group WHERE user_group_id = 110) "
				//+ " AND expresslk_bb.booking.agent_id is NULL  "
				+ " GROUP BY expresslk_bb.booking.reference "
				+ " ORDER BY BookingDateTime ASC ";

		Session session = HibernateUtil.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<CashReconData> collection = session.createSQLQuery(sql).setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate).setResultTransformer(Transformers.aliasToBean(CashReconData.class))
				.list();
		return Response.status(OK).entity(collection).build();
	}

	@GET
	@Path("/dailyCashReconciliation/{fromDate}/{toDate}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getDailyCashReconciliation(@PathParam("fromDate") String fromDate,
			@PathParam("toDate") String toDate) {
		RESTAuthenticationHandler authHandler = new RESTAuthenticationHandler("CashReconciliation");
		if (!authHandler.isAllowed(httpHeaders, "execute")) {
			return authHandler.buildErrorResponse(httpHeaders, "CashReconciliation");
		}
		String sql = " SELECT DATE(expresslk_bb.booking.booking_time) AS bookingDate, "
				+ " SUM(BIP_summary.NumberOfSeats) AS numberSeats,"
				+ " REPLACE(FORMAT( SUM(expresslk_bb.booking_item.fare),2),',','') AS Fare, "
				+ " REPLACE(FORMAT( (SUM(BIP_summary.NumberOfSeats) * 35),2),',','')  AS SltbBookingFee, "
				+ " REPLACE(FORMAT( (SUM(BIP_summary.NumberOfSeats) * 50),2),',','')   AS ExpressBookingFee,  "
				+ " REPLACE(FORMAT( (SUM(expresslk_bb.booking_item.fare) + (SUM(BIP_summary.NumberOfSeats) * 35) + (SUM(BIP_summary.NumberOfSeats) * 50)),2),',','') AS TransactionTotal, "
				+ " SUM(case when expresslk_bb.booking.status_code='CONF' then BIP_summary.NumberOfSeats else 0 end) as ConfirmedBooking, "
				+ " REPLACE(FORMAT( SUM(case when expresslk_bb.booking.status_code='CONF' then expresslk_bb.booking_item.fare else 0 end ),2),',','')   as ConfirmedBookingFare,  "
				+ " REPLACE(FORMAT(  SUM(case when expresslk_bb.booking.status_code='CONF' then BIP_summary.NumberOfSeats else 0 end) * 35,2),',','') ConfirmedSltbBookingFee, "
				+ " REPLACE(FORMAT( SUM(case when expresslk_bb.booking.status_code='CONF' then BIP_summary.NumberOfSeats else 0 end) * 50,2),',','') ConfirmedExpressBookingFee, "
				+ " SUM(case when expresslk_bb.booking.status_code='CANC' and (coalesce(expresslk_bb.booking.cancellation_cause,null,'-')!='NonPayment' ) then BIP_summary.NumberOfSeats else 0 end) as CancelledBooking, "
				+ " REPLACE(FORMAT(SUM(case when expresslk_bb.booking.status_code='CANC' and (coalesce(expresslk_bb.booking.cancellation_cause,null,'-')!='NonPayment' ) then expresslk_bb.booking_item.fare else 0 end),2),',','')  as CancelledBookingFare,  "
				+ " REPLACE(FORMAT(  SUM(case when expresslk_bb.booking.status_code='CANC' and (coalesce(expresslk_bb.booking.cancellation_cause,null,'-')!='NonPayment' ) then BIP_summary.NumberOfSeats else 0 end) * 35,2),',','') as CancelledSltbBookingFee, "
				+ " REPLACE(FORMAT(  SUM(case when expresslk_bb.booking.status_code='CANC' and (coalesce(expresslk_bb.booking.cancellation_cause,null,'-')!='NonPayment' ) then BIP_summary.NumberOfSeats else 0 end) * 50,2),',','') as CancelledExpressBookingFee "
				+ " FROM expresslk_bb.booking "
				+ " INNER JOIN expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id) "
				+ " INNER JOIN  ( "
				+ "  SELECT expresslk_bb.booking_item_passenger.booking_item_id, count(expresslk_bb.booking_item_passenger.id) as NumberOfSeats "
				+ "  FROM expresslk_bb.booking "
				+ "  INNER JOIN expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id) "
				+ "  INNER JOIN expresslk_bb.booking_item_passenger ON (expresslk_bb.booking_item.id = expresslk_bb.booking_item_passenger.booking_item_id) "
				+ "  INNER JOIN expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id) "
				+ "  WHERE expresslk_bb.booking.booking_time BETWEEN concat(:fromDate,' 00:00')  AND concat(:toDate,' 23:59') "
				+ " AND (coalesce(expresslk_bb.booking.cancellation_cause,null,'-')!='NonPayment' ) "
				+ "  GROUP BY expresslk_bb.booking_item_passenger.booking_item_id  ) "
				+ " AS BIP_summary ON (expresslk_bb.booking_item.id = BIP_summary.booking_item_id)   "
				+ " INNER JOIN expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id) "
				+ " INNER JOIN expresslk_bb.bus_route ON (expresslk_bb.bus_schedule.bus_route_id = expresslk_bb.bus_route.id and bus_route.name LIKE '%[B]') "
				+ " INNER JOIN expresslk_bb.bus ON (expresslk_bb.bus_schedule.bus_id = expresslk_bb.bus.id) "
				+ " INNER JOIN expresslk_bb.supplier ON (expresslk_bb.bus.supplier_id = expresslk_bb.supplier.id) "
				+ " WHERE expresslk_bb.booking.booking_time BETWEEN concat(:fromDate,' 00:00')  AND concat(:toDate,' 23:59') "
				+ " AND (coalesce(expresslk_bb.booking.cancellation_cause,null,'-')!='NonPayment' ) "
				+ " AND expresslk_bb.booking.user_id NOT IN (SELECT user_id FROM expresslk_bb.user_user_group WHERE user_group_id = 110) "
				+ " AND expresslk_bb.booking.agent_id is NULL  GROUP BY bookingDate ORDER BY BookingDate ASC";
		Session session = HibernateUtil.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<DailyCashReconData> collection = session.createSQLQuery(sql).setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate).setResultTransformer(Transformers.aliasToBean(DailyCashReconData.class))
				.list();
		return Response.status(OK).entity(collection).build();
	}

	@GET
	@Path("/dailyCashReconciliationV2/{fromDate}/{toDate}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getDailyCashReconciliationV2(@PathParam("fromDate") String fromDate,
			@PathParam("toDate") String toDate) {
		
		String sql = "select calander_days.date_field as dateField, " + 
				" CONCAT(coalesce( bookings.NumberSeats,NULL,0)  ,'')  as NumberSeats, " + 
				" CONCAT(coalesce( bookings.Fare,NULL,0)  ,'')  as Fare, " + 
				" CONCAT(coalesce( bookings.SltbBookingFee,NULL,0)  ,'')  as SltbBookingFee, " + 
				" CONCAT(coalesce( bookings.ExpressBookingFee,NULL,0)  ,'')  as ExpressBookingFee, " + 
				" CONCAT(coalesce( bookings.TransactionTotal,NULL,0)  ,'')  as TransactionTotal, " + 
				" CONCAT(coalesce( approvedRefunds.NumberSeats,NULL,0)  ,'')  as CxNumberSeats,  " + 
				" CONCAT(coalesce( approvedRefunds.Fare,NULL,0)  ,'')  as CxFare,  " + 
				" CONCAT(coalesce( approvedRefunds.SltbBookingFee,NULL,0)  ,'')  as CxSltbBookingFee,  " + 
				" CONCAT(coalesce( approvedRefunds.ExpressBookingFee,NULL,0)  ,'')  as CxExpressBookingFee,  " + 
				" CONCAT(coalesce( approvedRefunds.TransactionTotal,NULL,0)  ,'')  as CxTransactionTotal,  " + 
				" CONCAT(coalesce( approvedRefunds.ApplicableRefund,NULL,0)  ,'')  as CxApplicableRefund, " + 
				" CONCAT((coalesce( bookings.TransactionTotal,NULL,0)- coalesce( approvedRefunds.ApplicableRefund,NULL,0))  ,'')  as NetPayable  " + 
				" from ( SELECT  " + 
				"        `aaa`.`date_field` AS `date_field` " + 
				"    FROM " + 
				"        (SELECT  " + 
				"            ((MAKEDATE(YEAR(:start_date), 1) + INTERVAL (MONTH(:start_date) - 1) MONTH) + INTERVAL `aa`.`daynum` DAY) AS `date_field` " + 
				"        FROM " + 
				"            (SELECT  " + 
				"            ((`a`.`t` * 10) + `b`.`u`) AS `daynum` " + 
				"        FROM " + 
				"            (((SELECT 0 AS `t`) UNION SELECT 1 AS `1` UNION SELECT 2 AS `2` UNION SELECT 3 AS `3`) `a` " + 
				"        JOIN (SELECT 0 AS `u` UNION SELECT 1 AS `1` UNION SELECT 2 AS `2` UNION SELECT 3 AS `3` UNION SELECT 4 AS `4` UNION SELECT 5 AS `5` UNION SELECT 6 AS `6` UNION SELECT 7 AS `7` UNION SELECT 8 AS `8` UNION SELECT 9 AS `9`) `b`) " + 
				"        ORDER BY `daynum`) `aa`) `aaa` " + 
				"    WHERE " + 
				"        (MONTH(`aaa`.`date_field`) = MONTH(:start_date)) ) as calander_days left outer join  " + 
				" (SELECT   DATE_FORMAT(expresslk_bb.booking.booking_time,'%Y-%m-%d') AS BookingDateTime, " + 
				" SUM(BIP_summary.NumberOfSeats) AS NumberSeats, " + 
				" SUM(expresslk_bb.booking_item.fare) AS Fare,  " + 
				" SUM(BIP_summary.NumberOfSeats) * 35 AS SltbBookingFee,  " + 
				" SUM(BIP_summary.NumberOfSeats) * 50 AS ExpressBookingFee,  " + 
				" SUM(expresslk_bb.booking_item.fare) + (SUM(BIP_summary.NumberOfSeats) * 35) + (SUM(BIP_summary.NumberOfSeats) * 50) AS TransactionTotal " + 
				" FROM expresslk_bb.booking  " + 
				" INNER JOIN expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id)  " + 
				" INNER JOIN   " + 
				" (SELECT expresslk_bb.booking_item_passenger.booking_item_id, count(expresslk_bb.booking_item_passenger.id) as NumberOfSeats  " + 
				" FROM expresslk_bb.booking  " + 
				" INNER JOIN expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id)  " + 
				" INNER JOIN expresslk_bb.booking_item_passenger ON (expresslk_bb.booking_item.id = expresslk_bb.booking_item_passenger.booking_item_id)  " + 
				" INNER JOIN expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id)  " + 
				" WHERE expresslk_bb.booking.booking_time BETWEEN   concat(:start_date,' 00:00')  AND concat(:end_date,' 23:59') " + 
				" AND (coalesce(expresslk_bb.booking.cancellation_cause,null,'-')!='NonPayment' )   " + 
				" GROUP BY expresslk_bb.booking_item_passenger.booking_item_id  )  " + 
				" AS BIP_summary ON (expresslk_bb.booking_item.id = BIP_summary.booking_item_id)   " + 
				" INNER JOIN expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id)  " + 
				" INNER JOIN expresslk_bb.bus_route ON (expresslk_bb.bus_schedule.bus_route_id = expresslk_bb.bus_route.id and bus_route.name LIKE '%[B]')  " + 
				" WHERE (coalesce(expresslk_bb.booking.cancellation_cause,null,'-')!='NonPayment' )  " + 
				" AND expresslk_bb.booking.booking_time BETWEEN concat(:start_date,' 00:00')  AND concat(:end_date,' 23:59') " + 
				" AND expresslk_bb.booking.user_id NOT IN (SELECT user_id FROM expresslk_bb.user_user_group WHERE user_group_id = 110)  " + 
				" AND expresslk_bb.booking.agent_id is NULL   " + 
				" GROUP BY BookingDateTime " + 
				" ORDER BY BookingDateTime ASC ) as bookings on bookings.BookingDateTime=calander_days.date_field left outer join  " + 
				" (SELECT  " + 
				"  DATE_FORMAT(PaymentRefunds.RefundDatetime,'%Y-%m-%d') AS RefundDate,   " + 
				"  SUM(BIP_summary.NumberOfSeats)  AS NumberSeats,  " + 
				"  SUM(expresslk_bb.booking_item.fare) AS Fare, " + 
				"  SUM(BIP_summary.NumberOfSeats)*35 AS SltbBookingFee,  " + 
				"  SUM(BIP_summary.NumberOfSeats)*50 AS ExpressBookingFee,  " + 
				"  SUM(expresslk_bb.booking_item.fare) + (SUM(BIP_summary.NumberOfSeats)*35) + (SUM(BIP_summary.NumberOfSeats)*50) AS TransactionTotal,   " + 
//				"  ( CASE  " + 
//				"         WHEN SUM(PaymentRefunds.Amount) <= (SUM(expresslk_bb.booking_item.fare) + (SUM(BIP_summary.NumberOfSeats)*35) + (SUM(BIP_summary.NumberOfSeats)*50)) THEN SUM(PaymentRefunds.Amount)  " + 
//				"         ELSE (SUM(expresslk_bb.booking_item.fare) + (SUM(BIP_summary.NumberOfSeats)*35) + (SUM(BIP_summary.NumberOfSeats)*50))  " + 
//				"  END  ) AS ApplicableRefund  " +
				" (SUM(expresslk_bb.booking_item.fare) + (SUM(BIP_summary.NumberOfSeats)*35) + (SUM(BIP_summary.NumberOfSeats)*50)) AS ApplicableRefund   " +
				" FROM expresslk_bb.booking  " + 
				"  INNER JOIN expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id)  " + 
				"  INNER JOIN  (  " + 
				"   SELECT expresslk_bb.booking_item_passenger.booking_item_id, count(expresslk_bb.booking_item_passenger.id) as NumberOfSeats  " + 
				"   FROM expresslk_bb.booking  " + 
				"   INNER JOIN expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id)  " + 
				"   INNER JOIN expresslk_bb.booking_item_passenger ON (expresslk_bb.booking_item.id = expresslk_bb.booking_item_passenger.booking_item_id)  " + 
				"   INNER JOIN expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id)  " + 
				"  WHERE expresslk_bb.booking.status_code = 'CANC'  " + 
				"   GROUP BY expresslk_bb.booking_item_passenger.booking_item_id  )  " + 
				"  AS BIP_summary ON (expresslk_bb.booking_item.id = BIP_summary.booking_item_id)   " + 
				"  INNER JOIN expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id)  " + 
				"  INNER JOIN expresslk_bb.bus_route ON (expresslk_bb.bus_schedule.bus_route_id = expresslk_bb.bus_route.id and bus_route.name LIKE '%[B]')  " + 
				"  INNER JOIN expresslk_bb.bus ON (expresslk_bb.bus_schedule.bus_id = expresslk_bb.bus.id)  " + 
				"  INNER JOIN expresslk_bb.supplier ON (expresslk_bb.bus.supplier_id = expresslk_bb.supplier.id)  " + 
				"  INNER JOIN  (  " + 
				"         SELECT refund.booking_id AS refundBookingID, payment_refund.amount AS Amount, payment_refund.time as RefundDatetime  " + 
				"         FROM payment_refund INNER JOIN refund ON (payment_refund.id = refund.id)  " + 
				"         WHERE payment_refund.time BETWEEN concat(:start_date,' 00:00')  AND concat(:end_date,' 23:59') " + 
				" 	GROUP BY refundBookingID   " + 
				"  ) AS PaymentRefunds on (expresslk_bb.booking.id = PaymentRefunds.refundBookingID)   " + 
				"  WHERE  expresslk_bb.booking.status_code = 'CANC'  " + 
				"  AND expresslk_bb.booking_item.status_code = 'CANC'  " + 
				"  AND expresslk_bb.booking.user_id NOT IN (SELECT user_id FROM expresslk_bb.user_user_group WHERE user_group_id = 110)  " + 
				"  AND expresslk_bb.booking.agent_id is NULL  " + 
				"  GROUP BY RefundDate ) as approvedRefunds " + 
				" on calander_days.date_field=approvedRefunds.RefundDate " + 
				" order by date_field";
		
		
		Session session = HibernateUtil.getCurrentSessionWithTransaction();
		@SuppressWarnings("unchecked")
		List<DailyCashReconData> collection = session.createSQLQuery(sql).setParameter("start_date", fromDate)
				.setParameter("end_date", toDate).setResultTransformer(Transformers.aliasToBean(DailyCashReconDataV2.class))
				.list();
		return Response.status(OK).entity(collection).build();
		
		 
	}

	@GET
	@Path("/futureresList/{fromDate}/{toDate}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getFutureReservation(@PathParam("fromDate") String fromDate, @PathParam("toDate") String toDate) {
		RESTAuthenticationHandler authHandler = new RESTAuthenticationHandler("CashReconciliation");
		if (!authHandler.isAllowed(httpHeaders, "execute")) {
			return authHandler.buildErrorResponse(httpHeaders, "CashReconciliation");
		}
		String sql = " SELECT expresslk_bb.booking.reference AS bookingRef, expresslk_bb.booking.booking_time AS BookingDateTime, "
				+ " expresslk_bb.bus_schedule.departure_time AS JourneyStartDateTime, "
				+ " expresslk_bb.bus.plate_number AS busNo, expresslk_bb.bus_route.name AS Route, "
				+ " SUM(BIP_summary.NumberOfSeats) AS NumberSeats, "
				+ " REPLACE(FORMAT( SUM(expresslk_bb.booking_item.fare),2),',','') AS Fare, "
				+ " REPLACE(FORMAT( (SUM(BIP_summary.NumberOfSeats) * 35),2),',','')  AS SltbBookingFee, "
				+ " REPLACE(FORMAT( (SUM(BIP_summary.NumberOfSeats) * 50),2),',','')   AS ExpressBookingFee,  "
				+ " REPLACE(FORMAT( (SUM(expresslk_bb.booking_item.fare) + (SUM(BIP_summary.NumberOfSeats) * 35) + (SUM(BIP_summary.NumberOfSeats) * 50)),2),',','') AS TransactionTotal "
				+ " FROM expresslk_bb.booking "
				+ " INNER JOIN expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id) "
				+ " INNER JOIN  ( "
				+ "  SELECT expresslk_bb.booking_item_passenger.booking_item_id, count(expresslk_bb.booking_item_passenger.id) as NumberOfSeats "
				+ "  FROM expresslk_bb.booking "
				+ "  INNER JOIN expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id) "
				+ "  INNER JOIN expresslk_bb.booking_item_passenger ON (expresslk_bb.booking_item.id = expresslk_bb.booking_item_passenger.booking_item_id) "
				+ "  INNER JOIN expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id) "
				+ "  WHERE expresslk_bb.bus_schedule.departure_time >= DATE_FORMAT((LAST_DAY(NOW() - INTERVAL 1 MONTH) + INTERVAL 1 DAY),'%Y-%m-01 00:00:00') "
				+ "  AND expresslk_bb.booking.status_code = 'CONF' "
				+ "  GROUP BY expresslk_bb.booking_item_passenger.booking_item_id  ) "
				+ " AS BIP_summary ON (expresslk_bb.booking_item.id = BIP_summary.booking_item_id) "
				+ " INNER JOIN expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id) "
				+ " INNER JOIN expresslk_bb.bus_route ON (expresslk_bb.bus_schedule.bus_route_id = expresslk_bb.bus_route.id and bus_route.name LIKE '%[B]') "
				+ " INNER JOIN expresslk_bb.bus ON (expresslk_bb.bus_schedule.bus_id = expresslk_bb.bus.id) "
				+ " INNER JOIN expresslk_bb.supplier ON (expresslk_bb.bus.supplier_id = expresslk_bb.supplier.id) "
				+ " WHERE expresslk_bb.bus_schedule.departure_time >= concat(:fromDate,' 00:00') "
				+ " AND expresslk_bb.booking.booking_time <= concat(:toDate,' 23:59')  "
				+ " AND expresslk_bb.booking.status_code = 'CONF' "
				+ " AND expresslk_bb.booking_item.status_code = 'CONF' "
				+ " AND expresslk_bb.booking.user_id NOT IN (SELECT user_id FROM expresslk_bb.user_user_group WHERE user_group_id = 110) "
				+ " GROUP BY expresslk_bb.booking.reference  ORDER BY JourneyStartDateTime ASC ";
		Session session = HibernateUtil.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<FutureResData> collection = session.createSQLQuery(sql).setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate).setResultTransformer(Transformers.aliasToBean(FutureResData.class))
				.list();
		return Response.status(OK).entity(collection).build();
	}

	@GET
	@Path("/busfareList/{fromDate}/{toDate}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getBusFareReport(@PathParam("fromDate") String fromDate, @PathParam("toDate") String toDate) {
		RESTAuthenticationHandler authHandler = new RESTAuthenticationHandler("CashReconciliation");
		if (!authHandler.isAllowed(httpHeaders, "execute")) {
			return authHandler.buildErrorResponse(httpHeaders, "CashReconciliation");
		}
		String sql = " SELECT expresslk_bb.bus_schedule.departure_time AS DepartureDateTime, expresslk_bb.bus_schedule.arrival_time AS ArrivalDatetime, "
				+ " expresslk_bb.bus.plate_number AS BusNo, expresslk_bb.bus_route.name AS Route, "
				+ " fromCity.name as FromCity, toCity.name as ToCity, "
				+ " REPLACE(FORMAT((SUM(expresslk_bb.booking_item.fare) / SUM(BIP_summary.NumberOfSeats)),2),',','') AS Fare,  "
				+ " expresslk_bb.supplier.name AS Depot,  SUM(BIP_summary.NumberOfSeats) AS NumberSeats,  "
				+ " REPLACE(FORMAT(SUM(expresslk_bb.booking_item.fare),2),',','') AS TotalFare "
				+ " FROM expresslk_bb.booking "
				+ " INNER JOIN expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id) "
				+ " INNER JOIN  ( "
				+ "  SELECT expresslk_bb.booking_item_passenger.booking_item_id, count(expresslk_bb.booking_item_passenger.id) as NumberOfSeats "
				+ "  FROM expresslk_bb.booking "
				+ "  INNER JOIN expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id) "
				+ "  INNER JOIN expresslk_bb.booking_item_passenger ON (expresslk_bb.booking_item.id = expresslk_bb.booking_item_passenger.booking_item_id) "
				+ "  INNER JOIN expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id) "
				+ "  WHERE expresslk_bb.bus_schedule.departure_time BETWEEN concat(:fromDate,' 00:00')  AND concat(:toDate,' 23:59')  "
				+ "  AND expresslk_bb.booking.status_code = 'CONF' "
				+ "  GROUP BY expresslk_bb.booking_item_passenger.booking_item_id  ) "
				+ " AS BIP_summary ON (expresslk_bb.booking_item.id = BIP_summary.booking_item_id)   "
				+ " INNER JOIN expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id) "
				+ " INNER JOIN expresslk_bb.bus_route ON (expresslk_bb.bus_schedule.bus_route_id = expresslk_bb.bus_route.id and bus_route.name LIKE '%[B]') "
				+ " INNER JOIN expresslk_bb.bus ON (expresslk_bb.bus_schedule.bus_id = expresslk_bb.bus.id) "
				+ " INNER JOIN expresslk_bb.supplier ON (expresslk_bb.bus.supplier_id = expresslk_bb.supplier.id) "
				+ " INNER JOIN expresslk_bb.bus_stop fromStop ON (expresslk_bb.booking_item.from_bus_stop_id = fromStop.id) "
				+ " INNER JOIN expresslk_bb.city fromCity ON (fromStop.city_id = fromCity.id) "
				+ " INNER JOIN expresslk_bb.bus_stop toStop ON (expresslk_bb.booking_item.to_bus_stop_id = toStop.id) "
				+ " INNER JOIN expresslk_bb.city toCity ON (toStop.city_id = toCity.id) "
				+ " WHERE expresslk_bb.bus_schedule.departure_time BETWEEN concat(:fromDate,' 00:00')  AND concat(:toDate,' 23:59') "
				+ " AND expresslk_bb.booking.status_code = 'CONF' "
				+ " AND expresslk_bb.booking_item.status_code = 'CONF' "
				+ " AND expresslk_bb.booking.user_id NOT IN (SELECT user_id FROM expresslk_bb.user_user_group WHERE user_group_id = 110) "
				+ " GROUP BY DepartureDateTime, BusNo, FromCity, ToCity "
				+ " ORDER BY Depot ASC, ArrivalDatetime ASC, FromCity ASC, ToCity ASC ";

		Session session = HibernateUtil.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<BusFareData> collection = session.createSQLQuery(sql).setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate).setResultTransformer(Transformers.aliasToBean(BusFareData.class))
				.list();
		return Response.status(OK).entity(collection).build();
	}

	@GET
	@Path("/counterCashRecon/{fromDate}/{toDate}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getCounterCashReconReport(@PathParam("fromDate") String fromDate,
			@PathParam("toDate") String toDate) {
		RESTAuthenticationHandler authHandler = new RESTAuthenticationHandler("CashReconciliation");
		if (!authHandler.isAllowed(httpHeaders, "execute")) {
			return authHandler.buildErrorResponse(httpHeaders, "CashReconciliation");
		}
		String sql = "SELECT expresslk_bb.booking.reference AS BookingRef,  "
				+ " expresslk_bb.booking.booking_time  AS BookingDate, "
				+ " expresslk_bb.bus.plate_number AS BusNo,   expresslk_bb.bus_route.name AS Route,  "
				+ " SUM(BIP_summary.NumberOfSeats) AS NumberSeats,  "
				+ " REPLACE(FORMAT(SUM(expresslk_bb.booking_item.fare),2),',','') AS Fare,  "
				+ " REPLACE(FORMAT((SUM(BIP_summary.NumberOfSeats)*35),2),',','') AS SltbBookingFee,  "
				+ " REPLACE(FORMAT((expresslk_bb.booking_item.gross_price - expresslk_bb.booking_item.cost),2),',','') AS ExpressBookingFee, "
				+ " REPLACE(FORMAT((SUM(expresslk_bb.booking_item.fare) + (SUM(BIP_summary.NumberOfSeats)*35) + (expresslk_bb.booking_item.gross_price - expresslk_bb.booking_item.cost)),2),',','') AS TransactionTotal, user.username as TicketCounterUser "
				+ " FROM expresslk_bb.booking "
				+ " INNER JOIN expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id) "
				+ " INNER JOIN  ( "
				+ "   SELECT expresslk_bb.booking_item_passenger.booking_item_id, count(expresslk_bb.booking_item_passenger.id) as NumberOfSeats "
				+ "   FROM expresslk_bb.booking "
				+ "   INNER JOIN expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id) "
				+ "   INNER JOIN expresslk_bb.booking_item_passenger ON (expresslk_bb.booking_item.id = expresslk_bb.booking_item_passenger.booking_item_id) "
				+ "   INNER JOIN expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id) "
				+ "   WHERE expresslk_bb.booking.booking_time BETWEEN concat(:fromDate,' 00:00')  AND concat(:toDate,' 23:59') "
				+ "   AND expresslk_bb.booking.status_code = 'CONF' "
				+ "   GROUP BY expresslk_bb.booking_item_passenger.booking_item_id  ) "
				+ " AS BIP_summary ON (expresslk_bb.booking_item.id = BIP_summary.booking_item_id)  "
				+ " INNER JOIN expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id) "
				+ " INNER JOIN expresslk_bb.bus_route ON (expresslk_bb.bus_schedule.bus_route_id = expresslk_bb.bus_route.id and bus_route.name LIKE '%[B]') "
				+ " INNER JOIN expresslk_bb.bus ON (expresslk_bb.bus_schedule.bus_id = expresslk_bb.bus.id) "
				+ " INNER JOIN expresslk_bb.supplier ON (expresslk_bb.bus.supplier_id = expresslk_bb.supplier.id) "
				+ " INNER JOIN expresslk_bb.user ON (expresslk_bb.booking.user_id = expresslk_bb.user.id) "
				+ " WHERE expresslk_bb.booking.booking_time BETWEEN concat(:fromDate,' 00:00')  AND concat(:toDate,' 23:59') "
				+ " AND expresslk_bb.booking.status_code = 'CONF' "
				+ " AND expresslk_bb.booking_item.status_code = 'CONF' "
				+ " AND expresslk_bb.booking.user_id IN (SELECT user_id FROM expresslk_bb.user_user_group WHERE user_group_id = 110) "
				+ " GROUP BY expresslk_bb.booking.reference  ORDER BY BookingDate ASC, TicketCounterUser ASC";

		Session session = HibernateUtil.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<CounterCashReconData> collection = session.createSQLQuery(sql).setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate)
				.setResultTransformer(Transformers.aliasToBean(CounterCashReconData.class)).list();
		return Response.status(OK).entity(collection).build();
	}

	@GET
	@Path("/depotBusfareList/{fromDate}/{toDate}/{supplier}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getDepotFareReport(@PathParam("fromDate") String fromDate, @PathParam("toDate") String toDate,
			@PathParam("supplier") String supplier) {
		// RESTAuthenticationHandler authHandler = new
		// RESTAuthenticationHandler("CashReconciliation");
		// if (!authHandler.isAllowed(httpHeaders, "execute")) {
		// return authHandler.buildErrorResponse(httpHeaders, "depotFare");
		// }
		String sql = "SELECT   expresslk_bb.bus_schedule.departure_time AS departureDateTime, "
				+ "    expresslk_bb.bus_schedule.arrival_time AS arrivalDatetime, "
				+ "    expresslk_bb.bus.plate_number AS busNo,  expresslk_bb.bus_route.name AS route, "
				+ "    fromCity.name AS fromCity,  toCity.name AS toCity, "
				+ "    (SUM(expresslk_bb.booking_item.fare) / SUM(BIP_summary.NumberOfSeats)) AS fare, "
				+ "    expresslk_bb.supplier.name AS Depot,  SUM(BIP_summary.NumberOfSeats) AS numberSeats, "
				+ "    SUM(expresslk_bb.booking_item.fare) AS totalFare  FROM  expresslk_bb.booking "
				+ "        INNER JOIN "
				+ "    expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id) "
				+ "        INNER JOIN  (SELECT  " + "        expresslk_bb.booking_item_passenger.booking_item_id, "
				+ "            COUNT(expresslk_bb.booking_item_passenger.id) AS numberOfSeats  FROM "
				+ "        expresslk_bb.booking "
				+ "    INNER JOIN expresslk_bb.booking_item ON (expresslk_bb.booking.id = expresslk_bb.booking_item.booking_id) "
				+ "    INNER JOIN expresslk_bb.booking_item_passenger ON (expresslk_bb.booking_item.id = expresslk_bb.booking_item_passenger.booking_item_id) "
				+ "    INNER JOIN expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id) "
				+ "    WHERE "
				+ "        expresslk_bb.bus_schedule.departure_time BETWEEN concat(:fromDate,' 00:00')  AND concat(:toDate,' 23:59') "
				+ "            AND expresslk_bb.booking.status_code = 'CONF' "
				+ "    GROUP BY expresslk_bb.booking_item_passenger.booking_item_id) AS BIP_summary ON (expresslk_bb.booking_item.id = BIP_summary.booking_item_id) "
				+ "        INNER JOIN "
				+ "    expresslk_bb.bus_schedule ON (expresslk_bb.booking_item.schedule_id = expresslk_bb.bus_schedule.id) "
				+ "        INNER JOIN "
				+ "    expresslk_bb.bus_route ON (expresslk_bb.bus_schedule.bus_route_id = expresslk_bb.bus_route.id "
				+ "        AND bus_route.name LIKE '%[B]')  INNER JOIN "
				+ "    expresslk_bb.bus ON (expresslk_bb.bus_schedule.bus_id = expresslk_bb.bus.id) "
				+ "        INNER JOIN "
				+ "    expresslk_bb.supplier ON (expresslk_bb.bus.supplier_id = expresslk_bb.supplier.id) "
				+ "        INNER JOIN "
				+ "    expresslk_bb.bus_stop fromStop ON (expresslk_bb.booking_item.from_bus_stop_id = fromStop.id) "
				+ "        INNER JOIN expresslk_bb.city fromCity ON (fromStop.city_id = fromCity.id) "
				+ "        INNER JOIN "
				+ "    expresslk_bb.bus_stop toStop ON (expresslk_bb.booking_item.to_bus_stop_id = toStop.id) "
				+ "        INNER JOIN  expresslk_bb.city toCity ON (toStop.city_id = toCity.id)  WHERE "
				+ "    expresslk_bb.bus_schedule.departure_time BETWEEN concat(:fromDate,' 00:00')  AND concat(:toDate,' 23:59') "
				+ "        AND expresslk_bb.booking.status_code = 'CONF' "
				+ "        AND expresslk_bb.booking_item.status_code = 'CONF' "
				+ "        AND expresslk_bb.booking.user_id NOT IN (SELECT   user_id   FROM "
				+ "            expresslk_bb.user_user_group WHERE  user_group_id = 110) "
				+ "        AND expresslk_bb.bus.supplier_id = :supplier "
				+ "GROUP BY DepartureDateTime , BusNo , FromCity , ToCity "
				+ "ORDER BY Depot ASC , ArrivalDatetime ASC , FromCity ASC , ToCity ASC";
		Session session = HibernateUtil.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<DepotBusFare> collection = session.createSQLQuery(sql).setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate).setParameter("supplier", supplier)
				.setResultTransformer(Transformers.aliasToBean(DepotBusFare.class)).list();
		return Response.status(OK).entity(collection).build();
	}

	public static final class DepotBusFare {
		private Date departureDateTime, arrivalDatetime;
		private String busNo, route, fromCity, toCity, depot;
		private BigDecimal numberSeats;
		private Double fare, totalFare;

		public Date getDepartureDateTime() {
			return departureDateTime;
		}

		public void setDepartureDateTime(Date departureDateTime) {
			this.departureDateTime = departureDateTime;
		}

		public Date getArrivalDatetime() {
			return arrivalDatetime;
		}

		public void setArrivalDatetime(Date arrivalDatetime) {
			this.arrivalDatetime = arrivalDatetime;
		}

		public String getBusNo() {
			return busNo;
		}

		public void setBusNo(String busNo) {
			this.busNo = busNo;
		}

		public String getRoute() {
			return route;
		}

		public void setRoute(String route) {
			this.route = route;
		}

		public String getFromCity() {
			return fromCity;
		}

		public void setFromCity(String fromCity) {
			this.fromCity = fromCity;
		}

		public String getToCity() {
			return toCity;
		}

		public void setToCity(String toCity) {
			this.toCity = toCity;
		}

		public Double getFare() {
			return fare;
		}

		public void setFare(Double fare) {
			this.fare = fare;
		}

		public String getDepot() {
			return depot;
		}

		public void setDepot(String depot) {
			this.depot = depot;
		}

		public BigDecimal getNumberSeats() {
			return numberSeats;
		}

		public void setNumberSeats(BigDecimal numberSeats) {
			this.numberSeats = numberSeats;
		}

		public Double getTotalFare() {
			return totalFare;
		}

		public void setTotalFare(Double totalFare) {
			this.totalFare = totalFare;
		}

	}

	public static final class CounterCashReconData {
		private String BookingRef, BusNo, Route, Fare, SltbBookingFee, ExpressBookingFee, TransactionTotal,
				TicketCounterUser;
		private BigDecimal NumberSeats;
		private Date BookingDate;

		public String getBookingRef() {
			return BookingRef;
		}

		public void setBookingRef(String bookingRef) {
			BookingRef = bookingRef;
		}

		public String getBusNo() {
			return BusNo;
		}

		public void setBusNo(String busNo) {
			BusNo = busNo;
		}

		public String getRoute() {
			return Route;
		}

		public void setRoute(String route) {
			Route = route;
		}

		public String getFare() {
			return Fare;
		}

		public void setFare(String fare) {
			Fare = fare;
		}

		public String getSltbBookingFee() {
			return SltbBookingFee;
		}

		public void setSltbBookingFee(String sltbBookingFee) {
			SltbBookingFee = sltbBookingFee;
		}

		public String getExpressBookingFee() {
			return ExpressBookingFee;
		}

		public void setExpressBookingFee(String expressBookingFee) {
			ExpressBookingFee = expressBookingFee;
		}

		public String getTransactionTotal() {
			return TransactionTotal;
		}

		public void setTransactionTotal(String transactionTotal) {
			TransactionTotal = transactionTotal;
		}

		public String getTicketCounterUser() {
			return TicketCounterUser;
		}

		public void setTicketCounterUser(String ticketCounterUser) {
			TicketCounterUser = ticketCounterUser;
		}

		public BigDecimal getNumberSeats() {
			return NumberSeats;
		}

		public void setNumberSeats(BigDecimal numberSeats) {
			NumberSeats = numberSeats;
		}

		public Date getBookingDate() {
			return BookingDate;
		}

		public void setBookingDate(Date bookingDate) {
			BookingDate = bookingDate;
		}

	}

	public static final class BusFareData {
		private Date DepartureDateTime, ArrivalDatetime;
		private String BusNo, Route, FromCity, ToCity, Fare, Depot, TotalFare;
		private BigDecimal NumberSeats;

		public Date getDepartureDateTime() {
			return DepartureDateTime;
		}

		public void setDepartureDateTime(Date departureDateTime) {
			DepartureDateTime = departureDateTime;
		}

		public Date getArrivalDatetime() {
			return ArrivalDatetime;
		}

		public void setArrivalDatetime(Date arrivalDatetime) {
			ArrivalDatetime = arrivalDatetime;
		}

		public String getBusNo() {
			return BusNo;
		}

		public void setBusNo(String busNo) {
			BusNo = busNo;
		}

		public String getRoute() {
			return Route;
		}

		public void setRoute(String route) {
			Route = route;
		}

		public String getFromCity() {
			return FromCity;
		}

		public void setFromCity(String fromCity) {
			FromCity = fromCity;
		}

		public String getToCity() {
			return ToCity;
		}

		public void setToCity(String toCity) {
			ToCity = toCity;
		}

		public String getFare() {
			return Fare;
		}

		public void setFare(String fare) {
			Fare = fare;
		}

		public String getDepot() {
			return Depot;
		}

		public void setDepot(String depot) {
			Depot = depot;
		}

		public String getTotalFare() {
			return TotalFare;
		}

		public void setTotalFare(String totalFare) {
			TotalFare = totalFare;
		}

		public BigDecimal getNumberSeats() {
			return NumberSeats;
		}

		public void setNumberSeats(BigDecimal numberSeats) {
			NumberSeats = numberSeats;
		}

	}

	public static final class FutureResData {
		private String BookingRef, BusNo, Route, Fare, SltbBookingFee, ExpressBookingFee, TransactionTotal;
		private Date BookingDateTime, JourneyStartDateTime;
		private BigDecimal NumberSeats;

		public String getBookingRef() {
			return BookingRef;
		}

		public void setBookingRef(String bookingRef) {
			BookingRef = bookingRef;
		}

		public String getBusNo() {
			return BusNo;
		}

		public void setBusNo(String busNo) {
			BusNo = busNo;
		}

		public String getRoute() {
			return Route;
		}

		public void setRoute(String route) {
			Route = route;
		}

		public String getFare() {
			return Fare;
		}

		public void setFare(String fare) {
			Fare = fare;
		}

		public String getSltbBookingFee() {
			return SltbBookingFee;
		}

		public void setSltbBookingFee(String sltbBookingFee) {
			SltbBookingFee = sltbBookingFee;
		}

		public String getExpressBookingFee() {
			return ExpressBookingFee;
		}

		public void setExpressBookingFee(String expressBookingFee) {
			ExpressBookingFee = expressBookingFee;
		}

		public String getTransactionTotal() {
			return TransactionTotal;
		}

		public void setTransactionTotal(String transactionTotal) {
			TransactionTotal = transactionTotal;
		}

		public Date getBookingDateTime() {
			return BookingDateTime;
		}

		public void setBookingDateTime(Date bookingDateTime) {
			BookingDateTime = bookingDateTime;
		}

		public Date getJourneyStartDateTime() {
			return JourneyStartDateTime;
		}

		public void setJourneyStartDateTime(Date journeyStartDateTime) {
			JourneyStartDateTime = journeyStartDateTime;
		}

		public BigDecimal getNumberSeats() {
			return NumberSeats;
		}

		public void setNumberSeats(BigDecimal numberSeats) {
			NumberSeats = numberSeats;
		}
	}

	public static final class DailyCashReconDataV2 {
		private Date dateField;
		private String numberSeats, cxNumberSeats;
		private String fare, sltbBookingFee, expressBookingFee,transactionTotal, cxFare, 
		cxSltbBookingFee, cxExpressBookingFee, cxTransactionTotal, cxApplicableRefund, netPayable;
		public Date getDateField() {
			return dateField;
		}
		public void setDateField(Date dateField) {
			this.dateField = dateField;
		}
		public String getNumberSeats() {
			return numberSeats;
		}
		public void setNumberSeats(String numberSeats) {
			this.numberSeats = numberSeats;
		}
		public String getCxNumberSeats() {
			return cxNumberSeats;
		}
		public void setCxNumberSeats(String cxNumberSeats) {
			this.cxNumberSeats = cxNumberSeats;
		}
		public String getFare() {
			return fare;
		}
		public void setFare(String fare) {
			this.fare = fare;
		}
		public String getSltbBookingFee() {
			return sltbBookingFee;
		}
		public void setSltbBookingFee(String sltbBookingFee) {
			this.sltbBookingFee = sltbBookingFee;
		}
		public String getExpressBookingFee() {
			return expressBookingFee;
		}
		public void setExpressBookingFee(String expressBookingFee) {
			this.expressBookingFee = expressBookingFee;
		}
		public String getTransactionTotal() {
			return transactionTotal;
		}
		public void setTransactionTotal(String transactionTotal) {
			this.transactionTotal = transactionTotal;
		}
		public String getCxFare() {
			return cxFare;
		}
		public void setCxFare(String cxFare) {
			this.cxFare = cxFare;
		}
		public String getCxSltbBookingFee() {
			return cxSltbBookingFee;
		}
		public void setCxSltbBookingFee(String cxSltbBookingFee) {
			this.cxSltbBookingFee = cxSltbBookingFee;
		}
		public String getCxExpressBookingFee() {
			return cxExpressBookingFee;
		}
		public void setCxExpressBookingFee(String cxExpressBookingFee) {
			this.cxExpressBookingFee = cxExpressBookingFee;
		}
		public String getCxTransactionTotal() {
			return cxTransactionTotal;
		}
		public void setCxTransactionTotal(String cxTransactionTotal) {
			this.cxTransactionTotal = cxTransactionTotal;
		}
		public String getCxApplicableRefund() {
			return cxApplicableRefund;
		}
		public void setCxApplicableRefund(String cxApplicableRefund) {
			this.cxApplicableRefund = cxApplicableRefund;
		}
		public String getNetPayable() {
			return netPayable;
		}
		public void setNetPayable(String netPayable) {
			this.netPayable = netPayable;
		}

	}

	public static final class DailyCashReconData {
		private Date bookingDate;
		private BigDecimal numberSeats, confirmedBooking, cancelledBooking;
		private String fare, sltbBookingFee, expressBookingFee, transactionTotal, confirmedSltbBookingFee,
				confirmedExpressBookingFee, cancelledSltbBookingFee, cancelledExpressBookingFee, confirmedBookingFare,
				cancelledBookingFare;

		public Date getBookingDate() {
			return bookingDate;
		}

		public void setBookingDate(Date bookingDate) {
			this.bookingDate = bookingDate;
		}

		public BigDecimal getNumberSeats() {
			return numberSeats;
		}

		public void setNumberSeats(BigDecimal numberSeats) {
			this.numberSeats = numberSeats;
		}

		public String getFare() {
			return fare;
		}

		public void setFare(String fare) {
			this.fare = fare;
		}

		public String getSltbBookingFee() {
			return sltbBookingFee;
		}

		public void setSltbBookingFee(String sltbBookingFee) {
			this.sltbBookingFee = sltbBookingFee;
		}

		public String getExpressBookingFee() {
			return expressBookingFee;
		}

		public void setExpressBookingFee(String expressBookingFee) {
			this.expressBookingFee = expressBookingFee;
		}

		public String getTransactionTotal() {
			return transactionTotal;
		}

		public void setTransactionTotal(String transactionTotal) {
			this.transactionTotal = transactionTotal;
		}

		public BigDecimal getConfirmedBooking() {
			return confirmedBooking;
		}

		public void setConfirmedBooking(BigDecimal confirmedBooking) {
			this.confirmedBooking = confirmedBooking;
		}

		public BigDecimal getCancelledBooking() {
			return cancelledBooking;
		}

		public void setCancelledBooking(BigDecimal cancelledBooking) {
			this.cancelledBooking = cancelledBooking;
		}

		public String getConfirmedSltbBookingFee() {
			return confirmedSltbBookingFee;
		}

		public void setConfirmedSltbBookingFee(String confirmedSltbBookingFee) {
			this.confirmedSltbBookingFee = confirmedSltbBookingFee;
		}

		public String getConfirmedExpressBookingFee() {
			return confirmedExpressBookingFee;
		}

		public void setConfirmedExpressBookingFee(String confirmedExpressBookingFee) {
			this.confirmedExpressBookingFee = confirmedExpressBookingFee;
		}

		public String getCancelledSltbBookingFee() {
			return cancelledSltbBookingFee;
		}

		public void setCancelledSltbBookingFee(String cancelledSltbBookingFee) {
			this.cancelledSltbBookingFee = cancelledSltbBookingFee;
		}

		public String getCancelledExpressBookingFee() {
			return cancelledExpressBookingFee;
		}

		public void setCancelledExpressBookingFee(String cancelledExpressBookingFee) {
			this.cancelledExpressBookingFee = cancelledExpressBookingFee;
		}

		public String getConfirmedBookingFare() {
			return confirmedBookingFare;
		}

		public void setConfirmedBookingFare(String confirmedBookingFare) {
			this.confirmedBookingFare = confirmedBookingFare;
		}

		public String getCancelledBookingFare() {
			return cancelledBookingFare;
		}

		public void setCancelledBookingFare(String cancelledBookingFare) {
			this.cancelledBookingFare = cancelledBookingFare;
		}

	}

	public static final class CashReconData {
		private String bookingRef, busNo, route, bookingStatus, depot;
		private String fare, sltbBookingFee, expressBookingFee, transactionTotal;
		private BigDecimal numberSeats;
		private Date bookingDateTime, journeyDate, cancelledDate;

		public String getBookingRef() {
			return bookingRef;
		}

		public void setBookingRef(String bookingRef) {
			this.bookingRef = bookingRef;
		}

		public String getBusNo() {
			return busNo;
		}

		public void setBusNo(String busNo) {
			this.busNo = busNo;
		}

		public String getRoute() {
			return route;
		}

		public void setRoute(String route) {
			this.route = route;
		}

		public BigDecimal getNumberSeats() {
			return numberSeats;
		}

		public void setNumberSeats(BigDecimal numberSeats) {
			this.numberSeats = numberSeats;
		}

		public String getFare() {
			return fare;
		}

		public void setFare(String fare) {
			this.fare = fare;
		}

		public String getSltbBookingFee() {
			return sltbBookingFee;
		}

		public void setSltbBookingFee(String sltbBookingFee) {
			this.sltbBookingFee = sltbBookingFee;
		}

		public String getExpressBookingFee() {
			return expressBookingFee;
		}

		public void setExpressBookingFee(String expressBookingFee) {
			this.expressBookingFee = expressBookingFee;
		}

		public String getTransactionTotal() {
			return transactionTotal;
		}

		public void setTransactionTotal(String transactionTotal) {
			this.transactionTotal = transactionTotal;
		}

		public Date getBookingDateTime() {
			return bookingDateTime;
		}

		public void setBookingDateTime(Date bookingDateTime) {
			this.bookingDateTime = bookingDateTime;
		}

		public String getBookingStatus() {
			return bookingStatus;
		}

		public void setBookingStatus(String bookingStatus) {
			this.bookingStatus = bookingStatus;
		}

		public Date getCancelledDate() {
			return cancelledDate;
		}

		public void setCancelledDate(Date cancelledDate) {
			this.cancelledDate = cancelledDate;
		}

		public Date getJourneyDate() {
			return journeyDate;
		}

		public void setJourneyDate(Date journeyDate) {
			this.journeyDate = journeyDate;
		}

		public String getDepot() {
			return depot;
		}

		public void setDepot(String depot) {
			this.depot = depot;
		}

	}

	public static final class ApprovedRefundData {
		private String BookingReference, BusNo, Route;
		private Date BookingDate;
		private Date RefundDate;

		private BigDecimal NumberSeats;
		private String Fare, SltbBookingFee, ExpressBookingFee, TransactionTotal, ApplicableRefund;

		public String getBookingReference() {
			return BookingReference;
		}

		public void setBookingReference(String bookingReference) {
			BookingReference = bookingReference;
		}

		public String getBusNo() {
			return BusNo;
		}

		public void setBusNo(String busNo) {
			BusNo = busNo;
		}

		public String getRoute() {
			return Route;
		}

		public void setRoute(String route) {
			Route = route;
		}

		public Date getBookingDate() {
			return BookingDate;
		}

		public void setBookingDate(Date bookingDate) {
			BookingDate = bookingDate;
		}

		public Date getRefundDate() {
			return RefundDate;
		}

		public void setRefundDate(Date refundDate) {
			RefundDate = refundDate;
		}

		public BigDecimal getNumberSeats() {
			return NumberSeats;
		}

		public void setNumberSeats(BigDecimal numberSeats) {
			NumberSeats = numberSeats;
		}

		public String getFare() {
			return Fare;
		}

		public void setFare(String fare) {
			Fare = fare;
		}

		public String getSltbBookingFee() {
			return SltbBookingFee;
		}

		public void setSltbBookingFee(String sltbBookingFee) {
			SltbBookingFee = sltbBookingFee;
		}

		public String getExpressBookingFee() {
			return ExpressBookingFee;
		}

		public void setExpressBookingFee(String expressBookingFee) {
			ExpressBookingFee = expressBookingFee;
		}

		public String getTransactionTotal() {
			return TransactionTotal;
		}

		public void setTransactionTotal(String transactionTotal) {
			TransactionTotal = transactionTotal;
		}

		public String getApplicableRefund() {
			return ApplicableRefund;
		}

		public void setApplicableRefund(String applicableRefund) {
			ApplicableRefund = applicableRefund;
		}

	}

	public static final class ExpressInvoiceData {
		private Date DepartureDateTime;
		private Date ArrivalDatetime;
		private BigDecimal NumberSeats;
		private String BusNo, Route, FromStop, ToStop, Depot;
		private BigDecimal TotalExpressFee;

		public Date getDepartureDateTime() {
			return DepartureDateTime;
		}

		public void setDepartureDateTime(Date departureDateTime) {
			DepartureDateTime = departureDateTime;
		}

		public Date getArrivalDatetime() {
			return ArrivalDatetime;
		}

		public void setArrivalDatetime(Date arrivalDatetime) {
			ArrivalDatetime = arrivalDatetime;
		}

		public String getBusNo() {
			return BusNo;
		}

		public void setBusNo(String busNo) {
			BusNo = busNo;
		}

		public String getRoute() {
			return Route;
		}

		public void setRoute(String route) {
			Route = route;
		}

		public String getFromStop() {
			return FromStop;
		}

		public void setFromStop(String fromStop) {
			FromStop = fromStop;
		}

		public String getToStop() {
			return ToStop;
		}

		public void setToStop(String toStop) {
			ToStop = toStop;
		}

		public String getDepot() {
			return Depot;
		}

		public void setDepot(String depot) {
			Depot = depot;
		}

		public BigDecimal getNumberSeats() {
			return NumberSeats;
		}

		public void setNumberSeats(BigDecimal numberSeats) {
			NumberSeats = numberSeats;
		}

		public BigDecimal getTotalExpressFee() {
			return TotalExpressFee;
		}

		public void setTotalExpressFee(BigDecimal totalExpressFee) {
			TotalExpressFee = totalExpressFee;
		}

	}
}
