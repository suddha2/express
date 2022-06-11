<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 10/6/14
 * Time: 1:48 PM
 */

namespace Api\Manager\Schedule;


use Api\Client\Rest\Model\City;
use Api\Client\Soap\Core\AvailabilityCriteria;
use Api\Client\Soap\Core\BusSchedule;
use Api\Manager\Base;
use Api\Manager\Booking;
use Application\Acl\Acl;
use Application\Helper\ExprDateTime;
use Application\Model\User;

class Availability extends Base{

    protected $_session = null;
    protected $_showDeatiled = false;
    /**
     * @var BusSchedule
     */
    private $_currentSchedule = false;

    /**
     * set session variable
     * @param $session
     */
    public function setSession($session)
    {
        $this->_session = $session;
    }

    /**
     * @param $stat
     */
    public function setShowDetailed($stat)
    {
        $this->_showDeatiled = $stat;
    }

    /**
     * get availability result
     * @param $boardingLocation
     * @param $droppingLocation
     * @param $adults
     * @param $busTypeId
     * @param $children
     * @param $infants
     * @param $scheduleId
     * @return \Api\Client\Soap\Core\anyType
     * @throws \Exception
     */
    public function getResult($boardingLocation, $droppingLocation, $adults, $busTypeId, $children, $infants, $scheduleId)
    {
        $criteria = new AvailabilityCriteria($boardingLocation, $busTypeId, $droppingLocation, null, $scheduleId, null);

        $response = $this->getSearchService()->checkAvailability($this->_session, $criteria);

        //check if status is okay
        if($this->responseIsValid($response)){
            return $this->formatResult($response->data, $scheduleId);
        }
    }

    /**
     * @param \Api\Client\Soap\Core\AvailabilityResult $result
     * @return array
     */
    private function formatResult(\Api\Client\Soap\Core\AvailabilityResult $result, $scheduleId)
    {
        $oUser            = $this->getUserObject();
        $seatsVacant      = self::getArray($result->vacantSeats);
        $seatsBooked      = self::getArray($result->bookedSeats);
        $seatsUnavailable = self::getArray($result->unavailableSeats);
        $agentAllocations = self::getArray($result->agentAllocations);

        $seatsReserved = array();
        foreach ($seatsBooked as $seatBooked) {
            /**
             * @var $seatBooked \Api\Client\Soap\Core\Reservation
             */
            $seatsReserved[$seatBooked->seatNumber] = $seatBooked;
        }

        //get seats for agents
        $seatsForAgents = $this->getSeatsForAgents($agentAllocations);
        //check for allowed agents
        $allowedAgents = $this->tmpAllowedAgents($scheduleId);

        //format seats array
        $seats = array();
        foreach($result->seats as $seat){
            /**
             * @var $seat \Api\Client\Soap\Core\BusSeat
             */
            $oneSeat = array(
                "id"        => $seat->id,
                "number"    => $seat->number,
                "seatType"  => $seat->seatType,
                "x"         => $seat->x,
                "y"         => $seat->y,
                "available" => (! in_array($seat->number, $seatsUnavailable)),
                "booked"    => false,
            );

            if (isset($seatsReserved[$seat->number])) {
                /** @var \Api\Client\Soap\Core\Reservation $seatReserved */
                $seatReserved = $seatsReserved[$seat->number];
                $oneSeat['booked'] = true;
                $oneSeat['dummy'] = $seatReserved->dummy;

                //set booking status
                if($seatReserved->bookingStatusCode==Booking::STATUS_CODE_CONFIRM){
                    $oneSeat['booking_status'] = 'confirm';
                }
                if($seatReserved->bookingStatusCode==Booking::STATUS_CODE_CONFIRM
                    && $seatReserved->bookingChargeable==0){
                    $oneSeat['booking_status'] = 'pay_at_bus';
                }
                if($seatReserved->bookingStatusCode==Booking::STATUS_CODE_TENTATIVE){
                    $oneSeat['booking_status'] = 'tentative';
                }

                //show aditional details only if user has permission
                if (! $seatReserved->dummy && $this->_showDeatiled) {
                    $oneSeat['user_id'] = $seatReserved->bookingUserId;
                    $oneSeat['gender'] = $seatReserved->gender;
                    $oneSeat['passengerType'] = $seatReserved->passengerType;
                    $oneSeat['name'] = $seatReserved->name;
                    $oneSeat['mobile'] = $seatReserved->mobile;
                    $oneSeat['email'] = $seatReserved->email;
                    $oneSeat['nic'] = $seatReserved->nic;
                    $oneSeat['agent'] = $seatReserved->bookingAgentId;
                    $oneSeat['booking_id'] = $seatReserved->bookingId;
                    $oneSeat['booking_ref'] = $seatReserved->bookingReference;
                    $oneSeat['booking_time'] = $seatReserved->bookingTime;
                    $oneSeat['remarks'] = $seatReserved->bookingRemarks;
                    $oneSeat['journeyPerformed'] = $seatReserved->journeyPerformed;
                    $oneSeat['fromBusStopId'] = $seatReserved->fromBusStopId;
                    $oneSeat['toBusStopId'] = $seatReserved->toBusStopId;

                    $oneSeat['chargeable'] = $seatReserved->bookingChargeable;
                    $oneSeat['payments'] = $seatReserved->bookingPayments;
                    $oneSeat['paymentModes'] = $seatReserved->bookingPaymentModes;
                    $oneSeat['refunds'] = $seatReserved->bookingRefunds;
                    //payment to be collected
                    $toBeCollected = $seatReserved->bookingChargeable - ($seatReserved->bookingPayments - $seatReserved->bookingRefunds);
                    $oneSeat['tobe_collected'] = $toBeCollected;
                    $oneSeat['paid'] = ($toBeCollected>0);
                }
            }

            //check if this seat is agent's seat
            if(isset($seatsForAgents[$seat->number])){
                //if user has permission, show agent
                if($this->_showDeatiled){
                    $oneSeat['agentDefault'] = $seatsForAgents[$seat->number];
                }else{
                    //user has no permission to view agent. Block the seat
                    $oneSeat['available'] = false;

                    /**
                     * ===== TMP ====
                     */
                    //if seat is available in allowed agents, it's book-able
                    if(in_array($seat->number, $allowedAgents)){
                        $oneSeat['available'] = true;
                    }
                }
            }

            $seats[] = $oneSeat;
        }
        return $seats;
    }

    private function tmpAllowedAgents($scheduleId)
    {
        $agents = array(
            //passara - colombo
            19357 => array(28, 27, 31, 32, 35, 36),//21/08/16

            //badulla - colombo
            19251 => array(3,4,7,8,11,12), //2016-08-01

            //trinco - dehiwala

            //wellawatta - akkareipattu

            //akka - wella

            //col - kalmunei
            20868 => array(29,30,33,34,37,38,41,42),

            //kalmunei - colombo
            21066 => array(29,30,33,34,37,38,41,42),
        );

        $routes = array(
            //badulla - colombo
            58 => array(
                array(
                    'from' => strtotime('2016/08/01'),
                    'to' => strtotime('2017/06/30'),
                    'seats' => array(3,4,7,8,11,12)
                )
            ),
        	//colombo - badulla
        	57 => array(
                array(
                    'from' => strtotime('2017/02/09'),
                    'to' => strtotime('2017/02/10'),
                    'seats' => array(3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22)
                ),
                array(
                    'from' => strtotime('2017/03/03'),
                    'to' => strtotime('2017/03/03'),
                    'seats' => array(3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22)
                )
            ),
            //passara - colombo
            60 => array(
                array(
                    'from' => strtotime('2016/08/20'),
                    'to' => strtotime('2017/06/30'),
                    'seats' => array(9,10,13,14,17,18)
                )
            ),
            //colombo - passara
            59 => array(
                array(
                    'from' => strtotime('2017/02/09'),
                    'to' => strtotime('2017/02/10'),
                    'seats' => array( 9,10,11,12,13,14,15,16,17,18,19,20)
                ),
                array(
                    'from' => strtotime('2017/03/03'),
                    'to' => strtotime('2017/03/03'),
                    'seats' => array(9,10,11,12,13,14,15,16,17,18,19,20)
                )
            ),

        	// wellawatta - trinco
        	66 => array(
                array(
                    'from' => strtotime('2017/02/09'),
                    'to' => strtotime('2017/02/10'),
                    'seats' => array(3,4,7,8,11,12,15,16,19,20,23,24,27,28,31,32,35,36,39,40)
                ),
                array(
                    'from' => strtotime('2017/03/01'),
                    'to' => strtotime('2017/06/30'),
                    'seats' => array(7,8,11,12,15,16,19,20)
                ),
                array(
                    'from' => strtotime('2017/04/07'),
                    'to' => strtotime('2017/04/17'),
                    'seats' => array(7,8,11,12,15,16,19,20,23,24,27,28,31,32,35,36,39,40)
                )
            ),

            //trinco - dehiwala
            65 => array(
                array(
                    'from' => strtotime('2016/08/01'),
                    'to' => strtotime('2017/06/30'),
                    'seats' => array(3,4,7,8,11,12)
                ),
                array(
                    'from' => strtotime('2017/02/21'),
                    'to' => strtotime('2017/02/22'),
                    'seats' => array(19,20,23,24,27,28)
                ),
                array(
                    'from' => strtotime('2017/02/24'),
                    'to' => strtotime('2017/02/24'),
                    'seats' => array(19,20,23,24,27,28)
                ),
                array(
                    'from' => strtotime('2017/02/25'),
                    'to' => strtotime('2017/02/25'),
                    'seats' => array(31,32,35,36,39,40)
                ),
                array(
                    'from' => strtotime('2017/03/01'),
                    'to' => strtotime('2017/06/30'),
                    'seats' => array(15,16,19,20,23,24,27,28,31,32)
                ),
                array(
                    'from' => strtotime('2017/04/07'),
                    'to' => strtotime('2017/04/17'),
                    'seats' => array(15,16,19,20,23,24,27,28,31,32,35,36,39,40)
                ),
            ),
            //akkareipattu - wellawatta
            64 => array(
                array(
                    'from' => strtotime('2016/08/01'),
                    'to' => strtotime('2017/12/31'),
                    'seats' => array(3,4,7,8,43,44,47,48)
                )
            ),
            //colombo - akkareipattu
            63 => array(
                array(
                    'from' => strtotime('2016/08/01'),
                    'to' => strtotime('2017/12/31'),
                    'seats' => array(3,4,7,8,31,32,35,36,39,40)
                )
            ),
            //colombo - kalmunei
            62 => array(
                array(
                    'from' => strtotime('2016/08/01'),
                    'to' => strtotime('2017/06/30'),
                    'seats' => array(29,30,33,34)
                )
            ),
            //kalmunei - colombo
            61 => array(
                array(
                    'from' => strtotime('2016/08/01'),
                    'to' => strtotime('2017/06/30'),
                    'seats' => array(29,30,33,34)
                ),
            ),
        );

        //if seats are defined in agents array
        if(isset($agents[$scheduleId])){
            return $agents[$scheduleId];
        }
        //return if defined in routes
        else{
            $curSchedule = $this->getSchedule($scheduleId);
            $routeId = $curSchedule->busRoute->id;
            if(isset($routes[$routeId])){
                $allowedSeats = array();
                $today = ExprDateTime::getDateFromServices($curSchedule->departureTime)->setTime(0,0,0)->getTimestamp();
                //loop through routes seats
                foreach ($routes[$routeId] as $route) {
                    //get allowed date range from route
                    $from = $route['from'];
                    $to = $route['to'];

                    //check if the current date is between from/to dates. If so return allowed seats
                    if($from!==false && $to!==false
                        && ($today>=$from && $today<=$to)){
                        //merge the seats
                        $allowedSeats = array_merge($allowedSeats, $route['seats']);
                    }
                }
                return $allowedSeats;
            }
            return array();
        }
    }

    private static function getArray($arr) {
        if (empty($arr)) {
            return array();
        } else {
            if (! is_array($arr)) {
                return array($arr);
            }
            return $arr;
        }
    }

    public function bookedSeats($sch_id, $seat_id){
        $sch_id = intval($sch_id);
        $seat_id = intval($seat_id);

        $num = $sch_id * $seat_id;

        //split val
        $split = strlen(strval(($sch_id%$seat_id)));

        return str_split(strval($num * $num));
    }

    private function getSeatsForAgents($agentAllocations)
    {
        $aSeats = array();
        foreach ($agentAllocations as $allocation) {
            /** \Api\Client\Soap\Core\AgentAllocation $allocation */
            foreach ($allocation->seatNumbers as $seat) {
                $aSeats[$seat] = $allocation->agent->id;
            }
        }
        return $aSeats;
    }

    /**
     * @return \Application\Model\User
     */
    private function getUserObject()
    {
        $oUser = $this->serviceManager->get('AuthService')->getIdentity();
        return $oUser ? $oUser : new User();
    }

    /**
     * @param $scheduleId
     * @return BusSchedule|\Api\Client\Soap\Core\BusSchedule[]
     */
    private function getSchedule($scheduleId)
    {
        //load schedule if not loaded
        if($this->_currentSchedule==false){
            /* @var $oSchedule \Api\Manager\Schedule */
            $oSchedule = $this->getServiceManager()->get('Api\Manager\Schedule');
            $this->_currentSchedule = $oSchedule->fetch($scheduleId);
        }
        return $this->_currentSchedule;
    }

}