<?php

namespace Api\Client\Mobitel\CGW;

class chgRequest
{

    /**
     * @var int $amt
     * @access public
     */
    public $amt = null;

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
     * @var string $serviceId
     * @access public
     */
    public $serviceId = null;

    /**
     * @var string $transactionId
     * @access public
     */
    public $transactionId = null;

    /**
     * @param int $amt
     * @param string $contentId
     * @param string $msisdn
     * @param string $serviceId
     * @param string $transactionId
     * @access public
     */
    public function __construct($amt, $contentId, $msisdn, $serviceId, $transactionId)
    {
      $this->amt = $amt;
      $this->contentId = $contentId;
      $this->msisdn = $msisdn;
      $this->serviceId = $serviceId;
      $this->transactionId = $transactionId;
    }

}
