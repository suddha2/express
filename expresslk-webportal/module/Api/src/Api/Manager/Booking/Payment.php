<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 11/23/14
 * Time: 9:29 PM
 */

namespace Api\Manager\Booking;


use Api\Client\Soap\Core\PaymentRefundCriteria;
use Api\Client\Soap\Core\PaymentRefundType;
use Api\Manager\Base;

class Payment extends Base{

    /**
     * @param $amount
     * @param $bookingId
     * @param $currency
     * @param $paymode
     * @return \Api\Client\Soap\Core\Payment
     * @throws \Exception
     */
    // public function addPayment($amount, $bookingId, $currency, $paymode, $reference = null)
    // {
        // $type = PaymentRefundType::Payment;

        // /** @var PaymentRefundCriteria $criteria */
        // $criteria = self::getEntityObject('PaymentRefundCriteria');//new PaymentRefundCriteria($amount, $bookingId, $currency, $paymode, $reference, $type);
        // $criteria->amount = $amount;
        // $criteria->actualAmount = $amount;
        // $criteria->actualCurrency = $currency;
        // $criteria->bookingId = $bookingId;
        // $criteria->mode = $paymode;
        // $criteria->reference = $reference;
        // $criteria->type = $type;

        // return $this->addPaymentRefund($criteria);
    // }
	/**
     * @param $amount
     * @param $bookingId
     * @param $currency
     * @param $paymode
     * @return \Api\Client\Soap\Core\Payment
     * @throws \Exception
     */
    public function addPayment($amount, $bookingId, $currency, $paymode, $reference = null,$totalAmount)
    {
        $type = PaymentRefundType::Payment;
		
		if ( empty($totalAmount)){
			$totalAmount = $amount;
		}
		
		
        /** @var PaymentRefundCriteria $criteria */
        $criteria = self::getEntityObject('PaymentRefundCriteria');//new PaymentRefundCriteria($amount, $bookingId, $currency, $paymode, $reference, $type);
        $criteria->amount = $amount;
        $criteria->actualAmount = $totalAmount;
        $criteria->actualCurrency = $currency;
        $criteria->bookingId = $bookingId;
        $criteria->mode = $paymode;
        $criteria->reference = $reference;
        $criteria->type = $type;

        return $this->addPaymentRefund($criteria);
    }
    /**
     * @param PaymentRefundCriteria $criteria
     * @return \Api\Client\Soap\Core\anyType
     */
    public function addPaymentRefund($criteria)
    {
        $oConfirmResponse = $this->getSearchService()->addPaymentRefund($criteria);
        //check if status is okay
        if($this->responseIsValid($oConfirmResponse)) {
            $data = $oConfirmResponse->data;

            return $data;
        }
    }

    /**
     * Add a refund record
     * @param $amount
     * @param $bookingId
     * @param $currency
     * @param $paymode
     * @param null $reference
     * @return \Api\Client\Soap\Core\anyType
     * @throws \Application\Exception\SessionTimeoutException
     * @throws \Exception
     */
    public function addRefund($amount, $bookingId, $currency, $paymode, $reference = null)
    {
        $type = PaymentRefundType::Refund;

        /** @var PaymentRefundCriteria $criteria */
        $criteria = self::getEntityObject('PaymentRefundCriteria');//new PaymentRefundCriteria($amount, $bookingId, $currency, $paymode, $reference, $type);
        $criteria->amount = $amount;
        $criteria->actualAmount = $amount;
        $criteria->actualCurrency = $currency;
        $criteria->bookingId = $bookingId;
        $criteria->mode = $paymode;
        $criteria->reference = $reference;
        $criteria->type = $type;

        $oConfirmResponse = $this->getSearchService()->addPaymentRefund($criteria);
        //check if status is okay
        if($this->responseIsValid($oConfirmResponse)) {
            $data = $oConfirmResponse->data;

            return $data;
        }
    }
} 
