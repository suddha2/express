<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class SupplierContactPerson extends Entity
{

    /**
     * @var int $allowedDivisions
     * @access public
     */
    public $allowedDivisions = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var boolean $owner
     * @access public
     */
    public $owner = null;

    /**
     * @var Person $person
     * @access public
     */
    public $person = null;

    /**
     * @var boolean $primary
     * @access public
     */
    public $primary = null;

    /**
     * @var int $supplierId
     * @access public
     */
    public $supplierId = null;

    /**
     * @param int $allowedDivisions
     * @param int $id
     * @param boolean $owner
     * @param Person $person
     * @param boolean $primary
     * @param int $supplierId
     * @access public
     */
    public function __construct($allowedDivisions, $id, $owner, $person, $primary, $supplierId)
    {
      $this->allowedDivisions = $allowedDivisions;
      $this->id = $id;
      $this->owner = $owner;
      $this->person = $person;
      $this->primary = $primary;
      $this->supplierId = $supplierId;
    }

}
