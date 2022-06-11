<?php

namespace Api\Client\Soap\Core;

class LegCriteria
{

    /**
     * @var int $departureTimestamp
     * @access public
     */
    public $departureTimestamp = null;

    /**
     * @var int $departureTimestampEnd
     * @access public
     */
    public $departureTimestampEnd = null;

    /**
     * @var Filter[] $filters
     * @access public
     */
    public $filters = null;

    /**
     * @var int $resultsCount
     * @access public
     */
    public $resultsCount = null;

    /**
     * @var string[] $type
     * @access public
     */
    public $type = null;

    /**
     * @var int[] $viaPoints
     * @access public
     */
    public $viaPoints = null;

    /**
     * @param int $departureTimestamp
     * @param int $departureTimestampEnd
     * @param int $resultsCount
     * @access public
     */
    public function __construct($departureTimestamp, $departureTimestampEnd, $resultsCount)
    {
      $this->departureTimestamp = $departureTimestamp;
      $this->departureTimestampEnd = $departureTimestampEnd;
      $this->resultsCount = $resultsCount;
    }

}
