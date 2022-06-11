<?php

namespace Api\Client\Soap\Core;

class Reservation
{

    /**
     * @var int $bookingAgentId
     * @access public
     */
    public $bookingAgentId = null;

    /**
     * @var float $bookingChargeable
     * @access public
     */
    public $bookingChargeable = null;

    /**
     * @var int $bookingId
     * @access public
     */
    public $bookingId = null;

    /**
     * @var string $bookingPaymentModes
     * @access public
     */
    public $bookingPaymentModes = null;

    /**
     * @var float $bookingPayments
     * @access public
     */
    public $bookingPayments = null;

    /**
     * @var string $bookingReference
     * @access public
     */
    public $bookingReference = null;

    /**
     * @var float $bookingRefunds
     * @access public
     */
    public $bookingRefunds = null;

    /**
     * @var string $bookingRemarks
     * @access public
     */
    public $bookingRemarks = null;

    /**
     * @var string $bookingStatusCode
     * @access public
     */
    public $bookingStatusCode = null;

    /**
     * @var dateTime $bookingTime
     * @access public
     */
    public $bookingTime = null;

    /**
     * @var int $bookingUserId
     * @access public
     */
    public $bookingUserId = null;

    /**
     * @var boolean $dummy
     * @access public
     */
    public $dummy = null;

    /**
     * @var string $email
     * @access public
     */
    public $email = null;

    /**
     * @var int $fromBusStopId
     * @access public
     */
    public $fromBusStopId = null;

    /**
     * @var string $gender
     * @access public
     */
    public $gender = null;

    /**
     * @var boolean $journeyPerformed
     * @access public
     */
    public $journeyPerformed = null;

    /**
     * @var string $mobile
     * @access public
     */
    public $mobile = null;

    /**
     * @var string $name
     * @access public
     */
    public $name = null;

    /**
     * @var string $nic
     * @access public
     */
    public $nic = null;

    /**
     * @var int $scheduleId
     * @access public
     */
    public $scheduleId = null;

    /**
     * @var string $seatNumber
     * @access public
     */
    public $seatNumber = null;

    /**
     * @var int $toBusStopId
     * @access public
     */
    public $toBusStopId = null;
	
	
	public $passengerType = null;

    /**
     * @param int $bookingAgentId
     * @param float $bookingChargeable
     * @param int $bookingId
     * @param string $bookingPaymentModes
     * @param float $bookingPayments
     * @param string $bookingReference
     * @param float $bookingRefunds
     * @param string $bookingRemarks
     * @param string $bookingStatusCode
     * @param dateTime $bookingTime
     * @param int $bookingUserId
     * @param boolean $dummy
     * @param string $email
     * @param int $fromBusStopId
     * @param string $gender
     * @param boolean $journeyPerformed
     * @param string $mobile
     * @param string $name
     * @param string $nic
     * @param int $scheduleId
     * @param string $seatNumber
     * @param int $toBusStopId
     * @access public
     */
    public function __construct($bookingAgentId, $bookingChargeable, $bookingId, $bookingPaymentModes, $bookingPayments, $bookingReference, $bookingRefunds, $bookingRemarks, $bookingStatusCode, $bookingTime, $bookingUserId, $dummy, $email, $fromBusStopId, $gender, $journeyPerformed, $mobile, $name, $nic, $scheduleId, $seatNumber, $toBusStopId, $passengerType)
    {
      $this->bookingAgentId = $bookingAgentId;
      $this->bookingChargeable = $bookingChargeable;
      $this->bookingId = $bookingId;
      $this->bookingPaymentModes = $bookingPaymentModes;
      $this->bookingPayments = $bookingPayments;
      $this->bookingReference = $bookingReference;
      $this->bookingRefunds = $bookingRefunds;
      $this->bookingRemarks = $bookingRemarks;
      $this->bookingStatusCode = $bookingStatusCode;
      $this->bookingTime = $bookingTime;
      $this->bookingUserId = $bookingUserId;
      $this->dummy = $dummy;
      $this->email = $email;
      $this->fromBusStopId = $fromBusStopId;
      $this->gender = $gender;
      $this->journeyPerformed = $journeyPerformed;
      $this->mobile = $mobile;
      $this->name = $name;
      $this->nic = $nic;
      $this->scheduleId = $scheduleId;
      $this->seatNumber = $seatNumber;
      $this->toBusStopId = $toBusStopId;
	  $this->passengerType = $passengerType;
    }

}
