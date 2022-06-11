<?php

namespace Api\Client\Mobitel\MCash;

class chargeFromCustomerResponse
{

    /**
     * @var chargingResult $return
     * @access public
     */
    public $return = null;

    /**
     * @param chargingResult $return
     * @access public
     */
    public function __construct($return)
    {
      $this->return = $return;
    }

}
