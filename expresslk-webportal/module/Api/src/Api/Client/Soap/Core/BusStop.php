<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class BusStop extends Entity
{

    /**
     * @var City $city
     * @access public
     */
    public $city = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var float $latitude
     * @access public
     */
    public $latitude = null;

    /**
     * @var float $longitude
     * @access public
     */
    public $longitude = null;

    /**
     * @var string $name
     * @access public
     */
    public $name = null;

    /**
     * @var string $nameSi
     * @access public
     */
    public $nameSi = null;

    /**
     * @param City $city
     * @param int $id
     * @param float $latitude
     * @param float $longitude
     * @param string $name
     * @param string $nameSi
     * @access public
     */
    public function __construct($city, $id, $latitude, $longitude, $name, $nameSi)
    {
      $this->city = $city;
      $this->id = $id;
      $this->latitude = $latitude;
      $this->longitude = $longitude;
      $this->name = $name;
      $this->nameSi = $nameSi;
    }

}
