<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class BookingItemDiscount extends Entity
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
     * @var int $discountSchemeId
     * @access public
     */
    public $discountSchemeId = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @param float $amount
     * @param int $bookingItemId
     * @param int $discountSchemeId
     * @param int $id
     * @access public
     */
    public function __construct($amount, $bookingItemId, $discountSchemeId, $id)
    {
      $this->amount = $amount;
      $this->bookingItemId = $bookingItemId;
      $this->discountSchemeId = $discountSchemeId;
      $this->id = $id;
    }

}
