<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class Driver extends Entity
{

    /**
     * @var int $allowedDivisions
     * @access public
     */
    public $allowedDivisions = null;

    /**
     * @var string $drivingLicence
     * @access public
     */
    public $drivingLicence = null;

    /**
     * @var dateTime $drivingLicenceExpiryDate
     * @access public
     */
    public $drivingLicenceExpiryDate = null;

    /**
     * @var dateTime $drivingLicenceIssueDate
     * @access public
     */
    public $drivingLicenceIssueDate = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var string $nickName
     * @access public
     */
    public $nickName = null;

    /**
     * @var dateTime $ntcRegistrationExpiryDate
     * @access public
     */
    public $ntcRegistrationExpiryDate = null;

    /**
     * @var string $ntcRegistrationNumber
     * @access public
     */
    public $ntcRegistrationNumber = null;

    /**
     * @var Person $person
     * @access public
     */
    public $person = null;

    /**
     * @param int $allowedDivisions
     * @param string $drivingLicence
     * @param dateTime $drivingLicenceExpiryDate
     * @param dateTime $drivingLicenceIssueDate
     * @param int $id
     * @param string $nickName
     * @param dateTime $ntcRegistrationExpiryDate
     * @param string $ntcRegistrationNumber
     * @param Person $person
     * @access public
     */
    public function __construct($allowedDivisions, $drivingLicence, $drivingLicenceExpiryDate, $drivingLicenceIssueDate, $id, $nickName, $ntcRegistrationExpiryDate, $ntcRegistrationNumber, $person)
    {
      $this->allowedDivisions = $allowedDivisions;
      $this->drivingLicence = $drivingLicence;
      $this->drivingLicenceExpiryDate = $drivingLicenceExpiryDate;
      $this->drivingLicenceIssueDate = $drivingLicenceIssueDate;
      $this->id = $id;
      $this->nickName = $nickName;
      $this->ntcRegistrationExpiryDate = $ntcRegistrationExpiryDate;
      $this->ntcRegistrationNumber = $ntcRegistrationNumber;
      $this->person = $person;
    }

}
