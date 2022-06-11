<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class TravelClass extends Entity
{

    /**
     * @var string $code
     * @access public
     */
    public $code = null;

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
     * @var string $name
     * @access public
     */
    public $name = null;

    /**
     * @param string $code
     * @param string $description
     * @param int $id
     * @param string $name
     * @access public
     */
    public function __construct($code, $description, $id, $name)
    {
      $this->code = $code;
      $this->description = $description;
      $this->id = $id;
      $this->name = $name;
    }

}
