<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class SeatingProfile extends Entity
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
     * @var string $name
     * @access public
     */
    public $name = null;

    /**
     * @var string[] $seatNumbers
     * @access public
     */
    public $seatNumbers = null;

    /**
     * @param int $busTypeId
     * @param int $id
     * @param string $name
     * @access public
     */
    public function __construct($busTypeId, $id, $name)
    {
      $this->busTypeId = $busTypeId;
      $this->id = $id;
      $this->name = $name;
    }

}
