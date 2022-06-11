<?php


namespace Application\Adapter;

use Application\Acl\Acl;
use Application\Model\User;
use Zend\Authentication\Adapter\AdapterInterface;
use Zend\Authentication\Result as AuthResult;
use Zend\ServiceManager\ServiceManager;

class AppAuthenticateAdapter implements AdapterInterface{

    private $username, $token;
    private $_sm;

    function __construct(ServiceManager $serviceManager)
    {
        $this->_sm = $serviceManager;
    }

    /**
     * @return \Zend\Authentication\Result
     */
    public function authenticate()
    {
        try {
            //create authentication logic by connecting to core and validating username/password
            $permissions = $this->getUserData();

            if ($permissions !== false) {
                //Login success. request to get a token

                /* Return if success, second parameter is the identity, e.g user. */
                return new AuthResult(AuthResult::SUCCESS, $permissions);
            } else {
                /* Return if can't find user */
                return new AuthResult(AuthResult::FAILURE_IDENTITY_NOT_FOUND, null);
            }
        } catch (\Exception $e) {
            /* Return if user found, but credentials were invalid */
            return new AuthResult(AuthResult::FAILURE_CREDENTIAL_INVALID, null);
        }
    }

    /**
     * Set username
     * @param $username
     * @return $this
     */
    public function setIdentity($username)
    {
        $this->username = $username;
        return $this;
    }

    /**
     * Set token
     * @param $token
     * @return $this
     */
    public function setToken($token)
    {
        //set password with credential treatment
        $this->token = $token;
        return $this;
    }

    /**
     * Get validated users data
     * @return User
     */
    public function getUserData()
    {
        //validate username/token
        if(empty($this->username) || empty($this->token)){
            return false;
        }
        //populate user data
        $oUser = new User();
        $oUser->username = $this->username;
        $oUser->token = $this->token;
        /**
         * Remove this later and get roles from API for this user
         */
        //load guest role
        /**
         * @var $auth \Api\Manager\Authentication
         */
        $auth = $this->_sm->get('Api\Manager\Authentication');
        $guestUserGroup = array($auth->getUserGroupByCode(Acl::ROLE_GUEST));
        $oUser->setUserGroups($guestUserGroup);
        return $oUser;
    }
}