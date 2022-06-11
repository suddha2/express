<?php


namespace App\Ticketing\Schedule;


use App\Base\Injector;

class Availability extends Injector
{
    /**
     * @param $session
     * @param $boardingLocation
     * @param $dropoffLocation
     * @param $adults
     * @param $busTypeId
     * @param $children
     * @param $infants
     * @param $scheduleId
     * @return \Api\Client\Soap\Core\anyType
     */
    public function getSeatAvailability($session, $boardingLocation, $dropoffLocation, $adults, $busTypeId, $children, $infants, $scheduleId)
    {
        /**
         * @var $scheduleAvailability \Api\Manager\Schedule\Availability
         */
        $scheduleAvailability = $this->getServiceManager()->get('Api\Manager\Schedule\Availability');
        $scheduleAvailability->setShowDetailed(true); //show full details about bookings
        $scheduleAvailability->setSession($session);
        $seats = $scheduleAvailability->getResult($boardingLocation, $dropoffLocation, $adults, $busTypeId, $children, $infants, $scheduleId);
        //format result
        $this->formatSeats($seats);

        return $seats;
    }

    private function formatSeats(&$seats)
    {
        foreach ($seats as &$seat) {
            //if not available but not booked, show as available
            if($seat['available']===false && $seat['booked']===false){
                $seat['available'] = true;
            }
        }
    }
}