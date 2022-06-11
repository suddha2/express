<?php

namespace Api\Client\Soap\Core;

include_once('PaymentRefund.php');

class Refund extends PaymentRefund
{

    /**
     * @var int $allowedDivisions
     * @access public
     */
    public $allowedDivisions = null;

    /**
     * @var int $bookingId
     * @access public
     */
    public $bookingId = null;

    /**
     * @var int $writeAllowedDivisions
     * @access public
     */
    public $writeAllowedDivisions = null;

    /**
     * @param int $userId
     * @param int $allowedDivisions
     * @param int $bookingId
     * @param int $writeAllowedDivisions
     * @access public
     */
    public function __construct($userId, $allowedDivisions, $bookingId, $writeAllowedDivisions)
    {
      parent::__construct($userId);
      $this->allowedDivisions = $allowedDivisions;
      $this->bookingId = $bookingId;
      $this->writeAllowedDivisions = $writeAllowedDivisions;
    }

}
