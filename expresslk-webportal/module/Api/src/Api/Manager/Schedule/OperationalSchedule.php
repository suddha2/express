<?php


namespace Api\Manager\Schedule;


use Api\Client\Soap\Core\BusSchedule;
use Api\Client\Soap\Core\NotificationMethod;
use Api\Client\Soap\Core\OperationalStage;
use Api\Manager\Base;
use Application\Alert\Schedule\ConductorSms;
use Application\Alert\Schedule\PassengerSms;
use Application\Helper\ExprDateTime;
use Application\Sms\Template;
use Api\Client\Rest\Factory as RestFactory;

class OperationalSchedule extends Base
{
    const  STAGE_CODE_TBO = 'TBO'; //To be opened for bookings
    const  STAGE_CODE_OFB = 'OFB'; //Opened for bookings
    const  STAGE_CODE_CFB = 'CFB'; //Closed for bookings
    const  STAGE_CODE_BN = 'BN'; //Bus notified
    const  STAGE_CODE_PP = 'PP'; //Payment processed

    /**
     * @param $scheduleId
     * @param $stageCode
     */
    public function updateStage($scheduleId, $stageCode)
    {
        /* @var $oSchedule \Api\Manager\Schedule */
        $oSchedule = $this->getServiceManager()->get('Api\Manager\Schedule');
        //get current schedule
        /** @var BusSchedule $oThisSchedule */
        $oThisSchedule = $oSchedule->fetch($scheduleId);
        //current bu notify method
        $sBusNotifyMethod = $oThisSchedule->bus->notificationMethod;

        //flatten the object
        $oScheduleToSave = Base::prepareEntityToSave('BusSchedule', $oThisSchedule);

        //set stage of the schedule
        /** @var OperationalStage $oScehduleStage */
        $oScehduleStage = Base::getEntityObject('OperationalStage');
        $oScehduleStage->code = $stageCode;
        $oScheduleToSave->stage = $oScehduleStage;

        //call relavant method for the stage
        switch ($oScheduleToSave->stage->code){
            /**
             * If next stage is closed stage, send notifications to conductor and passengers
             */
            case self::STAGE_CODE_CFB:
                //close online bookings if web booking end time is greater than current time
                if(empty($oScheduleToSave->webBookingEndTime)
                    || ExprDateTime::getDateFromServices($oScheduleToSave->webBookingEndTime)->getTimestamp() > time()){
                    $oScheduleToSave->webBookingEndTime = (new ExprDateTime())->getTimestampMiliSeconds();
                }
                //close ticketbox bookings if conductor informed
                if(empty($oScheduleToSave->tbBookingEndTime)
                    || ExprDateTime::getDateFromServices($oScheduleToSave->tbBookingEndTime)->getTimestamp() > time()){
                    $oScheduleToSave->tbBookingEndTime = (new ExprDateTime())->getTimestampMiliSeconds();
                }

                /**
                 * If the bus notification method is SMS. Send sms to Conductor and admin, informing bookings.
                 */
                //if($sBusNotifyMethod == NotificationMethod::Sms){
                    /** @var ConductorSms $oConductorSms */
                    $oConductorSms = $this->getServiceManager()->get('Alert\Schedule\ConductorSms');
                    $oConductorSms->informBookingsOfSchedule($oThisSchedule, Template::CONDUCTOR_SCHEDULE_HAS_BOOKINGS);
                //}

                /**
                 * send sms to customer of conductor phone number
                 * @var PassengerSms $oPassSms
                 */
                $oPassSms = $this->getServiceManager()->get('Alert\Schedule\PassengerSms');
                $oPassSms->alertPassengersOfSchedule($oThisSchedule, Template::PASSENGER_SCHEDULE);

                break;
        }

        //update schedule.
        $oScheduleService = new RestFactory($this->getServiceManager(), 'busSchedule');
        $oScheduleService->updateOne($oScheduleToSave->id, $oScheduleToSave);
    }

}