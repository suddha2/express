<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class BusSeat extends Entity
{

    /**
     * @var int $busTypeId
     * @access public
     */
    public $busTypeId = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var string $number
     * @access public
     */
    public $number = null;

    /**
     * @var string $seatType
     * @access public
     */
    public $seatType = null;

    /**
     * @var int $x
     * @access public
     */
    public $x = null;

    /**
     * @var int $y
     * @access public
     */
    public $y = null;

    /**
     * @param int $busTypeId
     * @param int $id
     * @param string $number
     * @param string $seatType
     * @param int $x
     * @param int $y
     * @access public
     */
    public function __construct($busTypeId, $id, $number, $seatType, $x, $y)
    {
      $this->busTypeId = $busTypeId;
      $this->id = $id;
      $this->number = $number;
      $this->seatType = $seatType;
      $this->x = $x;
      $this->y = $y;
    }

}
