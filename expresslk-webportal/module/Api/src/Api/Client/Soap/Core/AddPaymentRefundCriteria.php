<?php

namespace Api\Client\Soap\Core;

class AddPaymentRefundCriteria
{

    /**
     * @var float $actualAmount
     * @access public
     */
    public $actualAmount = null;

    /**
     * @var string $actualCurrency
     * @access public
     */
    public $actualCurrency = null;

    /**
     * @var float $amount
     * @access public
     */
    public $amount = null;

    /**
     * @var int $bookingId
     * @access public
     */
    public $bookingId = null;

    /**
     * @var PaymentRefundMode $mode
     * @access public
     */
    public $mode = null;

    /**
     * @var string $reference
     * @access public
     */
    public $reference = null;

    /**
     * @var PaymentRefundType $type
     * @access public
     */
    public $type = null;

    /**
     * @var VendorPaymentRefundMode $vendorMode
     * @access public
     */
    public $vendorMode = null;

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
      $this->actualAmount = $actualAmount;
      $this->actualCurrency = $actualCurrency;
      $this->amount = $amount;
      $this->bookingId = $bookingId;
      $this->mode = $mode;
      $this->reference = $reference;
      $this->type = $type;
      $this->vendorMode = $vendorMode;
    }

}
