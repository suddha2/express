<?php

namespace Api\Client\Soap\Core;

include_once('ExpResponse.php');

class HoldResponse extends ExpResponse
{

    /**
     * @param anyType $data
     * @param string $message
     * @param int $status
     * @access public
     */
    public function __construct($data, $message, $status)
    {
      parent::__construct($data, $message, $status);
    }

}
