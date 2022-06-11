<?php

namespace Api\Client\Soap\Core;

class SearchResult
{

    /**
     * @var LegFilterData[] $legFilterData
     * @access public
     */
    public $legFilterData = null;

    /**
     * @var LegResult[] $legResults
     * @access public
     */
    public $legResults = null;

    /**
     * @access public
     */
    public function __construct()
    {
    
    }

}
