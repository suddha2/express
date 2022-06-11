<?php


namespace App\Ticketing\Entity;


use Api\Manager\Schedule;
use App\Base\Injector;

class ScheduleEntity extends Injector
{
    /**
     * @param $scheduleId
     * @param $seatNo
     * @param $journeyPerformed
     */
    public function setJourneyPerformed($scheduleId, $seatNo, $journeyPerformed)
    {
        /** @var Schedule $oCoreSchedule */
        $oCoreSchedule = $this->getServiceManager()->get('Api\Manager\Schedule');
        $oCoreSchedule->setJourneyPerformed($scheduleId, $seatNo, $journeyPerformed);
    }
}