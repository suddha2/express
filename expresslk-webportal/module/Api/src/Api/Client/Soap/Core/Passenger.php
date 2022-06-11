<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class Passenger extends Entity
{

    /**
     * @var int $bookingId
     * @access public
     */
    public $bookingId = null;

    /**
     * @var Gender $gender
     * @access public
     */
    public $gender = null;

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
     * @var string $mobileTelephone
     * @access public
     */
    public $mobileTelephone = null;

    /**
     * @var string $name
     * @access public
     */
    public $name = null;

    /**
     * @var string $nic
     * @access public
     */
    public $nic = null;

    /**
     * @var PassengerType $passengerType
     * @access public
     */
    public $passengerType = null;

    /**
     * @param int $bookingId
     * @param Gender $gender
     * @param int $id
     * @param int $index
     * @param string $mobileTelephone
     * @param string $name
     * @param string $nic
     * @param PassengerType $passengerType
     * @access public
     */
    public function __construct($bookingId, $gender, $id, $index, $mobileTelephone, $name, $nic, $passengerType)
    {
      $this->bookingId = $bookingId;
      $this->gender = $gender;
      $this->id = $id;
      $this->index = $index;
      $this->mobileTelephone = $mobileTelephone;
      $this->name = $name;
      $this->nic = $nic;
      $this->passengerType = $passengerType;
    }

}
