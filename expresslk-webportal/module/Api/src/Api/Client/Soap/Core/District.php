<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class District extends Entity
{

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
     * @var string $name
     * @access public
     */
    public $name = null;

    /**
     * @param string $code
     * @param int $id
     * @param string $name
     * @access public
     */
    public function __construct($code, $id, $name)
    {
      $this->code = $code;
      $this->id = $id;
      $this->name = $name;
    }

}
