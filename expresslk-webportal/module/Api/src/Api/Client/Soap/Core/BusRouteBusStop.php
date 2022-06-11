<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class BusRouteBusStop extends Entity
{

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var int $index
     * @access public
     */
    public $index = null;

    /**
     * @var int $routeId
     * @access public
     */
    public $routeId = null;

    /**
     * @var BusStop $stop
     * @access public
     */
    public $stop = null;

    /**
     * @var dateTime $travelTime
     * @access public
     */
    public $travelTime = null;

    /**
     * @var dateTime $waitingTime
     * @access public
     */
    public $waitingTime = null;

    /**
     * @param int $id
     * @param int $index
     * @param int $routeId
     * @param BusStop $stop
     * @param dateTime $travelTime
     * @param dateTime $waitingTime
     * @access public
     */
    public function __construct($id, $index, $routeId, $stop, $travelTime, $waitingTime)
    {
      $this->id = $id;
      $this->index = $index;
      $this->routeId = $routeId;
      $this->stop = $stop;
      $this->travelTime = $travelTime;
      $this->waitingTime = $waitingTime;
    }

}
