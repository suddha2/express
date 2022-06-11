<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class User extends Entity
{

    /**
     * @var boolean $accountable
     * @access public
     */
    public $accountable = null;

    /**
     * @var Agent $agent
     * @access public
     */
    public $agent = null;

    /**
     * @var string $city
     * @access public
     */
    public $city = null;

    /**
     * @var Division $division
     * @access public
     */
    public $division = null;

    /**
     * @var dateTime $dob
     * @access public
     */
    public $dob = null;

    /**
     * @var string $email
     * @access public
     */
    public $email = null;

    /**
     * @var string $firstName
     * @access public
     */
    public $firstName = null;

    /**
     * @var Gender $gender
     * @access public
     */
    public $gender = null;

    /**
     * @var string $homeTelephone
     * @access public
     */
    public $homeTelephone = null;

    /**
     * @var string $houseNumber
     * @access public
     */
    public $houseNumber = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var string $lastName
     * @access public
     */
    public $lastName = null;

    /**
     * @var string $middleName
     * @access public
     */
    public $middleName = null;

    /**
     * @var string $mobileTelephone
     * @access public
     */
    public $mobileTelephone = null;

    /**
     * @var string $nic
     * @access public
     */
    public $nic = null;

    /**
     * @var int $postalCode
     * @access public
     */
    public $postalCode = null;

    /**
     * @var AccountStatus $status
     * @access public
     */
    public $status = null;

    /**
     * @var string $street
     * @access public
     */
    public $street = null;

    /**
     * @var UserGroup[] $userGroups
     * @access public
     */
    public $userGroups = null;

    /**
     * @var string $username
     * @access public
     */
    public $username = null;

    /**
     * @var string $workTelephone
     * @access public
     */
    public $workTelephone = null;
	
	public $visibleDivisionsBitmask = null;
	
    /**
     * @param boolean $accountable
     * @param Agent $agent
     * @param string $city
     * @param Division $division
     * @param dateTime $dob
     * @param string $email
     * @param string $firstName
     * @param Gender $gender
     * @param string $homeTelephone
     * @param string $houseNumber
     * @param int $id
     * @param string $lastName
     * @param string $middleName
     * @param string $mobileTelephone
     * @param string $nic
     * @param int $postalCode
     * @param AccountStatus $status
     * @param string $street
     * @param string $username
     * @param string $workTelephone
     * @access public
     */
    public function __construct($accountable, $agent, $city, $division, $dob, $email, $firstName, $gender, $homeTelephone, $houseNumber, $id, $lastName, $middleName, $mobileTelephone, $nic, $postalCode, $status, $street, $username, $workTelephone,$visibleDivisionsBitmask)
    {
      $this->accountable = $accountable;
      $this->agent = $agent;
      $this->city = $city;
      $this->division = $division;
      $this->dob = $dob;
      $this->email = $email;
      $this->firstName = $firstName;
      $this->gender = $gender;
      $this->homeTelephone = $homeTelephone;
      $this->houseNumber = $houseNumber;
      $this->id = $id;
      $this->lastName = $lastName;
      $this->middleName = $middleName;
      $this->mobileTelephone = $mobileTelephone;
      $this->nic = $nic;
      $this->postalCode = $postalCode;
      $this->status = $status;
      $this->street = $street;
      $this->username = $username;
      $this->workTelephone = $workTelephone;
	  $this->visibleDivisionsBitmask=$visibleDivisionsBitmask;
    }

}
