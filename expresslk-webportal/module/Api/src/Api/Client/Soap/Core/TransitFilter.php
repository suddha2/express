<?php

namespace Api\Client\Soap\Core;

include_once('Filter.php');

class TransitFilter extends Filter
{

    /**
     * @var int $maxTransits
     * @access public
     */
    public $maxTransits = null;

    /**
     * @param int $maxTransits
     * @access public
     */
    public function __construct($maxTransits)
    {
      $this->maxTransits = $maxTransits;
    }

}
