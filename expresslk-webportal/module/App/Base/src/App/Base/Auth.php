<?php


namespace App\Base;


use Application\Adapter\AppAuthenticateAdapter;

class Auth extends Injector{

    /**
     * @param $username
     * @param $password
     * @return User
     * @throws AppException
     */
    public function login($username, $password)
    {
        /**
         * @var $authService \Zend\Authentication\AuthenticationService
         */
        $authService = $this->getServiceManager()->get('AuthService');
        /**
         * @var $adapter \Application\Adapter\CoreAuthenticationAdapter
         */
        $adapter = $authService->getAdapter();

        $adapter->setIdentity($username)
            ->setCredential($password);

        //authenticate
        $result = $authService->authenticate();
        //check for result validation
        if ($result->isValid()) {
            return $result->getIdentity();
        }else{
            return false;
        }
    }

    /**
     * Authorize based on username/token
     * @param $username
     * @param $token
     * @return bool
     */
    public function isAuthorised($username, $token)
    {
        /**
         * @var $authService \Zend\Authentication\AuthenticationService
         */
        $authService = $this->getServiceManager()->get('AuthService');
        //check if user is not already logged in
        if(!$authService->hasIdentity()){
            /**
             * Temporary
             */
            if($this->login($username, $token)===false){
                return false;
            }else{
                return true;
            }

//            //set app authenticate adapter
//            $adapter = new AppAuthenticateAdapter($this->getServiceManager());
//            //set username and token
//            $adapter->setIdentity($username)
//                ->setToken($token);
//
//            $authService->setAdapter($adapter);
//
//            //authenticate
//            $result = $authService->authenticate();
//            //check for result validation
//            if ($result->isValid()) {
//                return true;
//            }else{
//                return false;
//            }
        }else{
            return true;
        }
    }
}