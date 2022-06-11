<?php

namespace Api\Client\Soap\Core;

include_once('Filter.php');

class TravelClassFilter extends Filter
{

    /**
     * @var string[] $travelClasses
     * @access public
     */
    public $travelClasses = null;

    /**
     * @access public
     */
    public function __construct()
    {
    
    }

}
