<?php

namespace Api\Client\Mobitel\MCash;

class reverseToCustomerResponse
{

    /**
     * @var reversalResult $return
     * @access public
     */
    public $return = null;

    /**
     * @param reversalResult $return
     * @access public
     */
    public function __construct($return)
    {
      $this->return = $return;
    }

}
