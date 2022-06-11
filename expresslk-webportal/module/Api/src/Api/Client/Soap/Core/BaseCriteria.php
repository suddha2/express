<?php

namespace Api\Client\Soap\Core;

class BaseCriteria
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
     * @var string $ip
     * @access public
     */
    public $ip = null;

    /**
     * @var string $source
     * @access public
     */
    public $source = null;

    /**
     * @param int $clientId
     * @param string $discountCode
     * @param string $ip
     * @param string $source
     * @access public
     */
    public function __construct($clientId, $discountCode, $ip, $source)
    {
      $this->clientId = $clientId;
      $this->discountCode = $discountCode;
      $this->ip = $ip;
      $this->source = $source;
    }

}
