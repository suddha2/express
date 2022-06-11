<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class Bus extends Entity
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
     * @var Amenity[] $amenities
     * @access public
     */
    public $amenities = null;

    /**
     * @var BusBusRoute[] $busRoutes
     * @access public
     */
    public $busRoutes = null;

    /**
     * @var BusType $busType
     * @access public
     */
    public $busType = null;

    /**
     * @var boolean $cashOnDepartureAllowed
     * @access public
     */
    public $cashOnDepartureAllowed = null;

    /**
     * @var Conductor $conductor
     * @access public
     */
    public $conductor = null;

    /**
     * @var string $contact
     * @access public
     */
    public $contact = null;

    /**
     * @var Division $division
     * @access public
     */
    public $division = null;

    /**
     * @var Driver $driver
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
     * @var NotificationMethod $notificationMethod
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
     * @var SeatingProfile $seatingProfile
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
     * @var Supplier $supplier
     * @access public
     */
    public $supplier = null;

    /**
     * @var TravelClass $travelClass
     * @access public
     */
    public $travelClass = null;

    /**
     * @param string $adminContact
     * @param int $allowedDivisions
     * @param BusType $busType
     * @param boolean $cashOnDepartureAllowed
     * @param Conductor $conductor
     * @param string $contact
     * @param Division $division
     * @param Driver $driver
     * @param int $id
     * @param boolean $markupOnly
     * @param string $name
     * @param NotificationMethod $notificationMethod
     * @param dateTime $permitExpiryDate
     * @param dateTime $permitIssueDate
     * @param string $permitNumber
     * @param string $plateNumber
     * @param float $rating
     * @param SeatingProfile $seatingProfile
     * @param int $secondsBeforeTBEnd
     * @param int $secondsBeforeTicketingActive
     * @param int $secondsBeforeWebEnd
     * @param Supplier $supplier
     * @param TravelClass $travelClass
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
