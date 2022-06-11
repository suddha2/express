<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 11/23/14
 * Time: 5:07 PM
 */

namespace Application\Acl;


use Zend\Permissions\Acl\Role\RoleInterface;

class Role implements RoleInterface{

    private $_roles = array();
    private $_roleId = '';

    public function getRoleId()
    {
        return $this->_roleId;
    }

    /**
     * @param array $roles
     */
    public function setRoles(array $roles)
    {
        $this->_roles = $roles;
        //set role id
        $this->_roleId = implode(',', $roles);
    }

    /**
     * Get saved roles
     * @return array
     */
    public function getRoles()
    {
        return $this->_roles;
    }
} 