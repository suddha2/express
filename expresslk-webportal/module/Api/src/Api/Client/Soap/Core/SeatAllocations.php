<?php

namespace Api\Client\Soap\Core;

class SeatAllocations
{

    /**
     * @var Allocation[] $allocations
     * @access public
     */
    public $allocations = null;

    /**
     * @var int $heldItemId
     * @access public
     */
    public $heldItemId = null;

    /**
     * @param int $heldItemId
     * @access public
     */
    public function __construct($heldItemId)
    {
      $this->heldItemId = $heldItemId;
    }

}
