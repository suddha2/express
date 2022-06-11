<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 12/30/14
 * Time: 12:07 PM
 */

namespace Application\Sms;


class Template {

    /**
     * -nlc-
     * Sent on customer seat booking
     */
//     const BOOKING_SUCCESS = '::SITE_NAME::
// eTicket=::TICKET_NO::
// v-code=::V_CODE::
// ::FROM_NAME::-::TO_NAME::
// ::DATE::@::DEPARTURE_TIME::
// GetIn=::GET_IN::
// Bus=::PLATE_NO::
// Seat=::SEAT_NO::
// Fee=Rs::FEE::
// ::HOTLINE::';
    const BOOKING_SUCCESS_OLD = '::SITE_NAME::
eTicket=::TICKET_NO::
v-code=::V_CODE::
::FROM_NAME::-::TO_NAME::
::DATE::@::DEPARTURE_TIME::
GetIn=::GET_IN::
Bus=::PLATE_NO::
Seat=::SEAT_NO::
Fee=Rs::FEE::
::HOTLINE::';

	const BOOKING_SUCCESS = '::SITE_NAME::
eTicket=::TICKET_NO::
v-code=::V_CODE::
::ROUTE::
::DATE::@::DEPARTURE_TIME::
Bus=::PLATE_NO::
Seat=::SEAT_NO::
GetIn=::GET_IN::';

    const BOOKING_SUCCESS_TB = '::SITE_NAME::
eTicket=::TICKET_NO::
v-code=::V_CODE::
::FROM_NAME::-::TO_NAME::
::DATE::@::DEPARTURE_TIME::
GetIn=::GET_IN::
Bus=::PLATE_NO::
Seat=::SEAT_NO::
::HOTLINE::';

    const BOOKING_SUCCESS_MB365 = 'Thank you.
eTicket=::TICKET_NO::
::FROM_NAME::-::TO_NAME::
::DATE::@::DEPARTURE_TIME::
GetIn=::GET_IN::
Bus=::PLATE_NO::
Seat=::SEAT_NO::
Fee=Rs::FEE::
www.busbooking.lk';

    /**
     * Sent on tentitive booking
     */
    const BOOKING_TENTETIVE_OLD = '::SITE_NAME::
Tmp Reference=::TICKET_NO::
::FROM_NAME::-::TO_NAME::
::DATE::
Fee=Rs::FEE::
Please make a payment of above amount.
::HOTLINE::';

const BOOKING_TENTETIVE = '::SITE_NAME::
PAYMENT REFERENCE=::TICKET_NO::
Fee=Rs::FEE::
---------------
ACCOUNT DETAILS
Express 418 (Private) Limited
1175-1400-3962
Sampath Bank
::HOTLINE::';

    /**
     * Booking added from conductor App
     */
    const CONDUCTOR_BOOKING_SUCCESS = 'eTicket=::TICKET_NO::
::FROM_NAME::-::TO_NAME::
::DATE::@::DEPARTURE_TIME::
Bus=::PLATE_NO::
Seat=::SEAT_NO::
Fee=Rs::FEE::';

    /**
     * No bookings for a schedule - to conductor/driver
     */
    const CONDUCTOR_SCHEDULE_NO_BOOKINGS = 'BUSBOOKINGLK
					-nlc-Bus No.: ::PLATE_NO::
					-nlc-Date: ::DATE::
					-nlc-Bus Turn: ::FROM_NAME::%20(::DEPARTURE_TIME::)
					-nlc-No bus bookings.
					-nlc-(Aasana wenkara netha.)
					-nlc-CALL: 0712665464';
    /**
     * bookings for a schedule - to conductor/driver
     */
    const CONDUCTOR_SCHEDULE_HAS_BOOKINGS = '::FROM_NAME::-::TO_NAME::
::DATE::@::DEPARTURE_TIME::
Bus:::PLATE_NO::
TotalSeats:::SEAT_COUNT::

::SEAT_PASSENGER_DATA::

BusBooking.lk ~ Happy Journeys!';

    /**
     * No bookings for bus in day - to Bus owner
     */
    const OWNER_DAYEND_NO_BOOKINGS = 'BUSBOOKINGLK
					Bus: ::PLATE_NO::
					Date: ::DATE::
					No bus bookings.
					(Aasana wenkara netha.)
					CALL: 0712665464';
    /**
     * Bookings for bus in day - to Bus owner
     */
    const OWNER_DAYEND_HAS_BOOKINGS = 'BUSBOOKINGLK
					-nlc-::PLATE_NO::
					-nlc-::DATE::
					-nlc-::BUS_TURN_SEATS_AMOUNT::';

    /**
     * Sent to passenger before journey containing the conductor's contact details
     */
    const PASSENGER_SCHEDULE = 'eTicket=::TICKET_NO::
::FROM_NAME::-::TO_NAME::
::DATE::@::DEPARTURE_TIME::
Bus:::PLATE_NO::
Seat=::SEAT_NO::
GetIn=::GET_IN::
BusContact=::CONDUCTOR_MOBILE::
BusBooking.lk ~ Happy Journeys!';
}