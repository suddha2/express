<?php


namespace Application\Alert\Schedule;


use Api\Client\Soap\Core\BusSchedule;
use Api\Manager\Booking;
use Application\Helper\ExprDateTime;
use Application\InjectorBase;
use Api\Client\Rest\Factory as RestFactory;
use Api\Operation\Request\QueryCriteria;
use Application\Sms\Template;

class ConductorSms extends InjectorBase
{

    /**
     * @param $oSchedule BusSchedule
     * @param $smsTemplate
     */
    public function informBookingsOfSchedule($oSchedule, $smsTemplate)
    {
        //get bookings from API
        $oBookingRest = new RestFactory($this->getServiceLocator(), 'booking/bySchedule/'. $oSchedule->id);
        $rBookings = $oBookingRest->getList(new QueryCriteria());
        /** @var \Api\Client\Soap\Core\Booking[] $aBookings */
        $aBookings = $rBookings->body;

        $aSeatsInfo = array();
        $iNumberofSeats = 0;
        foreach ($aBookings as $aBooking) {
            //only confirmed bookings with mobile number
            if($aBooking->status->code == Booking::STATUS_CODE_CONFIRM){
                $aSeats = array();
                //get seat numbers
                foreach ($aBooking->bookingItems as $bookingItem) {
                    foreach ($bookingItem->passengers as $oPassenger){
                        $aSeats[] = $oPassenger->seatNumber;
                        $iNumberofSeats++;
                    }
                }
                //save one row for a booking. Seats - Phone - Boarding place
                $aSeatsInfo[] = implode(',', $aSeats) . ' - ' . $aBooking->client->mobileTelephone . ' - ' . strtoupper($aBooking->bookingItems[0]->fromBusStop->city->name);
            }
        }

        $secDepartureTime = ExprDateTime::getDateFromServices($oSchedule->departureTime)->getTimestamp();
        $iBusAdminContact = $oSchedule->bus->adminContact;
        $iBusContact = empty($oSchedule->bus->contact)? $oSchedule->bus->conductor->person->mobileTelephone : $oSchedule->bus->contact;
        /**
         * Send SMS to conductor
         */
        $aSmsData = array(
            '::PLATE_NO::'      => $oSchedule->bus->plateNumber,
            '::DATE::'          => date('Y-m-d',$secDepartureTime),
            '::FROM_NAME::'     => strtoupper($oSchedule->busRoute->fromCity->name),
            '::TO_NAME::'       => strtoupper($oSchedule->busRoute->toCity->name),
            '::DEPARTURE_TIME::' => date('h:i A', $secDepartureTime),
            '::SEAT_PASSENGER_DATA::'     => implode(PHP_EOL, $aSeatsInfo),
            '::SEAT_COUNT::'     => $iNumberofSeats,
        );
        /* @var $oSms \Application\Sms\Client */
        $oSms = $this->getServiceLocator()->get('Sms');

        //Bus admin contact
        try {
            if(!empty($iBusAdminContact)){
                $oSms->sendSMS($iBusAdminContact, $smsTemplate, $aSmsData);
            }
        } catch (\Exception $e) {
            //send alert to admin if sms failed
            $alertBody = '
                            <h2>BusOwner SMS failed due to</h2><pre>'. $e->getMessage() .'</pre>
                            <h2>SMS sent to</h2><pre>'. $iBusAdminContact .'</pre>
                            <h2>SMS body</h2><pre>'. var_export($aSmsData, true) .'</pre>
                            ';
            $oAlert = $this->getServiceLocator()->get('Application\Util\Alert');
            $oAlert->sendEmailToAdmin($alertBody, 'ExpressBot - Sending sms failed for Conductor');
        }

        //conductor
        try {
            if(!empty($iBusContact)){
                $oSms->sendSMS($iBusContact, $smsTemplate, $aSmsData);
            }
        } catch (\Exception $e) {
            //send alert to admin if sms failed
            $alertBody = '
                            <h2>Conductor SMS failed due to</h2><pre>'. $e->getMessage() .'</pre>
                            <h2>SMS sent to</h2><pre>'. $iBusContact .'</pre>
                            <h2>SMS body</h2><pre>'. var_export($aSmsData, true) .'</pre>
                            ';
            $oAlert = $this->getServiceLocator()->get('Application\Util\Alert');
            $oAlert->sendEmailToAdmin($alertBody, 'ExpressBot - Sending sms failed for Conductor');
        }
    }
}