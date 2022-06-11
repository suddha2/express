<?php

namespace Api\Client\Soap\Core;

class Allocation
{

    /**
     * @var int $passengerIndex
     * @access public
     */
    public $passengerIndex = null;

    /**
     * @var string $seatNumber
     * @access public
     */
    public $seatNumber = null;

    /**
     * @param int $passengerIndex
     * @param string $seatNumber
     * @access public
     */
    public function __construct($passengerIndex, $seatNumber)
    {
      $this->passengerIndex = $passengerIndex;
      $this->seatNumber = $seatNumber;
    }

}
