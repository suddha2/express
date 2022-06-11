<?php

namespace Api\Client\Mobitel\CGW;

class infoResult
{

    /**
     * @var string $accountStatus
     * @access public
     */
    public $accountStatus = null;

    /**
     * @var string $connectionType
     * @access public
     */
    public $connectionType = null;

    /**
     * @var string $msisdn
     * @access public
     */
    public $msisdn = null;

    /**
     * @var string $packageCode
     * @access public
     */
    public $packageCode = null;

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
     * @param string $accountStatus
     * @param string $connectionType
     * @param string $msisdn
     * @param string $packageCode
     * @param int $resultCode
     * @param string $resultDesc
     * @access public
     */
    public function __construct($accountStatus, $connectionType, $msisdn, $packageCode, $resultCode, $resultDesc)
    {
      $this->accountStatus = $accountStatus;
      $this->connectionType = $connectionType;
      $this->msisdn = $msisdn;
      $this->packageCode = $packageCode;
      $this->resultCode = $resultCode;
      $this->resultDesc = $resultDesc;
    }

}
