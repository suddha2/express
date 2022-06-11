<?php

namespace Api\Client\Soap\Core;

class ItemCancellationCriteria
{

    /**
     * @var int $bookingItemId
     * @access public
     */
    public $bookingItemId = null;

    /**
     * @param int $bookingItemId
     * @access public
     */
    public function __construct($bookingItemId)
    {
      $this->bookingItemId = $bookingItemId;
    }

}
