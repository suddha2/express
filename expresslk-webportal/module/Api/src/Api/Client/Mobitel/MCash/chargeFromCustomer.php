<?php

namespace Api\Client\Mobitel\MCash;

class chargeFromCustomer
{

    /**
     * @var chargingRequest $chargingRequest
     * @access public
     */
    public $chargingRequest = null;

    /**
     * @param chargingRequest $chargingRequest
     * @access public
     */
    public function __construct($chargingRequest)
    {
      $this->chargingRequest = $chargingRequest;
    }

}
