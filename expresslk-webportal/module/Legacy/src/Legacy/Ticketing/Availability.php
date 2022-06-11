<?php


namespace Legacy\Ticketing;


use Application\InjectorBase;

class Availability extends InjectorBase
{
    /**
     * @param $session
     * @param $boardingLocation
     * @param $dropoffLocation
     * @param $busTypeId
     * @param $scheduleId
     * @return \Api\Client\Soap\Core\anyType
     */
    public function getResult($session, $boardingLocation, $dropoffLocation, $busTypeId, $scheduleId)
    {
        $children   = 0;
        $infants    = 0;
        $adults     = 1;

        /**
         * @var $scheduleAvailability \Api\Manager\Schedule\Availability
         */
        $scheduleAvailability = $this->getServiceLocator()->get('Api\Manager\Schedule\Availability');
        $scheduleAvailability->setSession($session);
        $seats = $scheduleAvailability->getResult($boardingLocation, $dropoffLocation, $adults, $busTypeId, $children, $infants, $scheduleId);
        return $seats;
    }
}