<?php


namespace Application\Alert;


use Api\Client\Soap\Core\BusSchedule;
use Application\Domain;
use Application\Helper\ExprDateTime;
use Application\InjectorBase;

class Sms extends InjectorBase{

    /**
     * @param \Api\Client\Soap\Core\Booking $booking
     * @param $template
     */
    public function sendBookingSms($booking, $template, $sendTo = null)
    {
        /* @var $oSms \Application\Sms\Client */
        $oSms = $this->getServiceLocator()->get('Sms');
        /* @var $bookingItems \Api\Client\Soap\Core\BookingItem */
        $bookingItems = is_array($booking->bookingItems)? $booking->bookingItems[0] : $booking->bookingItems;
        $secDepartureTime = is_string($bookingItems->schedule->departureTime)? strtotime($bookingItems->schedule->departureTime) : floor($bookingItems->schedule->departureTime/1000);
        $secArrivalTime = is_string($bookingItems->schedule->arrivalTime)? strtotime($bookingItems->schedule->arrivalTime) : floor($bookingItems->schedule->arrivalTime/1000);

        $fromStopArrivalTime = $secDepartureTime;
        //get getin point time
        foreach ($bookingItems->schedule->scheduleStops as $scheduleStop) {
            //check from stop
            if($bookingItems->fromBusStop->id == $scheduleStop->stop->id){
                $fromStopArrivalTime = ExprDateTime::getDateFromServices($scheduleStop->departureTime)->getTimestamp();
            }
        }

        //params
        $beThereAt      = date('h:iA', ($fromStopArrivalTime - 1200));
        $departureTime  = date('h:i A', $secDepartureTime);
        $date           = date('jS F Y', $secDepartureTime);
        $price          = $bookingItems->price==0? $bookingItems->fare : $bookingItems->price; //if price is zero, display fare
        $bookingNo      = $booking->reference;
		$vcode 			= $booking->verficationCode;
        $clientTelephone= (is_null($sendTo) ? $booking->client->mobileTelephone : $sendTo );
        $clientEmail    = $booking->client->email;
        $seatNumbers    = array();

        //attach each tiket per passenger
        $passengers = is_array($bookingItems->passengers) ? $bookingItems->passengers : array($bookingItems->passengers);
        foreach($passengers as $passenger){
            /**
             * @var $passenger \Api\Client\Soap\Core\BookingItemPassenger
             */

            $seatNumbers[] = $passenger->seatNumber;
        }

        $oConf = $this->getServiceLocator()->get('config');
		
        /**
         * Send SMS
         */
        $aSmsData = array(
            '::SITE_NAME::' => \Application\Domain::getDomain(),
            '::PLATE_NO::' => $bookingItems->schedule->bus->plateNumber,
            '::DATE::' => date('Y.m.d', $secDepartureTime),
            // '::FROM_NAME::' => strtoupper($bookingItems->schedule->busRoute->fromCity->name),
            '::FROM_NAME::' => strtoupper($bookingItems->fromBusStop->name),
			'::GET_IN::' => strtoupper($bookingItems->fromBusStop->city->name) . '('. $beThereAt .')',
            '::DEPARTURE_TIME::' => $departureTime,
            //'::TO_NAME::' => strtoupper($bookingItems->schedule->busRoute->toCity->name),
            '::TO_NAME::' => strtoupper($bookingItems->toBusStop->name),
            '::SEAT_NO::' => implode(',', $seatNumbers),
            '::TICKET_NO::' => $bookingNo,
            '::FEE::' => $price,
            '::HOTLINE::' => $oConf['system']['hotlinePhone'],
            '::CONDUCTOR_MOBILE::' => $this->getBusContact($bookingItems->schedule),
			'::V_CODE::'=>$vcode,
        );
		$aSmsData = array(
			'::SITE_NAME::' => \Application\Domain::getDomain(),
            '::TICKET_NO::' => $bookingNo,
			'::V_CODE::'=>$vcode,
			'::ROUTE::'=>$bookingItems->schedule->busRoute->name,
			'::DATE::' => date('Y.m.d', $secDepartureTime),
			'::DEPARTURE_TIME::' => $departureTime,
			'::PLATE_NO::' => $bookingItems->schedule->bus->plateNumber,
			'::SEAT_NO::' => implode(',', $seatNumbers),
			'::GET_IN::' => strtoupper($bookingItems->fromBusStop->city->name) . '('. $beThereAt .')',  
			'::HOTLINE::' => '0772872877',			
        );
		
		if($template==Template::BOOKING_TENTETIVE){
			
			$microD2 = sprintf("%06d",($booking->expiryTime - floor($booking->expiryTime)) * 1000000);
			$dt2 = new DateTime( date('Y-m-d H:i:s.'.$microD2, $booking->expiryTime) );
			
			$aSmsData = array(
			'::SITE_NAME::' => \Application\Domain::getDomain(),
			'::TICKET_NO::' => $bookingNo,
			'::DATE::'=>date_format($dt2,'Y-m-d H:i:s'),
			'::FEE::' => $price+100,
			'::HOTLINE::' => '0772872877',
			);
		}
		
		
        $oSms->sendSMS($clientTelephone, $template, $aSmsData);
    }

    /**
     * @param $schedule BusSchedule
     * @return mixed
     */
    private function getBusContact($schedule) {
        if (! empty($schedule->bus->contact)) {
            return $schedule->bus->contact;
        }
        return $schedule->conductor->person->mobileTelephone;
    }
}