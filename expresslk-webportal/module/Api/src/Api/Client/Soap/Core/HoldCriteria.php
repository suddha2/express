<?php

namespace Api\Client\Soap\Core;

class HoldCriteria
{

    /**
     * @var int $boardingPoint
     * @access public
     */
    public $boardingPoint = null;

    /**
     * @var int $droppingPoint
     * @access public
     */
    public $droppingPoint = null;

    /**
     * @var string $resultIndex
     * @access public
     */
    public $resultIndex = null;

    /**
     * @var SeatCriteria[] $seatCriterias
     * @access public
     */
    public $seatCriterias = null;

    /**
     * @param int $boardingPoint
     * @param int $droppingPoint
     * @param string $resultIndex
     * @access public
     */
    public function __construct($boardingPoint, $droppingPoint, $resultIndex)
    {
      $this->boardingPoint = $boardingPoint;
      $this->droppingPoint = $droppingPoint;
      $this->resultIndex = $resultIndex;
    }

}
