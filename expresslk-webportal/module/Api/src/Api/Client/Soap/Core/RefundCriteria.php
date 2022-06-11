<?php

namespace Api\Client\Soap\Core;

include_once('PaymentRefundCriteria.php');

class RefundCriteria extends PaymentRefundCriteria
{

    /**
     * @param float $actualAmount
     * @param string $actualCurrency
     * @param float $amount
     * @param int $bookingId
     * @param PaymentRefundMode $mode
     * @param string $reference
     * @param PaymentRefundType $type
     * @param VendorPaymentRefundMode $vendorMode
     * @access public
     */
    public function __construct($actualAmount, $actualCurrency, $amount, $bookingId, $mode, $reference, $type, $vendorMode)
    {
      parent::__construct($actualAmount, $actualCurrency, $amount, $bookingId, $mode, $reference, $type, $vendorMode);
    }

}
