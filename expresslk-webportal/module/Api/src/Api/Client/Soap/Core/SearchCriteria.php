<?php

namespace Api\Client\Soap\Core;

include_once('BaseCriteria.php');

class SearchCriteria extends BaseCriteria
{

    /**
     * @var string $fromCity
     * @access public
     */
    public $fromCity = null;

    /**
     * @var int $fromCityId
     * @access public
     */
    public $fromCityId = null;

    /**
     * @var LegCriteria[] $legCriterion
     * @access public
     */
    public $legCriterion = null;

    /**
     * @var boolean $roundTrip
     * @access public
     */
    public $roundTrip = null;

    /**
     * @var string $toCity
     * @access public
     */
    public $toCity = null;

    /**
     * @var int $toCityId
     * @access public
     */
    public $toCityId = null;

    /**
     * @param int $clientId
     * @param string $discountCode
     * @param string $ip
     * @param string $source
     * @param string $fromCity
     * @param int $fromCityId
     * @param boolean $roundTrip
     * @param string $toCity
     * @param int $toCityId
     * @access public
     */
    public function __construct($clientId, $discountCode, $ip, $source, $fromCity, $fromCityId, $roundTrip, $toCity, $toCityId)
    {
      parent::__construct($clientId, $discountCode, $ip, $source);
      $this->fromCity = $fromCity;
      $this->fromCityId = $fromCityId;
      $this->roundTrip = $roundTrip;
      $this->toCity = $toCity;
      $this->toCityId = $toCityId;
    }

}
