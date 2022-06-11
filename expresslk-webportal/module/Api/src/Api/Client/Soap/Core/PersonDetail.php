<?php

namespace Api\Client\Soap\Core;

class PersonDetail
{

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
     * @var string $mobile
     * @access public
     */
    public $mobile = null;

    /**
     * @var string $nic
     * @access public
     */
    public $nic = null;

    /**
     * @access public
     */
    public function __construct()
    {
    
    }

}
