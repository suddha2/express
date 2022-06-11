<?php

namespace Api\Client\Mobitel\MCash;

class reversalResult
{

    /**
     * @var float $amount
     * @access public
     */
    public $amount = null;

    /**
     * @var string $ext1
     * @access public
     */
    public $ext1 = null;

    /**
     * @var string $ext2
     * @access public
     */
    public $ext2 = null;

    /**
     * @var string $merchantId
     * @access public
     */
    public $merchantId = null;

    /**
     * @var string $merchantRefNo
     * @access public
     */
    public $merchantRefNo = null;

    /**
     * @var string $mobileNo
     * @access public
     */
    public $mobileNo = null;

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
     * @param float $amount
     * @param string $ext1
     * @param string $ext2
     * @param string $merchantId
     * @param string $merchantRefNo
     * @param string $mobileNo
     * @param int $resultCode
     * @param string $resultDesc
     * @param string $transactionId
     * @access public
     */
    public function __construct($amount, $ext1, $ext2, $merchantId, $merchantRefNo, $mobileNo, $resultCode, $resultDesc, $transactionId)
    {
      $this->amount = $amount;
      $this->ext1 = $ext1;
      $this->ext2 = $ext2;
      $this->merchantId = $merchantId;
      $this->merchantRefNo = $merchantRefNo;
      $this->mobileNo = $mobileNo;
      $this->resultCode = $resultCode;
      $this->resultDesc = $resultDesc;
      $this->transactionId = $transactionId;
    }

}
