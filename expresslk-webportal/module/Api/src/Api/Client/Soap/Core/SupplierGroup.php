<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class SupplierGroup extends Entity
{

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
     * @param int $id
     * @param string $name
     * @access public
     */
    public function __construct($id, $name)
    {
      $this->id = $id;
      $this->name = $name;
    }

}
