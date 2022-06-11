<?php

namespace Api\Client\Soap\Core;

class FilterValue
{

    /**
     * @var string $name
     * @access public
     */
    public $name = null;

    /**
     * @var string $value
     * @access public
     */
    public $value = null;

    /**
     * @param string $name
     * @param string $value
     * @access public
     */
    public function __construct($name, $value)
    {
      $this->name = $name;
      $this->value = $value;
    }

}
