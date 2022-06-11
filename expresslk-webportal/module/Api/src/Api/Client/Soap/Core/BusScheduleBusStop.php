<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class BusScheduleBusStop extends Entity
{

    /**
     * @var dateTime $arrivalTime
     * @access public
     */
    public $arrivalTime = null;

    /**
     * @var dateTime $departureTime
     * @access public
     */
    public $departureTime = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var int $idx
     * @access public
     */
    public $idx = null;

    /**
     * @var int $scheduleId
     * @access public
     */
    public $scheduleId = null;

    /**
     * @var BusStop $stop
     * @access public
     */
    public $stop = null;

    /**
     * @param dateTime $arrivalTime
     * @param dateTime $departureTime
     * @param int $id
     * @param int $idx
     * @param int $scheduleId
     * @param BusStop $stop
     * @access public
     */
    public function __construct($arrivalTime, $departureTime, $id, $idx, $scheduleId, $stop)
    {
      $this->arrivalTime = $arrivalTime;
      $this->departureTime = $departureTime;
      $this->id = $id;
      $this->idx = $idx;
      $this->scheduleId = $scheduleId;
      $this->stop = $stop;
    }

}
