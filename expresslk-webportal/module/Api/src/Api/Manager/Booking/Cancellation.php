<?php
/**
 * Created by PhpStorm.
 * User: kavinda
 * Date: 5/21/2015
 * Time: 2:35 PM
 */

namespace Api\Manager\Booking;


use Api\Client\Soap\Core\CancellationChargeResponse;
use Api\Client\Soap\Core\CancellationCriteria;
use Api\Client\Soap\Core\CancellationResponse;
use Api\Manager\Base;

class Cancellation extends Base{

    /**
     * @param $bookingId
     * @param bool $chargeCancellationFee
     * @param null $cause
     * @param null $remark
     * @return \Api\Client\Soap\Core\CancellationResponse
     * @throws \Application\Exception\SessionTimeoutException
     * @throws \Exception
     */
    public function cancelTicket($bookingId, $chargeCancellationFee = true, $cause = null, $remark = null){

        $criteria = new CancellationCriteria($bookingId, $cause, $chargeCancellationFee, $remark);
        // keeping this as null would convert to an
        // ArrayList with a null item in the web service
        $criteria->itemCriteria = array();
        $oConfirmResponse = $this->getSearchService()->cancel($criteria);
        //check if status is okay
        if($this->responseIsValid($oConfirmResponse)) {
            $data = $oConfirmResponse->data;

            return $data;
        }
    }

    /**
     * @param $bookingId
     * @param bool $chargeCancellationFee
     * @param null $cause
     * @param null $remark
     * @return mixed
     * @throws \Application\Exception\SessionTimeoutException
     * @throws \Exception
     */
    public function cancellationCharge($bookingId, $chargeCancellationFee = true, $cause = null, $remark = null){
        $criteria = new CancellationCriteria($bookingId, $cause, $chargeCancellationFee, $remark);

        //$charge = new CancellationChargeResponse($data, $message, $status);
        $criteria->itemCriteria = array();
        $oResponse = $this->getSearchService()->calculateCancellationCharge($criteria);

        if($this->responseIsValid($oResponse)) {
            $data = $oResponse->data->bookingItemCancellation;

            return $data;
        }
    }

    /**
     * @param \Api\Client\Soap\Core\Booking $booking
     * @param float $cancellationCharge
     * @return float
     */
    public static function getRefundAmount($booking, $cancellationCharge)
    {
        $price = $booking->chargeable;
        //get all the refunds
        $refunds = 0;
        foreach ($booking->refunds as $refund) {
            $refunds =+ $refund->amount;
        }
        return $price-$refunds-$cancellationCharge;
    }
}