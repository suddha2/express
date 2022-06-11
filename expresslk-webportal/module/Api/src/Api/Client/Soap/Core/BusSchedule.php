<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class BusSchedule extends Entity
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
     * @var Bus $bus
     * @access public
     */
    public $bus = null;

    /**
     * @var BusRoute $busRoute
     * @access public
     */
    public $busRoute = null;

    /**
     * @var Conductor $conductor
     * @access public
     */
    public $conductor = null;

    /**
     * @var dateTime $departureTime
     * @access public
     */
    public $departureTime = null;

    /**
     * @var Driver $driver
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
     * @var BusScheduleBusStop[] $scheduleStops
     * @access public
     */
    public $scheduleStops = null;

    /**
     * @var SeatingProfile $seatingProfile
     * @access public
     */
    public $seatingProfile = null;

    /**
     * @var OperationalStage $stage
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
     * @param Bus $bus
     * @param BusRoute $busRoute
     * @param Conductor $conductor
     * @param dateTime $departureTime
     * @param Driver $driver
     * @param int $id
     * @param float $loadFactor
     * @param SeatingProfile $seatingProfile
     * @param OperationalStage $stage
     * @param dateTime $tbBookingEndTime
     * @param dateTime $terminalInTime
     * @param dateTime $ticketingActiveTime
     * @param dateTime $webBookingEndTime
     * @access public
     */
    public function __construct($active, $allowedDivisions, $arrivalTime, $bookingAllowed, $bus, $busRoute, $conductor, $departureTime, $driver, $id, $loadFactor, $seatingProfile, $stage, $tbBookingEndTime, $terminalInTime, $ticketingActiveTime, $webBookingEndTime)
    {
      $this->active = $active;
      $this->allowedDivisions = $allowedDivisions;
      $this->arrivalTime = $arrivalTime;
      $this->bookingAllowed = $bookingAllowed;
      $this->bus = $bus;
      $this->busRoute = $busRoute;
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
