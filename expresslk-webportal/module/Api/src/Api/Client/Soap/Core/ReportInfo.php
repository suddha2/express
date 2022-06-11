<?php

namespace Api\Client\Soap\Core;

class ReportInfo
{

    /**
     * @var string $fileLocation
     * @access public
     */
    public $fileLocation = null;

    /**
     * @param string $fileLocation
     * @access public
     */
    public function __construct($fileLocation)
    {
      $this->fileLocation = $fileLocation;
    }

}
