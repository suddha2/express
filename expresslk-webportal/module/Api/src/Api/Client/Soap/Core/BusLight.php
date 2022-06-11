<?php

namespace Api\Client\Soap\Core;

include_once('LightEntity.php');

class BusLight extends LightEntity
{

    /**
     * @var string $adminContact
     * @access public
     */
    public $adminContact = null;

    /**
     * @var int $allowedDivisions
     * @access public
     */
    public $allowedDivisions = null;

    /**
     * @var int $busType
     * @access public
     */
    public $busType = null;

    /**
     * @var boolean $cashOnDepartureAllowed
     * @access public
     */
    public $cashOnDepartureAllowed = null;

    /**
     * @var int $conductor
     * @access public
     */
    public $conductor = null;

    /**
     * @var string $contact
     * @access public
     */
    public $contact = null;

    /**
     * @var int $division
     * @access public
     */
    public $division = null;

    /**
     * @var int $driver
     * @access public
     */
    public $driver = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var boolean $markupOnly
     * @access public
     */
    public $markupOnly = null;

    /**
     * @var string $name
     * @access public
     */
    public $name = null;

    /**
     * @var string $notificationMethod
     * @access public
     */
    public $notificationMethod = null;

    /**
     * @var dateTime $permitExpiryDate
     * @access public
     */
    public $permitExpiryDate = null;

    /**
     * @var dateTime $permitIssueDate
     * @access public
     */
    public $permitIssueDate = null;

    /**
     * @var string $permitNumber
     * @access public
     */
    public $permitNumber = null;

    /**
     * @var string $plateNumber
     * @access public
     */
    public $plateNumber = null;

    /**
     * @var float $rating
     * @access public
     */
    public $rating = null;

    /**
     * @var int $seatingProfile
     * @access public
     */
    public $seatingProfile = null;

    /**
     * @var int $secondsBeforeTBEnd
     * @access public
     */
    public $secondsBeforeTBEnd = null;

    /**
     * @var int $secondsBeforeTicketingActive
     * @access public
     */
    public $secondsBeforeTicketingActive = null;

    /**
     * @var int $secondsBeforeWebEnd
     * @access public
     */
    public $secondsBeforeWebEnd = null;

    /**
     * @var int $supplier
     * @access public
     */
    public $supplier = null;

    /**
     * @var int $travelClass
     * @access public
     */
    public $travelClass = null;

    /**
     * @param string $adminContact
     * @param int $allowedDivisions
     * @param int $busType
     * @param boolean $cashOnDepartureAllowed
     * @param int $conductor
     * @param string $contact
     * @param int $division
     * @param int $driver
     * @param int $id
     * @param boolean $markupOnly
     * @param string $name
     * @param string $notificationMethod
     * @param dateTime $permitExpiryDate
     * @param dateTime $permitIssueDate
     * @param string $permitNumber
     * @param string $plateNumber
     * @param float $rating
     * @param int $seatingProfile
     * @param int $secondsBeforeTBEnd
     * @param int $secondsBeforeTicketingActive
     * @param int $secondsBeforeWebEnd
     * @param int $supplier
     * @param int $travelClass
     * @access public
     */
    public function __construct($adminContact, $allowedDivisions, $busType, $cashOnDepartureAllowed, $conductor, $contact, $division, $driver, $id, $markupOnly, $name, $notificationMethod, $permitExpiryDate, $permitIssueDate, $permitNumber, $plateNumber, $rating, $seatingProfile, $secondsBeforeTBEnd, $secondsBeforeTicketingActive, $secondsBeforeWebEnd, $supplier, $travelClass)
    {
      $this->adminContact = $adminContact;
      $this->allowedDivisions = $allowedDivisions;
      $this->busType = $busType;
      $this->cashOnDepartureAllowed = $cashOnDepartureAllowed;
      $this->conductor = $conductor;
      $this->contact = $contact;
      $this->division = $division;
      $this->driver = $driver;
      $this->id = $id;
      $this->markupOnly = $markupOnly;
      $this->name = $name;
      $this->notificationMethod = $notificationMethod;
      $this->permitExpiryDate = $permitExpiryDate;
      $this->permitIssueDate = $permitIssueDate;
      $this->permitNumber = $permitNumber;
      $this->plateNumber = $plateNumber;
      $this->rating = $rating;
      $this->seatingProfile = $seatingProfile;
      $this->secondsBeforeTBEnd = $secondsBeforeTBEnd;
      $this->secondsBeforeTicketingActive = $secondsBeforeTicketingActive;
      $this->secondsBeforeWebEnd = $secondsBeforeWebEnd;
      $this->supplier = $supplier;
      $this->travelClass = $travelClass;
    }

}
