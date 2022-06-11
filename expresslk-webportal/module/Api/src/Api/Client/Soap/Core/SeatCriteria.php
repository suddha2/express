<?php

namespace Api\Client\Soap\Core;

class SeatCriteria
{

    /**
     * @var string[] $seats
     * @access public
     */
    public $seats = null;

    /**
     * @var int $sectorNumber
     * @access public
     */
    public $sectorNumber = null;

    /**
     * @param int $sectorNumber
     * @access public
     */
    public function __construct($sectorNumber)
    {
      $this->sectorNumber = $sectorNumber;
    }

}
