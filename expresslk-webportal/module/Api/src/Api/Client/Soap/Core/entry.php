<?php

namespace Api\Client\Soap\Core;

class entry
{

    /**
     * @var int $key
     * @access public
     */
    public $key = null;

    /**
     * @var float $value
     * @access public
     */
    public $value = null;

    /**
     * @param int $key
     * @param float $value
     * @access public
     */
    public function __construct($key, $value)
    {
      $this->key = $key;
      $this->value = $value;
    }

}
