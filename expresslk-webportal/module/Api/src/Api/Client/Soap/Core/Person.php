<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class Person extends Entity
{

    /**
     * @var int $allowedDivisions
     * @access public
     */
    public $allowedDivisions = null;

    /**
     * @var string $city
     * @access public
     */
    public $city = null;

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
     * @var string $fullName
     * @access public
     */
    public $fullName = null;

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
     * @var string $street
     * @access public
     */
    public $street = null;

    /**
     * @var string $workTelephone
     * @access public
     */
    public $workTelephone = null;

    /**
     * @param int $allowedDivisions
     * @param string $fullName
     * @param int $id
     * @access public
     */
    public function __construct($allowedDivisions, $fullName, $id)
    {
      $this->allowedDivisions = $allowedDivisions;
      $this->fullName = $fullName;
      $this->id = $id;
    }

}
