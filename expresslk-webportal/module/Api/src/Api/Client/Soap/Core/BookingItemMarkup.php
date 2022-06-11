<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class BookingItemMarkup extends Entity
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
     * @var boolean $isMargin
     * @access public
     */
    public $isMargin = null;

    /**
     * @var int $markupSchemeId
     * @access public
     */
    public $markupSchemeId = null;

    /**
     * @param float $amount
     * @param int $bookingItemId
     * @param int $id
     * @param boolean $isMargin
     * @param int $markupSchemeId
     * @access public
     */
    public function __construct($amount, $bookingItemId, $id, $isMargin, $markupSchemeId)
    {
      $this->amount = $amount;
      $this->bookingItemId = $bookingItemId;
      $this->id = $id;
      $this->isMargin = $isMargin;
      $this->markupSchemeId = $markupSchemeId;
    }

}
