<?php

namespace Api\Client\Mobitel\CGW;

class chgResult
{

    /**
     * @var string $contentId
     * @access public
     */
    public $contentId = null;

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
     * @var string $transactionId
     * @access public
     */
    public $transactionId = null;

    /**
     * @param string $contentId
     * @param string $msisdn
     * @param int $resultCode
     * @param string $resultDesc
     * @param string $transactionId
     * @access public
     */
    public function __construct($contentId, $msisdn, $resultCode, $resultDesc, $transactionId)
    {
      $this->contentId = $contentId;
      $this->msisdn = $msisdn;
      $this->resultCode = $resultCode;
      $this->resultDesc = $resultDesc;
      $this->transactionId = $transactionId;
    }

}
