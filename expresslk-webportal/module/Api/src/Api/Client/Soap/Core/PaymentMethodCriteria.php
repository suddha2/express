<?php

namespace Api\Client\Soap\Core;

class PaymentMethodCriteria
{

    /**
     * @var dateTime $holdUntil
     * @access public
     */
    public $holdUntil = null;

    /**
     * @var PaymentMethod $paymentMethod
     * @access public
     */
    public $paymentMethod = null;

    /**
     * @param dateTime $holdUntil
     * @param PaymentMethod $paymentMethod
     * @access public
     */
    public function __construct($holdUntil, $paymentMethod)
    {
      $this->holdUntil = $holdUntil;
      $this->paymentMethod = $paymentMethod;
    }

}
