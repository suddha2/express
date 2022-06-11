<?php

namespace Api\Client\Mobitel\CGW;

class checkPrepaidBalanceResponse
{

    /**
     * @var balanceResult $return
     * @access public
     */
    public $return = null;

    /**
     * @param balanceResult $return
     * @access public
     */
    public function __construct($return)
    {
      $this->return = $return;
    }

}
