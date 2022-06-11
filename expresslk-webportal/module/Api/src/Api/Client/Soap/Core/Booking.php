<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class Booking extends Entity
{

    /**
     * @var Agent $agent
     * @access public
     */
    public $agent = null;

    /**
     * @var int $allowedDivisions
     * @access public
     */
    public $allowedDivisions = null;

    /**
     * @var BookingItem[] $bookingItems
     * @access public
     */
    public $bookingItems = null;

    /**
     * @var dateTime $bookingTime
     * @access public
     */
    public $bookingTime = null;

    /**
     * @var CancellationCause $cancellationCause
     * @access public
     */
    public $cancellationCause = null;

    /**
     * @var BookingItemCancellation[] $cancellations
     * @access public
     */
    public $cancellations = null;

    /**
     * @var Change[] $changes
     * @access public
     */
    public $changes = null;

    /**
     * @var float $chargeable
     * @access public
     */
    public $chargeable = null;

    /**
     * @var Client $client
     * @access public
     */
    public $client = null;

    /**
     * @var Division $division
     * @access public
     */
    public $division = null;

    /**
     * @var dateTime $expiryTime
     * @access public
     */
    public $expiryTime = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var Passenger[] $passengers
     * @access public
     */
    public $passengers = null;

    /**
     * @var Payment[] $payments
     * @access public
     */
    public $payments = null;

    /**
     * @var string $reference
     * @access public
     */
    public $reference = null;

    /**
     * @var Refund[] $refunds
     * @access public
     */
    public $refunds = null;

    /**
     * @var string $remarks
     * @access public
     */
    public $remarks = null;

    /**
     * @var BookingStatus $status
     * @access public
     */
    public $status = null;

    /**
     * @var User $user
     * @access public
     */
    public $user = null;

    /**
     * @var int $writeAllowedDivisions
     * @access public
     */
    public $writeAllowedDivisions = null;
	
	
	/**
     * @var string $verficationCode
     * @access public
     */
	public $verficationCode = null;
	

    /**
     * @param Agent $agent
     * @param int $allowedDivisions
     * @param dateTime $bookingTime
     * @param CancellationCause $cancellationCause
     * @param float $chargeable
     * @param Client $client
     * @param Division $division
     * @param dateTime $expiryTime
     * @param int $id
     * @param string $reference
     * @param string $remarks
     * @param BookingStatus $status
     * @param User $user
     * @param int $writeAllowedDivisions
     * @access public
     */
    public function __construct($agent, $allowedDivisions, $bookingTime, $cancellationCause, $chargeable, $client, $division, $expiryTime, $id, $reference, $remarks, $status, $user, $writeAllowedDivisions,$verficationCode)
    {
      $this->agent = $agent;
      $this->allowedDivisions = $allowedDivisions;
      $this->bookingTime = $bookingTime;
      $this->cancellationCause = $cancellationCause;
      $this->chargeable = $chargeable;
      $this->client = $client;
      $this->division = $division;
      $this->expiryTime = $expiryTime;
      $this->id = $id;
      $this->reference = $reference;
      $this->remarks = $remarks;
      $this->status = $status;
      $this->user = $user;
      $this->writeAllowedDivisions = $writeAllowedDivisions;
	  $this->verficationCode = $verficationCode;
    }

}
