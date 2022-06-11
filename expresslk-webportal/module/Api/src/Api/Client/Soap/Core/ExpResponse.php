<?php

namespace Api\Client\Soap\Core;

class ExpResponse
{

    /**
     * @var anyType $data
     * @access public
     */
    public $data = null;

    /**
     * @var string $message
     * @access public
     */
    public $message = null;

    /**
     * @var int $status
     * @access public
     */
    public $status = null;

    /**
     * @param anyType $data
     * @param string $message
     * @param int $status
     * @access public
     */
    public function __construct($data, $message, $status)
    {
      $this->data = $data;
      $this->message = $message;
      $this->status = $status;
    }

}
