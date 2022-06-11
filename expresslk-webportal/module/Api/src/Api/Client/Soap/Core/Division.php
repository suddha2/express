<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class Division extends Entity
{

    /**
     * @var int $bitmask
     * @access public
     */
    public $bitmask = null;

    /**
     * @var string $code
     * @access public
     */
    public $code = null;

    /**
     * @var Company $company
     * @access public
     */
    public $company = null;

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
     * @param int $bitmask
     * @param string $code
     * @param Company $company
     * @param int $id
     * @param string $name
     * @access public
     */
    public function __construct($bitmask, $code, $company, $id, $name)
    {
      $this->bitmask = $bitmask;
      $this->code = $code;
      $this->company = $company;
      $this->id = $id;
      $this->name = $name;
    }

}
