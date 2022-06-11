<?php

namespace Api\Client\Soap\Core;

class JourneyPerformedCriteria
{

    /**
     * @var boolean $journeyPerformed
     * @access public
     */
    public $journeyPerformed = null;

    /**
     * @var int $scheduleId
     * @access public
     */
    public $scheduleId = null;

    /**
     * @var string $seatNumber
     * @access public
     */
    public $seatNumber = null;

    /**
     * @param boolean $journeyPerformed
     * @param int $scheduleId
     * @param string $seatNumber
     * @access public
     */
    public function __construct($journeyPerformed, $scheduleId, $seatNumber)
    {
      $this->journeyPerformed = $journeyPerformed;
      $this->scheduleId = $scheduleId;
      $this->seatNumber = $seatNumber;
    }

}
