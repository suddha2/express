<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class Agent extends Entity
{

    /**
     * @var int $allowedDivisions
     * @access public
     */
    public $allowedDivisions = null;

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
     * @param int $allowedDivisions
     * @param int $id
     * @param string $name
     * @access public
     */
    public function __construct($allowedDivisions, $id, $name)
    {
      $this->allowedDivisions = $allowedDivisions;
      $this->id = $id;
      $this->name = $name;
    }

}
