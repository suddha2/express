<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class BookingItemCharge extends Entity
{

    /**
     * @var float $amount
     * @access public
     */
    public $amount = null;

    /**
     * @var int $chargeSchemeId
     * @access public
     */
    public $chargeSchemeId = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @param float $amount
     * @param int $chargeSchemeId
     * @param int $id
     * @access public
     */
    public function __construct($amount, $chargeSchemeId, $id)
    {
      $this->amount = $amount;
      $this->chargeSchemeId = $chargeSchemeId;
      $this->id = $id;
    }

}
