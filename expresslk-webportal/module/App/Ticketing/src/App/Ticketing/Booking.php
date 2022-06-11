<?php


namespace App\Ticketing;


use Api\Operation\Request\QueryCriteria;
use App\Base\Injector;
use Api\Client\Rest\Factory as RestFactory;
use Application\Util;

class Booking extends Injector
{
    /**
     * @param $scheduleId
     */
    public function init($scheduleId)
    {
        try {
            /** @var Serializer $oSerializer */
            $oSerializer = $this->getServiceManager()->get('App\Ticketing\V1\Serializer');
            //clear saved bookings if exists
            $oSerializer->clearSaved($scheduleId);
        } catch (\Exception $e) {
        }
    }
    /**
     * Get all the bookings for a schedule
     * @param $scheduleId
     * @return \Api\Client\Soap\Core\Booking[]
     */
    public function getBookingsForSchedule($scheduleId)
    {
        //get bookings from API
        $oBookingRest = new RestFactory($this->getServiceManager(), 'booking/bySchedule/'. $scheduleId);
        $rBookings = $oBookingRest->getList(new QueryCriteria());
        /** @var \Api\Client\Soap\Core\Booking[] $aBookings */
        $aBookings = $rBookings->body;

        return $this->_prepareBookings($aBookings, $scheduleId);
    }

    /**
     * @param $reference
     * @return array
     * @throws \Exception
     */
    public function getByReference($reference)
    {
        $translator = $this->getServiceManager()->get('translator');
        /** @var $booking \Api\Manager\Booking */
        $booking = $this->getServiceManager()->get('Api\Manager\Booking');
        $bookingResult = $booking->getBookingRefById($reference);
        //validate
        if(!is_object($bookingResult) || !isset($bookingResult->reference)){
            throw new \Exception($translator->translate('Booking for reference does not exist.'));
        }
        return $this->_formatBooking($bookingResult);
    }

    /**
     * @param \Api\Client\Soap\Core\Booking[] $oBookings
     * @param int $scheduleId
     * @return array
     */
    private function _prepareBookings($oBookings, $scheduleId)
    {
        $response = array();
        /** @var Serializer $oSerializer */
        $oSerializer = $this->getServiceManager()->get('App\Ticketing\V1\Serializer');
        //get saved array for this schedule
        $oSavedBookings = $oSerializer->getSavedObject($scheduleId);

        $bookingList = array();
        foreach ($oBookings as $oBooking){
            $ref = $oBooking->reference;
            $formattedBooking = $this->_formatBooking(Util::ksortDeep($oBooking));

            //get hash from current booking
            $thisHash = $oSerializer->getHash($formattedBooking);
            //check retrieved array if there is a record for this reference
            if(isset($oSavedBookings[$ref]) && $oSavedBookings[$ref]['hash']==$thisHash){
                //has saved booking and hashes are equal. This booking has not changed

            }else{
                //booking is either new or has changed. Add to output array. False if cancelled
                $response[$ref] = $oBooking->status->code == \Api\Manager\Booking::STATUS_CODE_CANCELLED?
                    false : $formattedBooking;
            }
            //save
            $bookingList[$ref] = array(
                'hash' => $thisHash
            );
        }
        //save booking object
        $oSerializer->saveObject($scheduleId, $bookingList);

        return $response;
    }

    /**
     * @param \Api\Client\Soap\Core\Booking $oBooking
     * @return array
     */
    private function _formatBooking($oBooking)
    {
        $translator = $this->getServiceManager()->get('translator');
        $res = array(
            'reference' => $oBooking->reference,
            'bookingTime' => $oBooking->bookingTime,
            'client'=> array(
                'name'             => $oBooking->client->name,
                'email'            => $oBooking->client->email,
                'mobileTelephone'  => $oBooking->client->mobileTelephone,
                'nic'              => $oBooking->client->nic,
            ),
            'agent' => array(
                'name'             => (!empty($oBooking->agent)? $oBooking->agent->name : '')
            ),
            'user'  => array(
                'name' => $oBooking->user->firstName. ' ' .$oBooking->user->lastName,
            ),
            'bookingItems'  => array(),
            'remarks'       => $oBooking->remarks,
            'status'        => array(
                'code'  => $oBooking->status->code,
                'name'  => $oBooking->status->name,
            ),
        );

        //set up booking items
        $bookingsItems = array();
        foreach ($oBooking->bookingItems as $itemKey=> $bookingItem){
            $bookingsItems[$itemKey] = array(
                'id'        => $bookingItem->id,
                'fromBusStop'   => array(
                    'id'    => $bookingItem->fromBusStop->id,
                    'name'  => $translator->translate($bookingItem->fromBusStop->name),
                ),
                'toBusStop'   => array(
                    'id'    => $bookingItem->toBusStop->id,
                    'name'  => $translator->translate($bookingItem->toBusStop->name),
                ),
                'schedule'      => array(
                    'busRoute'  => array(
                        'name'  => $bookingItem->schedule->busRoute->name
                    ),
                    'departureTime' => $bookingItem->schedule->departureTime,
                    'arrivalTime' => $bookingItem->schedule->arrivalTime,
                )
            );

            //set up passngers
            foreach ($bookingItem->passengers as $passenger) {
                $bookingsItems[$itemKey]['passengers'][] = array(
                    'seatNumber'        => $passenger->seatNumber,
                    'journeyPerformed'  => $passenger->journeyPerformed,
                );
            }
        }
        $res['bookingItems'] = $bookingsItems;

        return $res;
    }

}