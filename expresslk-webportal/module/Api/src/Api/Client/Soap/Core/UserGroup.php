<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class UserGroup extends Entity
{

    /**
     * @var string $code
     * @access public
     */
    public $code = null;

    /**
     * @var string $description
     * @access public
     */
    public $description = null;

    /**
     * @var Division $division
     * @access public
     */
    public $division = null;

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
     * @var Permission[] $permission
     * @access public
     */
    public $permission = null;

    /**
     * @var Division[] $visibleDivisions
     * @access public
     */
    public $visibleDivisions = null;

    /**
     * @param string $code
     * @param string $description
     * @param Division $division
     * @param int $id
     * @param string $name
     * @access public
     */
    public function __construct($code, $description, $division, $id, $name)
    {
      $this->code = $code;
      $this->description = $description;
      $this->division = $division;
      $this->id = $id;
      $this->name = $name;
    }

}
