<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class BookingItemCancellation extends Entity
{

    /**
     * @var float $amount
     * @access public
     */
    public $amount = null;

    /**
     * @var int $bookingId
     * @access public
     */
    public $bookingId = null;

    /**
     * @var int $bookingItemId
     * @access public
     */
    public $bookingItemId = null;

    /**
     * @var int $cancellationSchemeId
     * @access public
     */
    public $cancellationSchemeId = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @param float $amount
     * @param int $bookingId
     * @param int $bookingItemId
     * @param int $cancellationSchemeId
     * @param int $id
     * @access public
     */
    public function __construct($amount, $bookingId, $bookingItemId, $cancellationSchemeId, $id)
    {
      $this->amount = $amount;
      $this->bookingId = $bookingId;
      $this->bookingItemId = $bookingItemId;
      $this->cancellationSchemeId = $cancellationSchemeId;
      $this->id = $id;
    }

}
