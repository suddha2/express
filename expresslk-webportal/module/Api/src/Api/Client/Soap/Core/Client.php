<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class Client extends Entity
{

    /**
     * @var string $email
     * @access public
     */
    public $email = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var string $mobileTelephone
     * @access public
     */
    public $mobileTelephone = null;

    /**
     * @var string $name
     * @access public
     */
    public $name = null;

    /**
     * @var string $nic
     * @access public
     */
    public $nic = null;

    /**
     * @var User $user
     * @access public
     */
    public $user = null;

    /**
     * @param string $email
     * @param int $id
     * @param string $mobileTelephone
     * @param string $name
     * @param string $nic
     * @param User $user
     * @access public
     */
    public function __construct($email, $id, $mobileTelephone, $name, $nic, $user)
    {
      $this->email = $email;
      $this->id = $id;
      $this->mobileTelephone = $mobileTelephone;
      $this->name = $name;
      $this->nic = $nic;
      $this->user = $user;
    }

}
