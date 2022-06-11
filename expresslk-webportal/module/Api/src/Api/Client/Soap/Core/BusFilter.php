<?php

namespace Api\Client\Soap\Core;

include_once('Filter.php');

class BusFilter extends Filter
{

    /**
     * @var int $busId
     * @access public
     */
    public $busId = null;

    /**
     * @param int $busId
     * @access public
     */
    public function __construct($busId)
    {
      $this->busId = $busId;
    }

}
