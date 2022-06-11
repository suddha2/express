<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class Rule extends Entity
{

    /**
     * @var float $amount
     * @access public
     */
    public $amount = null;

    /**
     * @var ApplicationType $applicationType
     * @access public
     */
    public $applicationType = null;

    /**
     * @var RuleCondition $condition
     * @access public
     */
    public $condition = null;

    /**
     * @var string $description
     * @access public
     */
    public $description = null;

    /**
     * @var dateTime $endTime
     * @access public
     */
    public $endTime = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var string $name
     * @access public
     */
    public $name = null;

    /**
     * @var float $salience
     * @access public
     */
    public $salience = null;

    /**
     * @var string $scheme
     * @access public
     */
    public $scheme = null;

    /**
     * @var dateTime $startTime
     * @access public
     */
    public $startTime = null;

    /**
     * @param float $amount
     * @param ApplicationType $applicationType
     * @param RuleCondition $condition
     * @param string $description
     * @param dateTime $endTime
     * @param int $id
     * @param string $name
     * @param float $salience
     * @param string $scheme
     * @param dateTime $startTime
     * @access public
     */
    public function __construct($amount, $applicationType, $condition, $description, $endTime, $id, $name, $salience, $scheme, $startTime)
    {
      $this->amount = $amount;
      $this->applicationType = $applicationType;
      $this->condition = $condition;
      $this->description = $description;
      $this->endTime = $endTime;
      $this->id = $id;
      $this->name = $name;
      $this->salience = $salience;
      $this->scheme = $scheme;
      $this->startTime = $startTime;
    }

}
