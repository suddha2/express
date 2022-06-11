<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class Parameter extends Entity
{

    /**
     * @var string $param
     * @access public
     */
    public $param = null;

    /**
     * @var ValueType $type
     * @access public
     */
    public $type = null;

    /**
     * @var string[] $values
     * @access public
     */
    public $values = null;

    /**
     * @param string $param
     * @param ValueType $type
     * @access public
     */
    public function __construct($param, $type)
    {
      $this->param = $param;
      $this->type = $type;
    }

}
