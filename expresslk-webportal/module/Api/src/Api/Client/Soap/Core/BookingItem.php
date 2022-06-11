<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class BookingItem extends Entity
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
     * @var BookingItemCharge[] $charges
     * @access public
     */
    public $charges = null;

    /**
     * @var float $cost
     * @access public
     */
    public $cost = null;

    /**
     * @var BookingItemDiscount[] $discounts
     * @access public
     */
    public $discounts = null;

    /**
     * @var float $fare
     * @access public
     */
    public $fare = null;

    /**
     * @var BusStop $fromBusStop
     * @access public
     */
    public $fromBusStop = null;

    /**
     * @var float $grossPrice
     * @access public
     */
    public $grossPrice = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var int $index
     * @access public
     */
    public $index = null;

    /**
     * @var BookingItemMarkup[] $markups
     * @access public
     */
    public $markups = null;

    /**
     * @var BookingItemPassenger[] $passengers
     * @access public
     */
    public $passengers = null;

    /**
     * @var float $price
     * @access public
     */
    public $price = null;

    /**
     * @var float $priceBeforeCharge
     * @access public
     */
    public $priceBeforeCharge = null;

    /**
     * @var float $priceBeforeTax
     * @access public
     */
    public $priceBeforeTax = null;

    /**
     * @var string $remarks
     * @access public
     */
    public $remarks = null;

    /**
     * @var BusSchedule $schedule
     * @access public
     */
    public $schedule = null;

    /**
     * @var BookingStatus $status
     * @access public
     */
    public $status = null;

    /**
     * @var BookingItemTax[] $taxes
     * @access public
     */
    public $taxes = null;

    /**
     * @var BusStop $toBusStop
     * @access public
     */
    public $toBusStop = null;

    /**
     * @var int $writeAllowedDivisions
     * @access public
     */
    public $writeAllowedDivisions = null;

    /**
     * @param int $allowedDivisions
     * @param int $bookingId
     * @param CancellationCause $cancellationCause
     * @param float $cost
     * @param float $fare
     * @param BusStop $fromBusStop
     * @param float $grossPrice
     * @param int $id
     * @param int $index
     * @param float $price
     * @param float $priceBeforeCharge
     * @param float $priceBeforeTax
     * @param string $remarks
     * @param BusSchedule $schedule
     * @param BookingStatus $status
     * @param BusStop $toBusStop
     * @param int $writeAllowedDivisions
     * @access public
     */
    public function __construct($allowedDivisions, $bookingId, $cancellationCause, $cost, $fare, $fromBusStop, $grossPrice, $id, $index, $price, $priceBeforeCharge, $priceBeforeTax, $remarks, $schedule, $status, $toBusStop, $writeAllowedDivisions)
    {
      $this->allowedDivisions = $allowedDivisions;
      $this->bookingId = $bookingId;
      $this->cancellationCause = $cancellationCause;
      $this->cost = $cost;
      $this->fare = $fare;
      $this->fromBusStop = $fromBusStop;
      $this->grossPrice = $grossPrice;
      $this->id = $id;
      $this->index = $index;
      $this->price = $price;
      $this->priceBeforeCharge = $priceBeforeCharge;
      $this->priceBeforeTax = $priceBeforeTax;
      $this->remarks = $remarks;
      $this->schedule = $schedule;
      $this->status = $status;
      $this->toBusStop = $toBusStop;
      $this->writeAllowedDivisions = $writeAllowedDivisions;
    }

}
