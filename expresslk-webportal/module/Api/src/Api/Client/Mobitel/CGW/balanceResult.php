<?php

namespace Api\Client\Mobitel\CGW;

class balanceResult
{

    /**
     * @var string $balance
     * @access public
     */
    public $balance = null;

    /**
     * @var string $msisdn
     * @access public
     */
    public $msisdn = null;

    /**
     * @var int $resultCode
     * @access public
     */
    public $resultCode = null;

    /**
     * @var string $resultDesc
     * @access public
     */
    public $resultDesc = null;

    /**
     * @param string $balance
     * @param string $msisdn
     * @param int $resultCode
     * @param string $resultDesc
     * @access public
     */
    public function __construct($balance, $msisdn, $resultCode, $resultDesc)
    {
      $this->balance = $balance;
      $this->msisdn = $msisdn;
      $this->resultCode = $resultCode;
      $this->resultDesc = $resultDesc;
    }

}
