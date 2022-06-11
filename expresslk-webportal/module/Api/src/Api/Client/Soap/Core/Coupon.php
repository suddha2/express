<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class Coupon extends Entity
{

    /**
     * @var boolean $active
     * @access public
     */
    public $active = null;

    /**
     * @var float $amount
     * @access public
     */
    public $amount = null;

    /**
     * @var int $clientId
     * @access public
     */
    public $clientId = null;

    /**
     * @var dateTime $expiryDate
     * @access public
     */
    public $expiryDate = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var dateTime $issueTime
     * @access public
     */
    public $issueTime = null;

    /**
     * @var string $number
     * @access public
     */
    public $number = null;

    /**
     * @param boolean $active
     * @param float $amount
     * @param int $clientId
     * @param dateTime $expiryDate
     * @param int $id
     * @param dateTime $issueTime
     * @param string $number
     * @access public
     */
    public function __construct($active, $amount, $clientId, $expiryDate, $id, $issueTime, $number)
    {
      $this->active = $active;
      $this->amount = $amount;
      $this->clientId = $clientId;
      $this->expiryDate = $expiryDate;
      $this->id = $id;
      $this->issueTime = $issueTime;
      $this->number = $number;
    }

}
