<?php

namespace Api\Client\Soap\Core;

class RepriceCriteria
{

    /**
     * @var int $clientId
     * @access public
     */
    public $clientId = null;

    /**
     * @var string $discountCode
     * @access public
     */
    public $discountCode = null;

    /**
     * @var string $source
     * @access public
     */
    public $source = null;

    /**
     * @param int $clientId
     * @param string $discountCode
     * @param string $source
     * @access public
     */
    public function __construct($clientId, $discountCode, $source)
    {
      $this->clientId = $clientId;
      $this->discountCode = $discountCode;
      $this->source = $source;
    }

}
