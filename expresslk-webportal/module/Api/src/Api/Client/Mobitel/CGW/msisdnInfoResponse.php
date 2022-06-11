<?php

namespace Api\Client\Mobitel\CGW;

class msisdnInfoResponse
{

    /**
     * @var infoResult $return
     * @access public
     */
    public $return = null;

    /**
     * @param infoResult $return
     * @access public
     */
    public function __construct($return)
    {
      $this->return = $return;
    }

}
