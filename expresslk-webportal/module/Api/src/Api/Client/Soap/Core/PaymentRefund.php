<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class PaymentRefund extends Entity
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
     * @var int $id
     * @access public
     */
    public $id = null;

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
     * @var dateTime $time
     * @access public
     */
    public $time = null;

    /**
     * @var PaymentRefundType $type
     * @access public
     */
    public $type = null;

    /**
     * @var int $userId
     * @access public
     */
    public $userId = null;

    /**
     * @var VendorPaymentRefundMode $vendorMode
     * @access public
     */
    public $vendorMode = null;

    /**
     * @param int $userId
     * @access public
     */
    public function __construct($userId)
    {
      $this->userId = $userId;
    }

}
