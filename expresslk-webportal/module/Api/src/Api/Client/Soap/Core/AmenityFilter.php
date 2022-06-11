<?php

namespace Api\Client\Soap\Core;

include_once('Filter.php');

class AmenityFilter extends Filter
{

    /**
     * @var string $amenityCode
     * @access public
     */
    public $amenityCode = null;

    /**
     * @param string $amenityCode
     * @access public
     */
    public function __construct($amenityCode)
    {
      $this->amenityCode = $amenityCode;
    }

}
