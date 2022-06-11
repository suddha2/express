<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class BookingItemTax extends Entity
{

    /**
     * @var float $amount
     * @access public
     */
    public $amount = null;

    /**
     * @var int $bookingItemId
     * @access public
     */
    public $bookingItemId = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var int $taxSchemeId
     * @access public
     */
    public $taxSchemeId = null;

    /**
     * @param float $amount
     * @param int $bookingItemId
     * @param int $id
     * @param int $taxSchemeId
     * @access public
     */
    public function __construct($amount, $bookingItemId, $id, $taxSchemeId)
    {
      $this->amount = $amount;
      $this->bookingItemId = $bookingItemId;
      $this->id = $id;
      $this->taxSchemeId = $taxSchemeId;
    }

}
