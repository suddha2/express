<?php

namespace Api\Client\Soap\Core;

include_once('Permission.php');

class PermissionGroup extends Permission
{

    /**
     * @var PermissionSingle[] $permissions
     * @access public
     */
    public $permissions = null;

    /**
     * @param string $code
     * @param string $description
     * @param int $id
     * @param Module $module
     * @param string $name
     * @access public
     */
    public function __construct($code, $description, $id, $module, $name)
    {
      parent::__construct($code, $description, $id, $module, $name);
    }

}
