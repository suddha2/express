<?php

namespace Api\Client\Soap\Core;

class charges
{

    /**
     * @var entry $entry
     * @access public
     */
    public $entry = null;

    /**
     * @param entry $entry
     * @access public
     */
    public function __construct($entry)
    {
      $this->entry = $entry;
    }

}
