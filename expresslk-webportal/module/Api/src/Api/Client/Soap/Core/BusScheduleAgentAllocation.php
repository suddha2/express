<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class BusScheduleAgentAllocation extends Entity
{

    /**
     * @var Agent $agent
     * @access public
     */
    public $agent = null;

    /**
     * @var int $busScheduleId
     * @access public
     */
    public $busScheduleId = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var string[] $seatNumbers
     * @access public
     */
    public $seatNumbers = null;

    /**
     * @param Agent $agent
     * @param int $busScheduleId
     * @param int $id
     * @access public
     */
    public function __construct($agent, $busScheduleId, $id)
    {
      $this->agent = $agent;
      $this->busScheduleId = $busScheduleId;
      $this->id = $id;
    }

}
