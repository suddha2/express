<?php

namespace Api\Client\Mobitel\CGW;

class helloResponse
{

    /**
     * @var string $return
     * @access public
     */
    public $return = null;

    /**
     * @param string $return
     * @access public
     */
    public function __construct($return)
    {
      $this->return = $return;
    }

}
