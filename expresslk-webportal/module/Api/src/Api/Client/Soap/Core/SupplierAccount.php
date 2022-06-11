<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class SupplierAccount extends Entity
{

    /**
     * @var int $allowedDivisions
     * @access public
     */
    public $allowedDivisions = null;

    /**
     * @var string $bank
     * @access public
     */
    public $bank = null;

    /**
     * @var string $branch
     * @access public
     */
    public $branch = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var boolean $isPrimary
     * @access public
     */
    public $isPrimary = null;

    /**
     * @var string $name
     * @access public
     */
    public $name = null;

    /**
     * @var string $number
     * @access public
     */
    public $number = null;

    /**
     * @var int $supplierId
     * @access public
     */
    public $supplierId = null;

    /**
     * @var string $swift
     * @access public
     */
    public $swift = null;

    /**
     * @var AccountType $type
     * @access public
     */
    public $type = null;

    /**
     * @param int $allowedDivisions
     * @param string $bank
     * @param string $branch
     * @param int $id
     * @param boolean $isPrimary
     * @param string $name
     * @param string $number
     * @param int $supplierId
     * @param string $swift
     * @param AccountType $type
     * @access public
     */
    public function __construct($allowedDivisions, $bank, $branch, $id, $isPrimary, $name, $number, $supplierId, $swift, $type)
    {
      $this->allowedDivisions = $allowedDivisions;
      $this->bank = $bank;
      $this->branch = $branch;
      $this->id = $id;
      $this->isPrimary = $isPrimary;
      $this->name = $name;
      $this->number = $number;
      $this->supplierId = $supplierId;
      $this->swift = $swift;
      $this->type = $type;
    }

}
