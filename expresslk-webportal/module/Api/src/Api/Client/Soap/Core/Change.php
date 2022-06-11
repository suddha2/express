<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class Change extends Entity
{

    /**
     * @var int $bookingId
     * @access public
     */
    public $bookingId = null;

    /**
     * @var int $bookingItemId
     * @access public
     */
    public $bookingItemId = null;

    /**
     * @var int $bookingItemPassengerId
     * @access public
     */
    public $bookingItemPassengerId = null;

    /**
     * @var dateTime $changeTime
     * @access public
     */
    public $changeTime = null;

    /**
     * @var string $description
     * @access public
     */
    public $description = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var ChangeType $type
     * @access public
     */
    public $type = null;

    /**
     * @var int $userId
     * @access public
     */
    public $userId = null;

    /**
     * @param int $bookingId
     * @param int $bookingItemId
     * @param int $bookingItemPassengerId
     * @param dateTime $changeTime
     * @param string $description
     * @param int $id
     * @param ChangeType $type
     * @param int $userId
     * @access public
     */
    public function __construct($bookingId, $bookingItemId, $bookingItemPassengerId, $changeTime, $description, $id, $type, $userId)
    {
      $this->bookingId = $bookingId;
      $this->bookingItemId = $bookingItemId;
      $this->bookingItemPassengerId = $bookingItemPassengerId;
      $this->changeTime = $changeTime;
      $this->description = $description;
      $this->id = $id;
      $this->type = $type;
      $this->userId = $userId;
    }

}
