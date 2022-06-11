<?php

namespace Api\Client\Mobitel\MCash;

class reverseToCustomer
{

    /**
     * @var reversalRequest $reversalRequest
     * @access public
     */
    public $reversalRequest = null;

    /**
     * @param reversalRequest $reversalRequest
     * @access public
     */
    public function __construct($reversalRequest)
    {
      $this->reversalRequest = $reversalRequest;
    }

}
