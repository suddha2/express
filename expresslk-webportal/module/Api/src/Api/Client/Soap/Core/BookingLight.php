<?php

namespace Api\Client\Soap\Core;

include_once('LightEntity.php');

class BookingLight extends LightEntity
{

    /**
     * @var int $agent
     * @access public
     */
    public $agent = null;

    /**
     * @var int $allowedDivisions
     * @access public
     */
    public $allowedDivisions = null;

    /**
     * @var dateTime $bookingTime
     * @access public
     */
    public $bookingTime = null;

    /**
     * @var string $cancellationCause
     * @access public
     */
    public $cancellationCause = null;

    /**
     * @var float $chargeable
     * @access public
     */
    public $chargeable = null;

    /**
     * @var int $client
     * @access public
     */
    public $client = null;

    /**
     * @var string $clientMobileTelephone
     * @access public
     */
    public $clientMobileTelephone = null;

    /**
     * @var string $clientName
     * @access public
     */
    public $clientName = null;

    /**
     * @var string $clientNic
     * @access public
     */
    public $clientNic = null;

    /**
     * @var int $division
     * @access public
     */
    public $division = null;

    /**
     * @var dateTime $expiryTime
     * @access public
     */
    public $expiryTime = null;

    /**
     * @var string $fromCity
     * @access public
     */
    public $fromCity = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var string $reference
     * @access public
     */
    public $reference = null;

    /**
     * @var string $remarks
     * @access public
     */
    public $remarks = null;

    /**
     * @var string $status
     * @access public
     */
    public $status = null;

    /**
     * @var string $toCity
     * @access public
     */
    public $toCity = null;

    /**
     * @var int $user
     * @access public
     */
    public $user = null;

    /**
     * @var int $writeAllowedDivisions
     * @access public
     */
    public $writeAllowedDivisions = null;

    /**
     * @param int $agent
     * @param int $allowedDivisions
     * @param dateTime $bookingTime
     * @param string $cancellationCause
     * @param float $chargeable
     * @param int $client
     * @param string $clientMobileTelephone
     * @param string $clientName
     * @param string $clientNic
     * @param int $division
     * @param dateTime $expiryTime
     * @param string $fromCity
     * @param int $id
     * @param string $reference
     * @param string $remarks
     * @param string $status
     * @param string $toCity
     * @param int $user
     * @param int $writeAllowedDivisions
     * @access public
     */
    public function __construct($agent, $allowedDivisions, $bookingTime, $cancellationCause, $chargeable, $client, $clientMobileTelephone, $clientName, $clientNic, $division, $expiryTime, $fromCity, $id, $reference, $remarks, $status, $toCity, $user, $writeAllowedDivisions)
    {
      $this->agent = $agent;
      $this->allowedDivisions = $allowedDivisions;
      $this->bookingTime = $bookingTime;
      $this->cancellationCause = $cancellationCause;
      $this->chargeable = $chargeable;
      $this->client = $client;
      $this->clientMobileTelephone = $clientMobileTelephone;
      $this->clientName = $clientName;
      $this->clientNic = $clientNic;
      $this->division = $division;
      $this->expiryTime = $expiryTime;
      $this->fromCity = $fromCity;
      $this->id = $id;
      $this->reference = $reference;
      $this->remarks = $remarks;
      $this->status = $status;
      $this->toCity = $toCity;
      $this->user = $user;
      $this->writeAllowedDivisions = $writeAllowedDivisions;
    }

}
