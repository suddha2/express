<?php

namespace Api\Client\Soap\Core;

include_once('Result.php');

class ResultLeg extends Result
{

    /**
     * @var ResultSector[] $sectors
     * @access public
     */
    public $sectors = null;

    /**
     * @param string $resultIndex
     * @access public
     */
    public function __construct($resultIndex)
    {
      parent::__construct($resultIndex);
    }

}
