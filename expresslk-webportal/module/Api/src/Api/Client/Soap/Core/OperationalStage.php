<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class OperationalStage extends Entity
{

    /**
     * @var string $code
     * @access public
     */
    public $code = null;

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
     * @var int $ordinal
     * @access public
     */
    public $ordinal = null;

    /**
     * @param string $code
     * @param int $id
     * @param string $name
     * @param int $ordinal
     * @access public
     */
    public function __construct($code, $id, $name, $ordinal)
    {
      $this->code = $code;
      $this->id = $id;
      $this->name = $name;
      $this->ordinal = $ordinal;
    }

}
