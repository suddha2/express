<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class BookingItemPassenger extends Entity
{

    /**
     * @var int $bookingItemId
     * @access public
     */
    public $bookingItemId = null;

    /**
     * @var Change[] $changes
     * @access public
     */
    public $changes = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var boolean $journeyPerformed
     * @access public
     */
    public $journeyPerformed = null;

    /**
     * @var int $passengerId
     * @access public
     */
    public $passengerId = null;

    /**
     * @var string $seatNumber
     * @access public
     */
    public $seatNumber = null;

    /**
     * @var BookingStatus $status
     * @access public
     */
    public $status = null;

    /**
     * @param int $bookingItemId
     * @param int $id
     * @param boolean $journeyPerformed
     * @param int $passengerId
     * @param string $seatNumber
     * @param BookingStatus $status
     * @access public
     */
    public function __construct($bookingItemId, $id, $journeyPerformed, $passengerId, $seatNumber, $status)
    {
      $this->bookingItemId = $bookingItemId;
      $this->id = $id;
      $this->journeyPerformed = $journeyPerformed;
      $this->passengerId = $passengerId;
      $this->seatNumber = $seatNumber;
      $this->status = $status;
    }

}
