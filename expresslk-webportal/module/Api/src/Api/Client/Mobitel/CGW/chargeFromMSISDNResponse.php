<?php

namespace Api\Client\Mobitel\CGW;

class chargeFromMSISDNResponse
{

    /**
     * @var chgResult $return
     * @access public
     */
    public $return = null;

    /**
     * @param chgResult $return
     * @access public
     */
    public function __construct($return)
    {
      $this->return = $return;
    }

}
