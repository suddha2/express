<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class Company extends Entity
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
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var string $logoFile
     * @access public
     */
    public $logoFile = null;

    /**
     * @var string $name
     * @access public
     */
    public $name = null;

    /**
     * @param int $bitmask
     * @param string $code
     * @param int $id
     * @param string $logoFile
     * @param string $name
     * @access public
     */
    public function __construct($bitmask, $code, $id, $logoFile, $name)
    {
      $this->bitmask = $bitmask;
      $this->code = $code;
      $this->id = $id;
      $this->logoFile = $logoFile;
      $this->name = $name;
    }

}
