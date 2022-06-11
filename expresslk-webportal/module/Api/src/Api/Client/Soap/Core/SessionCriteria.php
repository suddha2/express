<?php

namespace Api\Client\Soap\Core;

class SessionCriteria
{

    /**
     * @var string $locale
     * @access public
     */
    public $locale = null;

    /**
     * @param string $locale
     * @access public
     */
    public function __construct($locale)
    {
      $this->locale = $locale;
    }

}
