<?php

namespace Api\Client\Soap\Core;

class AvailabilityResult
{

    /**
     * @var AgentAllocation[] $agentAllocations
     * @access public
     */
    public $agentAllocations = null;

    /**
     * @var Reservation[] $bookedSeats
     * @access public
     */
    public $bookedSeats = null;

    /**
     * @var BusSeat[] $seats
     * @access public
     */
    public $seats = null;

    /**
     * @var string[] $unavailableSeats
     * @access public
     */
    public $unavailableSeats = null;

    /**
     * @var string[] $vacantSeats
     * @access public
     */
    public $vacantSeats = null;

    /**
     * @access public
     */
    public function __construct()
    {
    
    }

}
