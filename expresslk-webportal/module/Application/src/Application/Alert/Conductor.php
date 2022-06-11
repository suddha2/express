<?php


namespace Application\Alert;


use Api\Client\Soap\Core\BusSchedule;
use Application\InjectorBase;
use Api\Client\Rest\Factory as RestFactory;

class Conductor extends InjectorBase{

    public function sendClosedBookings($scheduleId)
    {
        //get schedule based on id
        $oSchedule = new RestFactory($this->getServiceLocator(), 'busSchedule');
        /** @var BusSchedule $oneSchedule */
        $oneSchedule = $oSchedule->getOne($scheduleId);
        //get conda's detail from schedule
        $conductorPhone = $oneSchedule->conductor->person->mobileTelephone;

        //get bookings for schedule
        $oBookings = new RestFactory($this->getServiceLocator(), 'booking');
    }
}