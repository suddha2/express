<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class BusType extends Entity
{

    /**
     * @var string $description
     * @access public
     */
    public $description = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var int $seatingCapacity
     * @access public
     */
    public $seatingCapacity = null;

    /**
     * @var string $type
     * @access public
     */
    public $type = null;

    /**
     * @param string $description
     * @param int $id
     * @param int $seatingCapacity
     * @param string $type
     * @access public
     */
    public function __construct($description, $id, $seatingCapacity, $type)
    {
      $this->description = $description;
      $this->id = $id;
      $this->seatingCapacity = $seatingCapacity;
      $this->type = $type;
    }

}
