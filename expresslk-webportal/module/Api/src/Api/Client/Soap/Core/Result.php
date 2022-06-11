<?php

namespace Api\Client\Soap\Core;

include_once('BaseCostPrice.php');

class Result extends BaseCostPrice
{

    /**
     * @var string $resultIndex
     * @access public
     */
    public $resultIndex = null;

    /**
     * @param string $resultIndex
     * @access public
     */
    public function __construct($resultIndex)
    {
      parent::__construct();
      $this->resultIndex = $resultIndex;
    }

}
