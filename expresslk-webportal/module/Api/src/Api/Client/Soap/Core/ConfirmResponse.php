<?php

namespace Api\Client\Soap\Core;

include_once('ExpResponse.php');

class ConfirmResponse extends ExpResponse
{

    /**
     * @var PaymentRefundsResponse $paymentResponse
     * @access public
     */
    public $paymentResponse = null;

    /**
     * @param anyType $data
     * @param string $message
     * @param int $status
     * @access public
     */
    public function __construct($data, $message, $status)
    {
      parent::__construct($data, $message, $status);
    }

}
