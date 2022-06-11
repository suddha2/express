<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class Supplier extends Entity
{

    /**
     * @var SupplierAccount[] $accounts
     * @access public
     */
    public $accounts = null;

    /**
     * @var int $allowedDivisions
     * @access public
     */
    public $allowedDivisions = null;

    /**
     * @var SupplierContactPerson[] $contactPersonnel
     * @access public
     */
    public $contactPersonnel = null;

    /**
     * @var SupplierGroup $group
     * @access public
     */
    public $group = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var string $name
     * @access public
     */
    public $name = null;

    /**
     * @var PaymentRefundMode $preferredPaymentMode
     * @access public
     */
    public $preferredPaymentMode = null;

    /**
     * @param int $allowedDivisions
     * @param SupplierGroup $group
     * @param int $id
     * @param string $name
     * @param PaymentRefundMode $preferredPaymentMode
     * @access public
     */
    public function __construct($allowedDivisions, $group, $id, $name, $preferredPaymentMode)
    {
      $this->allowedDivisions = $allowedDivisions;
      $this->group = $group;
      $this->id = $id;
      $this->name = $name;
      $this->preferredPaymentMode = $preferredPaymentMode;
    }

}
