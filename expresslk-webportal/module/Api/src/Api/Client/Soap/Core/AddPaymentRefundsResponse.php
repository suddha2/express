<?php

namespace Api\Client\Soap\Core;

include_once('ExpResponse.php');

class AddPaymentRefundsResponse extends ExpResponse
{

    /**
     * @var Coupon $coupon
     * @access public
     */
    public $coupon = null;

    /**
     * @param anyType $data
     * @param string $message
     * @param int $status
     * @param Coupon $coupon
     * @access public
     */
    public function __construct($data, $message, $status, $coupon)
    {
      parent::__construct($data, $message, $status);
      $this->coupon = $coupon;
    }

}
