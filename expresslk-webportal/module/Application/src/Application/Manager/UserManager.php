<?php


namespace Application\Manager;


use Application\Adapter\CoreAuthenticationAdapter;
use Application\InjectorBase;
use Application\Model\User;
use Application\Util\Log;
use Zend\Authentication\AuthenticationService;
use Zend\Session\Container;

class UserManager extends InjectorBase
{
    const SESSION_NAME = 'UserManagerSession';

    const KEY_USER = 'UserObject';

    private $_sessionObject;

    public function __construct($serviceManager)
    {
        parent::__construct($serviceManager);
        //initialize session
        $this->_sessionObject = new Container(self::SESSION_NAME);
    }

    /**
     * @return User
     */
    public function getCurrentUser()
    {
        /** @var \Application\Model\User $oUser */
        $oUser = null;
        /**
         * @var $userAuth AuthenticationService
         */
        $userAuth = $this->getServiceLocator()->get('AuthService');
        if($userAuth->hasIdentity()){
            $oUser = $userAuth->getIdentity();
        }else{
            //set site user if guest user
            $aSiteConfig = $this->_getConfig();
            //Check if the user exists or if the date expired
            $oUser = $this->_getUser($aSiteConfig['username']);
            if($oUser===false || $oUser->isTokenExpired()){
                //re-sign on user
                $oUser = $this->signInDefaultUser();
            }
        }

        return $oUser;
    }

    /**
     * Renew session of the default user
     */
    public function renewDefaultUser()
    {
        //call signin again to initiate re-login process
        $this->signInDefaultUser();
    }

    /**
     * @return User
     * @throws \Exception
     */
    protected function signInDefaultUser()
    {
        $config = $this->_getConfig();
        //get username/password
        $sUsername = $config['username'];
        $sPassword = $config['password'];
        //get authenticate adapter
        $oAuthAdapter = new CoreAuthenticationAdapter($this->getServiceLocator());

        //authenticate and get user result
        $oAuthAdapter->setIdentity($sUsername)
            ->setCredential($sPassword);
        $oAuthResult = $oAuthAdapter->authenticate();

        //validate authentication result
        if($oAuthResult->isValid()){
            $oUser = $oAuthResult->getIdentity();
            //save user object after clearing existing key
            $this->_saveUser($oUser);
            return $oUser;
        }else{
            //log this as a critical exception
            (new Log())->emerg('Error in Default UserSignin: Could not validate User.');
            throw new \Exception('Could not validate User.');
        }
    }

    /**
     * @return array array()
     */
    private function _getConfig()
    {
        $config = $this->getServiceLocator()->get('Config');
        //get username/password
        return $config['api']['auth'];
    }

    /**
     * @param User $oUser
     */
    private function _saveUser($oUser)
    {
        $oCache = $this->getServiceLocator()->get('DataStore');
        $oCache->setItem($this->_getUserKey($oUser->username), $oUser);
    }

    /**
     * @param $sUsername
     * @return bool|User
     */
    private function _getUser($sUsername)
    {
        $oCache = $this->getServiceLocator()->get('DataStore');
        $success = false;
        //get from cache
        $oUser = $oCache->getItem($this->_getUserKey($sUsername), $success);
        if($success){
            return $oUser;
        }
        return false;
    }

    /**
     * @param $sName
     * @return string
     */
    private function _getUserKey($sName)
    {
        return sha1(self::KEY_USER.$sName);
    }

}