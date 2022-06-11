<?php

namespace Api\Client\Soap\Core;

class AvailabilityCriteria
{

    /**
     * @var int $boardingLocationId
     * @access public
     */
    public $boardingLocationId = null;

    /**
     * @var int $busTypeId
     * @access public
     */
    public $busTypeId = null;

    /**
     * @var int $droppingLocationId
     * @access public
     */
    public $droppingLocationId = null;

    /**
     * @var string $resultIndex
     * @access public
     */
    public $resultIndex = null;

    /**
     * @var int $scheduleId
     * @access public
     */
    public $scheduleId = null;

    /**
     * @var int $sectorIndex
     * @access public
     */
    public $sectorIndex = null;

    /**
     * @param int $boardingLocationId
     * @param int $busTypeId
     * @param int $droppingLocationId
     * @param string $resultIndex
     * @param int $scheduleId
     * @param int $sectorIndex
     * @access public
     */
    public function __construct($boardingLocationId, $busTypeId, $droppingLocationId, $resultIndex, $scheduleId, $sectorIndex)
    {
      $this->boardingLocationId = $boardingLocationId;
      $this->busTypeId = $busTypeId;
      $this->droppingLocationId = $droppingLocationId;
      $this->resultIndex = $resultIndex;
      $this->scheduleId = $scheduleId;
      $this->sectorIndex = $sectorIndex;
    }

}
