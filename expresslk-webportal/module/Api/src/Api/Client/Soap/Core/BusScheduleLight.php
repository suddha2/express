<?php

namespace Api\Client\Soap\Core;

include_once('LightEntity.php');

class BusScheduleLight extends LightEntity
{

    /**
     * @var boolean $active
     * @access public
     */
    public $active = null;

    /**
     * @var int $allowedDivisions
     * @access public
     */
    public $allowedDivisions = null;

    /**
     * @var dateTime $arrivalTime
     * @access public
     */
    public $arrivalTime = null;

    /**
     * @var boolean $bookingAllowed
     * @access public
     */
    public $bookingAllowed = null;

    /**
     * @var int $bus
     * @access public
     */
    public $bus = null;

    /**
     * @var string $busPlateNumber
     * @access public
     */
    public $busPlateNumber = null;

    /**
     * @var int $busRoute
     * @access public
     */
    public $busRoute = null;

    /**
     * @var string $busRouteName
     * @access public
     */
    public $busRouteName = null;

    /**
     * @var int $conductor
     * @access public
     */
    public $conductor = null;

    /**
     * @var dateTime $departureTime
     * @access public
     */
    public $departureTime = null;

    /**
     * @var int $driver
     * @access public
     */
    public $driver = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var float $loadFactor
     * @access public
     */
    public $loadFactor = null;

    /**
     * @var int $seatingProfile
     * @access public
     */
    public $seatingProfile = null;

    /**
     * @var int $stage
     * @access public
     */
    public $stage = null;

    /**
     * @var dateTime $tbBookingEndTime
     * @access public
     */
    public $tbBookingEndTime = null;

    /**
     * @var dateTime $terminalInTime
     * @access public
     */
    public $terminalInTime = null;

    /**
     * @var dateTime $ticketingActiveTime
     * @access public
     */
    public $ticketingActiveTime = null;

    /**
     * @var dateTime $webBookingEndTime
     * @access public
     */
    public $webBookingEndTime = null;

    /**
     * @param boolean $active
     * @param int $allowedDivisions
     * @param dateTime $arrivalTime
     * @param boolean $bookingAllowed
     * @param int $bus
     * @param string $busPlateNumber
     * @param int $busRoute
     * @param string $busRouteName
     * @param int $conductor
     * @param dateTime $departureTime
     * @param int $driver
     * @param int $id
     * @param float $loadFactor
     * @param int $seatingProfile
     * @param int $stage
     * @param dateTime $tbBookingEndTime
     * @param dateTime $terminalInTime
     * @param dateTime $ticketingActiveTime
     * @param dateTime $webBookingEndTime
     * @access public
     */
    public function __construct($active, $allowedDivisions, $arrivalTime, $bookingAllowed, $bus, $busPlateNumber, $busRoute, $busRouteName, $conductor, $departureTime, $driver, $id, $loadFactor, $seatingProfile, $stage, $tbBookingEndTime, $terminalInTime, $ticketingActiveTime, $webBookingEndTime)
    {
      $this->active = $active;
      $this->allowedDivisions = $allowedDivisions;
      $this->arrivalTime = $arrivalTime;
      $this->bookingAllowed = $bookingAllowed;
      $this->bus = $bus;
      $this->busPlateNumber = $busPlateNumber;
      $this->busRoute = $busRoute;
      $this->busRouteName = $busRouteName;
      $this->conductor = $conductor;
      $this->departureTime = $departureTime;
      $this->driver = $driver;
      $this->id = $id;
      $this->loadFactor = $loadFactor;
      $this->seatingProfile = $seatingProfile;
      $this->stage = $stage;
      $this->tbBookingEndTime = $tbBookingEndTime;
      $this->terminalInTime = $terminalInTime;
      $this->ticketingActiveTime = $ticketingActiveTime;
      $this->webBookingEndTime = $webBookingEndTime;
    }

}
