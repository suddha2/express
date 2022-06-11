<?php


namespace Application\Alert\Schedule;


use Api\Client\Soap\Core\BusSchedule;
use Api\Manager\Booking;
use Api\Operation\Request\QueryCriteria;
use Application\InjectorBase;
use Api\Client\Rest\Factory as RestFactory;
use Application\Sms\Template;

class PassengerSms extends InjectorBase
{

    /**
     * @param $oSchedule BusSchedule
     * @param $smsTemplate
     */
    public function alertPassengersOfSchedule($oSchedule, $smsTemplate)
    {
        //get bookings from API
        $oBookingRest = new RestFactory($this->getServiceLocator(), 'booking/bySchedule/'. $oSchedule->id);
        $rBookings = $oBookingRest->getList(new QueryCriteria());
        /** @var \Api\Client\Soap\Core\Booking[] $aBookings */
        $aBookings = $rBookings->body;

        foreach ($aBookings as $aBooking) {
            //only confirmed bookings with mobile number
            if($aBooking->status->code == Booking::STATUS_CODE_CONFIRM
                && !empty($aBooking->client->mobileTelephone)){
                try {
                    /**
                     * get sms object
                     * @var $oSms \Application\Alert\Sms
                     */
                    $oSms = $this->getServiceLocator()->get('Alert\Sms');
                    $oSms->sendBookingSms($aBooking, $smsTemplate);
                } catch (\Exception $e) {
                    //send alert to admin if sms failed
                    $alertBody = '
                            <h2>Client Inform SMS failed due to</h2><pre>'. $e->getMessage() .'</pre>
                            <h2>SMS to</h2><pre>'. $aBooking->client->mobileTelephone .'</pre>
                            ';
                    $oAlert = $this->getServiceLocator()->get('Application\Util\Alert');
                    $oAlert->sendEmailToAdmin($alertBody, 'ExpressBot - Sending sms failed on bus notify');
                }
            }
        }
    }
}