<?php

namespace Api\Client\Soap\Core;

class FilterData
{

    /**
     * @var string $type
     * @access public
     */
    public $type = null;

    /**
     * @var FilterValue[] $values
     * @access public
     */
    public $values = null;

    /**
     * @param string $type
     * @access public
     */
    public function __construct($type)
    {
      $this->type = $type;
    }

}
