<?php
/**
 * File for Acl Class
 *
 * @category  User
 * @package   User_Acl
 * @author    Udantha Pathirana <info@udantha.com>
 */

/**
 * @namespace
 */
namespace Application\Acl;

/**
 * @uses Zend\Acl\Acl
 * @uses Zend\Acl\Role\GenericRole
 * @uses Zend\Acl\Resource\GenericResource
 */
use Application\Manager\UserManager;
use Application\Model\User;
use Zend\Authentication\AuthenticationService;
use Zend\Permissions\Acl\Acl as ZendAcl,
    Zend\Permissions\Acl\Role\GenericRole as Role,
    Zend\Permissions\Acl\Resource\GenericResource as Resource,
    Zend\Debug;
use Zend\ServiceManager\ServiceManager;

/**
 * Class to handle Acl
 *
 * This class is for loading ACL defined in a config
 *
 * @category User
 * @package  User_Acl
 */
class Acl extends ZendAcl {

    //modules
    const MODULE_CONTROLLER   = 'SEARCH';
    const MODULE_ENTITY       = 'ENTITY';
    const MODULE_FRAGMENT     = 'FRAGMENT';
    const MODULE_PERMGROUP    = 'PERMGROUP';
    //roles
    const ROLE_GUEST        = 'GST';
    const ROLE_CUSTOMER     = 'CLT';
    const ROLE_AGENT        = 'AGT';
    const ROLE_TICKETBOX    = 'STF';
    const ROLE_ADMIN        = 'ADM';
    const ROLE_SUPERLINE_MANAGER        = 'SPLM';

    //entity permission codes
    const ENTITY_CAN_VIEW   = 'view';
    const ENTITY_CAN_CREATE = 'create';
    const ENTITY_CAN_EDIT   = 'edit';
    const ENTITY_CAN_DELETE = 'delete';

    //fragment permission codes
    const FRAGMENT_CAN_READ = 'read';
    const FRAGMENT_CAN_WRITE = 'write';
    const FRAGMENT_CAN_EXECUTE = 'execute';

    /**
     * Constructor
     *
     * @param array $config
     * @return void
     * @throws \Exception
     */
    public function __construct($sl)
    {
        /**
         * @var $userAuth AuthenticationService
         */
//        $userAuth = $sl->get('AuthService');
//        if(!$userAuth->hasIdentity()){
//            //create user
//            $oUser = new User();
//            //load guest role
//            /**
//             * @var $auth \Api\Manager\Authentication
//             */
//            $auth = $sl->get('Api\Manager\Authentication');
//            $guestUserGroup = array($auth->getUserGroupByCode(self::ROLE_GUEST));
//            $oUser->setUserGroups($guestUserGroup);
//        }else{
//            $oUser = $userAuth->getIdentity();
//        }

        $oUser = self::getAuthUser($sl);

        $resources = array();
        $roles = $oUser->getRole()->getRoles();
        $resources['allow'] = $oUser->getModuleResources(self::MODULE_CONTROLLER);

        $this->_addRoles($roles)
            ->_addResources($resources);
    }

    /**
     * @param ServiceManager $sm
     * @return User
     */
    public static function getAuthUser($sm)
    {
        /** @var UserManager $oUserManager */
        $oUserManager = $sm->get('Manager\UserManager');
        return $oUserManager->getCurrentUser();
    }

    /**
     * Adds Roles to ACL
     *
     * @param array $roles
     * @return $this
     */
    protected function _addRoles($roles)
    {
        foreach ($roles as $name) {
            if (!$this->hasRole($name)) {
                $parent = null;
                if (empty($parent)) {
                    $parent = array();
                } else {
                    $parent = explode(',', $parent);
                }

                $this->addRole(new Role($name), $parent);
            }
        }

        return $this;
    }

    /**
     * Adds Resources to ACL
     *
     * @param $resources
     * @return $this
     * @throws \Exception
     */
    protected function _addResources($resources)
    {
        foreach ($resources as $permission => $controllers) {
            foreach ($controllers as $controller => $actions) {
                if ($controller == 'all') {
                    $controller = null;
                } else {
                    if (!$this->hasResource($controller)) {
                        $this->addResource(new Resource($controller));
                    }
                }

                foreach ($actions as $action => $sRole) {
                    if ($action == 'all') {
                        $action = null;
                    }
                    //accomodate multiple roles per resource
                    $roleArray = is_array($sRole) ? $sRole : array($sRole);
                    foreach($roleArray as $role){
                        if ($permission == 'allow') {
                            $this->allow($role, $controller, $action);
                        } elseif ($permission == 'deny') {
                            $this->deny($role, $controller, $action);
                        } else {
                            throw new \Exception('No valid permission defined: ' . $permission);
                        }
                    }
                }
            }
        }

        return $this;
    }

    /**
     * overwrites is allowed function to custom check role
     * @param \Application\Acl\Role $oRoles
     * @param null $resource
     * @param null $privilege
     * @return bool
     */
    public function isAllowed($oRoles = null, $resource = null, $privilege = null)
    {
        $isAllowed = false;

        //get roles from role object
        $roles = $oRoles->getRoles();
        // Loop through them one by one and check if they're allowed
        foreach ($roles as $role) {
            // Using the actual ACL isAllowed method here
            if (parent::isAllowed($role, $resource, $privilege)===true){
                $isAllowed = true;
            }
        }

        return $isAllowed;
    }
}
