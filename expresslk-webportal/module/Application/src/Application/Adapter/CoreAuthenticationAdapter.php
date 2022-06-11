<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 10/1/14
 * Time: 7:41 PM
 */

namespace Application\Adapter;


use Application\Acl\Acl;
use Application\Model\User;
use Zend\Authentication\Adapter\AdapterInterface;
use Zend\Authentication\Result as AuthResult;
use Zend\ServiceManager\ServiceManager;

class CoreAuthenticationAdapter implements AdapterInterface
{
    protected $username;

    protected $password;

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
                /**
                 * @var $auth \Api\Manager\Authentication
                 */
                $auth = $this->_sm->get('Api\Manager\Authentication');
                $oTokenResponse = $auth->getToken($this->username, $this->password);
                //save token
                $permissions->token = $oTokenResponse->token;
                $permissions->setTokenExpiry($oTokenResponse->expiry);

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
     * Set password
     * @param $password
     * @return $this
     */
    public function setCredential($password)
    {
        //set password with credential treatment
        $this->password = $password;
        return $this;
    }

    /**
     * Get validated users data
     * @return User
     */
    public function getUserData()
    {
        /**
         * @var $auth \Api\Manager\Authentication
         */
        $auth = $this->_sm->get('Api\Manager\Authentication');
        $authResponse = $auth->login($this->username, $this->password);

        //populate user data
        $oUser = new User();
        $oUser->id = $authResponse->id;
        $oUser->username = $authResponse->username;
        $oUser->firstName = $authResponse->firstName;
        $oUser->middleName = $authResponse->middleName;
        $oUser->lastName = $authResponse->lastName;
        $oUser->dob = $authResponse->dob;
        $oUser->nic = $authResponse->nic;
        $oUser->gender = $authResponse->gender;
        $oUser->email = $authResponse->email;
        $oUser->divisionId = $authResponse->division->id;
        $oUser->setRole($authResponse->roles);
        $oUser->setDivisions($authResponse->divisions);
        $oUser->setUserGroups($authResponse->userGroups);
		$oUser->visibleDivisionsBitmask=$authResponse->visibleDivisionsBitmask;
        return $oUser;
    }
}