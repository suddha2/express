<?php

namespace Api\Client\Soap\Core;

include_once('LightEntity.php');

class UserLight extends LightEntity
{

    /**
     * @var string $city
     * @access public
     */
    public $city = null;

    /**
     * @var int $division
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
     * @var string $password
     * @access public
     */
    public $password = null;

    /**
     * @var int $postalCode
     * @access public
     */
    public $postalCode = null;

    /**
     * @var string $status
     * @access public
     */
    public $status = null;

    /**
     * @var string $street
     * @access public
     */
    public $street = null;

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

    /**
     * @param string $city
     * @param int $division
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
     * @param string $password
     * @param int $postalCode
     * @param string $status
     * @param string $street
     * @param string $username
     * @param string $workTelephone
     * @access public
     */
    public function __construct($city, $division, $dob, $email, $firstName, $gender, $homeTelephone, $houseNumber, $id, $lastName, $middleName, $mobileTelephone, $nic, $password, $postalCode, $status, $street, $username, $workTelephone)
    {
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
      $this->password = $password;
      $this->postalCode = $postalCode;
      $this->status = $status;
      $this->street = $street;
      $this->username = $username;
      $this->workTelephone = $workTelephone;
    }

}
