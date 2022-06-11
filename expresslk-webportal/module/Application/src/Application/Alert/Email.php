<?php


namespace Application\Alert;


use Application\Domain;
use Application\Helper\ExprDateTime;
use Application\InjectorBase;
use Zend\Mime\Mime;
use Zend\Mime\Part;
use Zend\View\Model\ViewModel;

class Email extends InjectorBase{

    /**
     * @param \Api\Client\Soap\Core\Booking $booking
     * @throws \Exception
     */
    public function sendBookingSuccessEmail($booking)
    {
        set_time_limit(180);

        $oConf = $this->getServiceLocator()->get('config');
        $viewRender = $this->getServiceLocator()->get('ViewRenderer');
        /* @var $email \Application\Model\Email */
        $email = $this->getServiceLocator()->get('Email');
        // calender email
        /* @var $calenderEmail \Application\Model\Email */
        $calenderEmail = $this->getServiceLocator()->get('Email');

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
        $beThereAt      = date('h:i A', ($fromStopArrivalTime - 1200));
        $departureTime  = date('h:i A', $secDepartureTime);
        $date           = date('jS F Y', $secDepartureTime);
        $price          = $bookingItems->price==0? $bookingItems->fare : $bookingItems->price; //if price is zero, display fare
        $bookingNo      = $booking->reference;
		// Verification code 
		$vCode = $booking->verficationCode;
        $clientEmail    = $booking->client->email;
        $seatNumbers    = array();
        $siteName       = $oConf['system']['siteName'];
        $serverName     = $oConf['system']['serverName'];

        //add email recepients
        $email->addTo($clientEmail);
        $calenderEmail->addTo($clientEmail);

        //send confirmation only if bbk
        if(Domain::getDomain()==Domain::NAME_BUSBOOKING){
            $email->addBcc('dinusha@express.lk');
        }

        //attach each tiket per passenger
        $passengers = is_array($bookingItems->passengers) ? $bookingItems->passengers : array($bookingItems->passengers);
        foreach($passengers as $passenger){
            /**
             * @var $passenger \Api\Client\Soap\Core\BookingItemPassenger
             */

            $seatNumbers[] = $passenger->seatNumber;

            $oView = new ViewModel();
            $oView->setVariables(array(
                'beThereAt'         => $beThereAt,
                'travels'           => $bookingItems->schedule->bus->busType->type ,
                'busNumber'         => $bookingItems->schedule->bus->plateNumber,
                'departCity'        => $bookingItems->fromBusStop->name . '('. $bookingItems->fromBusStop->city->code .')',
                'boardingLocation'  => $bookingItems->fromBusStop->name,
                'seatNumber'        => $passenger->seatNumber,
                'seatType'          => '',
                'departureTime'     => $departureTime,
                'bookingNo'         => $bookingNo,
                'routeName'         => $bookingItems->schedule->busRoute->displayNumber,
                'date'              => $date,
                'arrivalCity'       => $bookingItems->toBusStop->name . '('. $bookingItems->toBusStop->city->code .')',
                'sequenceNo'        => $passenger->id,
                'busType'           => $bookingItems->schedule->bus->busType->type,
				'vCode'					=> $vCode,
            ));
            $oView->setTerminal(true);
            $oView->setTemplate('application/index/partial/ticket');

            $oDomPdf = new \DOMPDF();
            $oDomPdf->load_html($viewRender->render($oView));
            $oDomPdf->set_paper('A5', 'landscape');
            $oDomPdf->render();
            $oPdfString = $oDomPdf->output();
            //add attachement as pdf
            $attachment = new Part($oPdfString);
            $attachment->filename = "BBK-eTicket-". $bookingNo;
            $attachment->type = 'application/pdf';
            $attachment->encoding = Mime::ENCODING_BASE64;
            $attachment->disposition = Mime::DISPOSITION_ATTACHMENT;

            $email->addPart($attachment);
        }

        //set email body
        $bodyParams = array(
            '::bookingNo::'     => $bookingNo,
			'::vCode::'			=> $vCode,
            '::departCity::'    => $bookingItems->fromBusStop->city->name,
            '::arrivalCity::'   => $bookingItems->toBusStop->city->name,
            '::date::'          => $date,
            '::departureTime::' => $departureTime,
            '::boardingLocation::' => $bookingItems->fromBusStop->name,
            '::beThereAt::'     => $beThereAt,
            '::travels::'       => $bookingItems->schedule->bus->supplier->name,
            '::busNumber::'     => $bookingItems->schedule->bus->plateNumber,
            '::seatNumber::'    => implode(',', $seatNumbers),
            '::totalAmount::'   => $price,
            '::cost::'          => $bookingItems->cost,
            '::bookingCharges::' => ($bookingItems->grossPrice - $bookingItems->cost),
            '::SITE_NAME::'     => $siteName,
        );
        $email->setLayout(\Application\Model\Email::TEMPLATE_BOOKING_SUCCESS)
            ->setSubject($siteName. ' booking confirmation : '. $bookingNo)
            ->setBodyHtml($bodyParams);
        //calendar email
        $calenderEmail->setLayout(\Application\Model\Email::TEMPLATE_BOOKING_SUCCESS)
            ->setSubject($siteName. ' booking confirmation event : '. $bookingNo)
            ->setBodyHtml($bodyParams);

        /**
         * Create ICS file to include journey details
         */
        $calDescription = 'Journey with ::SITE_NAME::\\n'.
            'Reference ::bookingNo::\\n'.
            'Service Route: ::departCity:: - ::arrivalCity::\\n'.
            'Journey Date: ::date::\\n'.
            'Journey Time: ::departureTime::\\n'.
            'Boarding Point: ::boardingLocation::\\n'.
            'Please reach your boarding place by: ::beThereAt::\\n'.
            'Service Provider: ::travels::\\n'.
            'Coach/ Bus Number: ::busNumber::\\n'.
            'Seat No: ::seatNumber::\\n'.
            'Total Fee: Rs. ::totalAmount::';

        $cal = 'BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//hacksw/handcal//NONSGML v1.0//EN
CALSCALE:GREGORIAN
BEGIN:VEVENT
DTEND:'. ExprDateTime::unixToiCal($secArrivalTime, 0) .'
UID:'. uniqid() .'
DTSTAMP:'. date('Ymd\THis\Z') .'
ORGANIZER;CN='. $siteName . ':mailto:info@busbooking.lk
LOCATION:'. $bookingItems->fromBusStop->name .', Sri Lanka
DESCRIPTION:'. str_replace(array_keys($bodyParams), array_values($bodyParams), $calDescription) .'
URL;VALUE=URI:http://'. $serverName . '
SUMMARY:'. $siteName . ' : '. $bookingItems->fromBusStop->city->name .' - '. $bookingItems->toBusStop->city->name .' : '. $bookingNo .'
DTSTART:'. ExprDateTime::unixToiCal($secDepartureTime, 0) .'
END:VEVENT
END:VCALENDAR';
        $att = new Part($cal);
        $att->filename = 'BookingCal.ics';
        $att->type = 'text/calendar';
        $calenderEmail->addPart($att);

        //send email if email is defined
        if($clientEmail){
            $email->send();
            $calenderEmail->send();
        }
    }
}