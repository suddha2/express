<?php

namespace Api\Client\Soap\Core;

include_once('PersonDetail.php');

class PassengerDetail extends PersonDetail
{

    /**
     * @var int $index
     * @access public
     */
    public $index = null;

    /**
     * @var PassengerType $passengerType
     * @access public
     */
    public $passengerType = null;

    /**
     * @param int $index
     * @param PassengerType $passengerType
     * @access public
     */
    public function __construct($index, $passengerType)
    {
      parent::__construct();
      $this->index = $index;
      $this->passengerType = $passengerType;
    }

}
