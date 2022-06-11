<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 11/5/14
 * Time: 2:24 PM
 */

namespace Api\Manager\Schedule;


use Api\Client\Soap\Core\HoldCriteria;
use Api\Client\Soap\Core\SeatCriteria;
use Api\Client\Soap\Core\intArray;
use Api\Manager\Base;
use Api\Client\Rest\Model\City;

class Hold extends Base{

    protected $_session = null;

    /**
     * set session variable
     * @param $session
     */
    public function setSession($session)
    {
        $this->_session = $session;
    }

    /**
     * Send hold request to the api
     * @param $boardingLocation
     * @param $droppingLocation
     * @param $resultIndex
     * @param $seats
     * @return \Api\Client\Soap\Core\HoldResult
     * @throws \Exception
     */
    public function executeHold($boardingLocation, $droppingLocation, $resultIndex, $seats)
    {
        //seat criteris
        $oSeatCriteria = new SeatCriteria(0);
        $oSeatCriteria->seats = $seats;

        $holdCriteria = new HoldCriteria($boardingLocation['id'], $droppingLocation['id'], $resultIndex);
        $holdCriteria->seatCriterias = array($oSeatCriteria);

        $oHoldResponse = $this->getSearchService()->hold($this->_session, $holdCriteria);

        //check if status is okay
        if($this->responseIsValid($oHoldResponse)){
            $data = $oHoldResponse->data;
            return $data;
        }
    }

    /**
     * @param $sessionId
     * @param $heldItemIds
     * @return \Api\Client\Soap\Core\anyType
     * @throws \Application\Exception\SessionTimeoutException
     * @throws \Exception
     */
    public function releaseHolded($sessionId, $heldItemIds)
    {
        if(!is_array($heldItemIds)){
            $heldItemIds = array($heldItemIds);
        }
        $itemIds = new intArray();
        $itemIds->item = $heldItemIds;

        $oHoldResponse = $this->getSearchService()->release($sessionId, $itemIds);
        //check if status is okay
        if($this->responseIsValid($oHoldResponse)){
            $data = $oHoldResponse->data;
            return $data;
        }
    }

}