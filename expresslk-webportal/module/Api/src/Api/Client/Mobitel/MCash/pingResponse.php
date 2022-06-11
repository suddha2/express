<?php

namespace Api\Client\Mobitel\MCash;

class pingResponse
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
