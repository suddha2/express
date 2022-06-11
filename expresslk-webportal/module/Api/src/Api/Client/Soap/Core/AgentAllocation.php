<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class AgentAllocation extends Entity
{

    /**
     * @var Agent $agent
     * @access public
     */
    public $agent = null;

    /**
     * @var int $allowedDivisions
     * @access public
     */
    public $allowedDivisions = null;

    /**
     * @var int $busRouteId
     * @access public
     */
    public $busRouteId = null;

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
     * @param int $allowedDivisions
     * @param int $busRouteId
     * @param int $id
     * @access public
     */
    public function __construct($agent, $allowedDivisions, $busRouteId, $id)
    {
      $this->agent = $agent;
      $this->allowedDivisions = $allowedDivisions;
      $this->busRouteId = $busRouteId;
      $this->id = $id;
    }

}
