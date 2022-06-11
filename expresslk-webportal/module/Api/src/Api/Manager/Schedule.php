<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 3/25/15
 * Time: 8:39 AM
 */

namespace Api\Manager;


use Api\Client\Soap\Core\BusSchedule;
use Api\Client\Soap\Core\JourneyPerformedCriteria;
use Application\Helper\ExprDateTime;

class Schedule extends Base{

    /**
     * @param BusSchedule $schedule
     * @return bool
     */
    public function hasWebBookingEnded($schedule)
    {
        $config = $this->getServiceManager()->get('Config');
        $now = time();
        //expire booking before 12 hours
        $EXIRATION_TIME = $config['system']['webBookingExpireTime'];

        //check if webbooking paramter set
        if(!empty($schedule->webBookingEndTime)){
            $bookingEndTime = ExprDateTime::getDateFromServices($schedule->webBookingEndTime)->getTimestamp();
            //true if the webbookingendTime is lower than current time
            return ($bookingEndTime < $now);
        }else{
            $departureTime = ExprDateTime::getDateFromServices($schedule->departureTime)->getTimestamp();

            return ((time()+$EXIRATION_TIME) > $departureTime);
        }
    }

    /**
     * @param BusSchedule $schedule
     * @return bool
     */
    public function hasTBBookingEnded($schedule)
    {
        $config = $this->getServiceManager()->get('Config');
        $now = time();
        $EXIRATION_TIME = $config['system']['TBBookingExpireTime'];

        //check if tbBookingEndTime paramter set
        if(!empty($schedule->tbBookingEndTime)){
            $bookingEndTime = ExprDateTime::getDateFromServices($schedule->tbBookingEndTime)->getTimestamp();
            //true if the tbBookingEndTime is lower than current time
            return ($bookingEndTime < $now);
        }else{
            $departureTime = ExprDateTime::getDateFromServices($schedule->departureTime)->getTimestamp();

            return ((time()+$EXIRATION_TIME) > $departureTime);
        }
    }

    /**
     * @param $schedule
     * @return \Api\Client\Soap\Core\BusSchedule
     */
    public function create($schedule)
    {
        $oSchedule = new \Api\Client\Rest\Model\Schedule($this->getServiceManager());
        return $oSchedule->create($schedule);
    }

    /**
     * @param null $scheduleId
     * @param array $params
     * @return \Api\Client\Soap\Core\BusSchedule
     */
    public function fetch($scheduleId = null, $params = array())
    {
        $oSchedule = new \Api\Client\Rest\Model\Schedule($this->getServiceManager());
        return $oSchedule->fetch($scheduleId, $params);
    }

    /**
     * @param $busStop
     * @return \Api\Client\Soap\Core\BusRouteBusStop
     */
    public function createBusStop($busStop)
    {
        $oSchedule = new \Api\Client\Rest\Model\Schedule($this->getServiceManager());
        return $oSchedule->createBusStop($busStop);
    }

    /**
     * @param $scheduleId
     * @param $seatNo
     * @param $journeyPerformed
     * @return \Api\Client\Soap\Core\anyType
     * @throws \Application\Exception\SessionTimeoutException
     * @throws \Exception
     */
    public function setJourneyPerformed($scheduleId, $seatNo, $journeyPerformed)
    {
        /** @var JourneyPerformedCriteria $oCriteria */
        $oCriteria = Base::getEntityObject('JourneyPerformedCriteria');
        $oCriteria->journeyPerformed = $journeyPerformed;
        $oCriteria->scheduleId = $scheduleId;
        $oCriteria->seatNumber = $seatNo;

        $oResponse = $this->getSearchService()->markJourneyPerformed($oCriteria);
        //check if status is okay
        if($this->responseIsValid($oResponse)){
            return $oResponse->data;
        }
    }

}