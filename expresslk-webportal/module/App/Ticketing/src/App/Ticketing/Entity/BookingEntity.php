<?php


namespace App\Ticketing\Entity;


use Api\Client\Soap\Core\BookingItem;
use Api\Client\Soap\Core\BusStop;
use Api\Manager\Base;
use App\Base\Injector;
use Api\Client\Rest\Factory as RestFactory;

class BookingEntity extends Injector
{
    /**
     * Set boarding point for booking item
     * @param $bookingItemId
     * @param $boardingPointId
     */
    public function updateBoardingPoint($bookingItemId, $boardingPointId)
    {
        //get booking item from services
        $oBookingItemRest = new RestFactory($this->getServiceManager(), 'bookingItem');
        $oItemRest = $oBookingItemRest->getOne($bookingItemId);
        /** @var BookingItem $oBookingItem */
        $oBookingItem = $oItemRest->body;
        //flatten the object
        $oBookingItem = Base::prepareEntityToSave('BookingItem', $oBookingItem);
//var_dump($oBookingItem);die;
        //set new boarding point
        /** @var BusStop $oBoardingPoint */
        $oBoardingPoint = Base::getEntityObject('BusStop');
        $oBoardingPoint->id = $boardingPointId;
        //update booking item
        $oBookingItem->fromBusStop = $oBoardingPoint;

        //save item
        $oBookingItemRest->updateOne($bookingItemId, $oBookingItem);
    }
    
}