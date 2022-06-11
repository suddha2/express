<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class BusBusRoute extends Entity
{

    /**
     * @var int $busId
     * @access public
     */
    public $busId = null;

    /**
     * @var BusRoute $busRoute
     * @access public
     */
    public $busRoute = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var float $loadFactor
     * @access public
     */
    public $loadFactor = null;

    /**
     * @param int $busId
     * @param BusRoute $busRoute
     * @param int $id
     * @param float $loadFactor
     * @access public
     */
    public function __construct($busId, $busRoute, $id, $loadFactor)
    {
      $this->busId = $busId;
      $this->busRoute = $busRoute;
      $this->id = $id;
      $this->loadFactor = $loadFactor;
    }

}
