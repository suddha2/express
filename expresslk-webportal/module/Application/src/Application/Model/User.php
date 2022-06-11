<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 11/22/14
 * Time: 8:08 PM
 */

namespace Application\Model;


use Api\Client\Soap\Core\Division;
use Application\Acl\Acl;
use Application\Acl\Role;
use Application\Helper\ExprDateTime;

class User {

    const DEFAULT_USER_ID = 0;

    public $id = null;
    public $firstName = '';
    public $middleName = '';
    public $lastName = '';
    public $dob = '';
    public $nic = '';
    public $gender = '';
    public $email = '';
    public $mobile = '';
    public $username = '';
    public $token = '';
    /** @var ExprDateTime  */
    private $tokenExpiry = null;
    public $status = '';
    public $divisionId = '';
    private $allowedResources = array();
    /** @var \Api\Client\Soap\Core\UserGroup[]  */
    public $userGroups = array();
	public $visibleDivisionsBitmask = null;
    /** @var Division[]  */
    private $divisions = array();
    /**
     * @var Role
     */
    private $roles = null;

    public function __construct()
    {
        //set defailt role
        $this->setRole(array(Acl::ROLE_GUEST));
    }

    /**
     * @param array $roles
     */
    public function setRole(array $roles)
    {
        //create role object
        $oRole = new Role();
        $oRole->setRoles($roles);
        $this->roles = $oRole;
    }

    /**
     * @return Role
     */
    public function getRole()
    {
        return $this->roles;
    }

    /**
     * Check if the user has specified role
     * @param $role
     * @return bool
     */
    public function hasRole($role)
    {
        $rolsArray = $this->roles->getRoles();
        return in_array($role, $rolsArray);
    }

    /**
     * @param array $divisions
     */
    public function setDivisions(array $divisions)
    {
        $this->divisions = $divisions;
    }

    /**
     * @return \Api\Client\Soap\Core\Division[]
     */
    public function getDivisions()
    {
        return $this->divisions;
    }

    /**
     * @return \Api\Client\Soap\Core\UserGroup[]
     */
    public function getUserGroups()
    {
        return $this->userGroups;
    }

    /**
     * @param \Api\Client\Soap\Core\UserGroup[] $userGroups
     * @return User
     */
    public function setUserGroups($userGroups)
    {
        $this->userGroups = $userGroups;
        //set up allowed resources
        $this->setUpAllowedResources();

        return $this;
    }

    /**
     * @param integer $tokenExpiry
     * @return User
     */
    public function setTokenExpiry($tokenExpiry)
    {
        //convert to date time object
        $this->tokenExpiry = ExprDateTime::getDateFromServices($tokenExpiry);
        return $this;
    }

    /**
     * Check if token is expired
     * @return bool
     */
    public function isTokenExpired()
    {
        //check if token is expired before 1 minute
        return ($this->tokenExpiry->sub(new \DateInterval('PT1M')) < new \DateTime());
    }

    /**
     * @return array
     */
    public function getModuleResources($moduleName)
    {
        if(!isset($this->allowedResources[$moduleName])){
            //throw new \Exception('Module doesnt exists in allowed resources list');
            return array();
        }

        return $this->allowedResources[$moduleName];
    }

    private function setUpAllowedResources()
    {
        $this->allowedResources = array();

        if($this->userGroups && is_array($this->userGroups)){
            foreach($this->userGroups as $group){
                $role = $group->code;
                //set user permissions for resources
                if(isset($group->permission)){
                    foreach ($group->permission as $permission) {
                        $moduleCode = $permission->module->code;
                        $this->addAllowedResource($moduleCode, $permission->code, $role);
                        if (isset($permission->permissions) && is_array($permission->permissions)) {
                            foreach ($permission->permissions as $childPermission) {
                                $moduleCode = $childPermission->module->code;
                                $this->addAllowedResource($moduleCode, $childPermission->code, $role);
                            }
                        }
                    }
                }
            }
        }
    }

    private function addAllowedResource($moduleCode, $permissionCode, $role)
    {
        //organize by permission module
        //set module code if not exists
        if (! isset($this->allowedResources[$moduleCode])) {
            $this->allowedResources[$moduleCode] = array();
        }
        //set allowed resource if not exists
        if (! isset($allowedResources[$moduleCode][$permissionCode])) {
            $this->allowedResources[$moduleCode][$permissionCode] = array(
                // 'all' means allow all within the resource
                'all' => array($role)
            );
        } else {
            //'all' key already exists for the resource, add role
            $this->allowedResources[$moduleCode][$permissionCode]['all'][] = $role;
        }
    }
}