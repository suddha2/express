<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class BusRoute extends Entity
{

    /**
     * @var string $displayNumber
     * @access public
     */
    public $displayNumber = null;

    /**
     * @var City $fromCity
     * @access public
     */
    public $fromCity = null;

    /**
     * @var boolean $genderRequired
     * @access public
     */
    public $genderRequired = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var string $name
     * @access public
     */
    public $name = null;

    /**
     * @var string $routeNumber
     * @access public
     */
    public $routeNumber = null;

    /**
     * @var BusRouteBusStop[] $routeStops
     * @access public
     */
    public $routeStops = null;

    /**
     * @var City $toCity
     * @access public
     */
    public $toCity = null;

    /**
     * @param string $displayNumber
     * @param City $fromCity
     * @param boolean $genderRequired
     * @param int $id
     * @param string $name
     * @param string $routeNumber
     * @param City $toCity
     * @access public
     */
    public function __construct($displayNumber, $fromCity, $genderRequired, $id, $name, $routeNumber, $toCity)
    {
      $this->displayNumber = $displayNumber;
      $this->fromCity = $fromCity;
      $this->genderRequired = $genderRequired;
      $this->id = $id;
      $this->name = $name;
      $this->routeNumber = $routeNumber;
      $this->toCity = $toCity;
    }

}
