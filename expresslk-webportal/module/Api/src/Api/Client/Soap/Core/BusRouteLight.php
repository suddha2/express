<?php

namespace Api\Client\Soap\Core;

include_once('LightEntity.php');

class BusRouteLight extends LightEntity
{

    /**
     * @var string $displayNumber
     * @access public
     */
    public $displayNumber = null;

    /**
     * @var int $fromCity
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
     * @var int $toCity
     * @access public
     */
    public $toCity = null;

    /**
     * @param string $displayNumber
     * @param int $fromCity
     * @param boolean $genderRequired
     * @param int $id
     * @param string $name
     * @param string $routeNumber
     * @param int $toCity
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
