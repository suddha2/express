<?php

namespace Api\Client\Mobitel\CGW;

class msisdnInfo
{

    /**
     * @var string $msisdn
     * @access public
     */
    public $msisdn = null;

    /**
     * @param string $msisdn
     * @access public
     */
    public function __construct($msisdn)
    {
      $this->msisdn = $msisdn;
    }

}
