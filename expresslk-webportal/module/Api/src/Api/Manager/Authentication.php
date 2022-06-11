<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 11/22/14
 * Time: 7:34 PM
 */

namespace Api\Manager;


use Api\Client\Soap\Core\Division;
use Api\Client\Soap\Core\User;
use Application\Acl\Acl;

class Authentication extends Base{

    /**
     * return user after login
     * @param $username
     * @param $password
     * @return \stdClass
     * @throws \Exception
     */
    public function login($username, $password)
    {
        $authService = new \Api\Client\Rest\Model\Authentication($this->getServiceManager());
        //disable auth check for login
        $authService->setAuthHeadersEnabled(false);
        $response = $authService->login($username, $password);

        //check if id exists, otherwise throw error
        if($response && isset($response->id)){
            //format data
            $this->formatUserData($response);
            //send data back
            return $response;
        }else{
            throw new \Exception('Response not properly formated');
        }

    }

    /**
     * @param $username
     * @param $password
     * @return string
     * @throws \Exception
     */
    public function getToken($username, $password)
    {
        $authService = new \Api\Client\Rest\Model\Authentication($this->getServiceManager());
        //disable auth check for get token
        $authService->setAuthHeadersEnabled(false);
        $response = $authService->getToken($username, $password);

        //check if id exists, otherwise throw error
        if($response){
            //send data back
            return $response;
        }else{
            throw new \Exception('Response not properly formated');
        }
    }

    /**
     * @param $username
     * @param $password
     * @param $newPassword
     * @return array|object|string
     * @throws \Exception
     */
    public function changePassword($username, $password, $newPassword)
    {
        $authService = new \Api\Client\Rest\Model\Authentication($this->getServiceManager());
        $response = $authService->changePassword($username, $password, $newPassword);

        //check if id exists, otherwise throw error
        if($response){
            //send data back
            return $response;
        }else{
            throw new \Exception('Response not properly formated');
        }
	}
	
	public function changePasswordWithouotOldPassword($username, $password)
	{
		$authService = new \Api\Client\Rest\Model\Authentication($this->getServiceManager());
		$token = $authService->forgotPassword($username);
		//pass token and new password
		$response = $authService->resetPassword($username, $token, $password);

        //check if id exists, otherwise throw error
        if($response){
            //send data back
            return $response;
        }else{
            throw new \Exception('Response not properly formated');
        }
	}

    /**
     * @param $sUsername
     * @param $sToken
     * @return boolean
     * @throws \Exception
     */
    public function tokenIsValid($sUsername, $sToken)
    {
        $authService = new \Api\Client\Rest\Model\Authentication($this->getServiceManager());
        //disable auth check for get token
        $authService->setAuthHeadersEnabled(false);
        $response = $authService->verifyToken($sUsername, $sToken);

        //check if id exists, otherwise throw error
        if($response){
            //send data back
            return (strtolower($response->result)==='true');
        }else{
            throw new \Exception('Token Response not properly formated');
        }
    }

    /**
     * @param $userGroupCode
     * @return \Api\Client\Soap\Core\UserGroup
     * @throws \Exception
     */
    public function getUserGroupByCode($userGroupCode)
    {
        //set up cache
        $oCache = $this->getServiceManager()->get('cache');
        $key = 'Api-usergroupByCode-'. $userGroupCode;

        $success = false;
        //get from cache
        $result = $oCache->getItem($key, $success);
        if(!$success){
            $authService = new \Api\Client\Rest\Model\Authentication($this->getServiceManager());
            $result = $authService->getUserGroupByCode($userGroupCode);

            //cache result only if the response is proper
            if(!empty($result)){
                $oCache->setItem($key, $result);
            }else{
                throw new \Exception('Response not properly formatted');
            }
        }

        return $result;
    }

    /**
     * @param $user User
     */
    private function formatUserData(&$user)
    {
        //format permissions
        $divisionsAr = array();
        $roles = array(Acl::ROLE_GUEST);
        $allowedResources = array();

        if($user->userGroups && is_array($user->userGroups)){
            $roles = array();
            foreach($user->userGroups as $group){
                $role = $group->code;
                //set user roles
                $roles[] = $role;

                //set user permissions for resources
//                foreach ($group->permission as $permission) {
//                    //set allowed resource if not exists
//                    if(!isset($allowedResources[$permission->code])){
//                        $allowedResources[$permission->code] = array(
//                            // 'all' means allow all within the resource
//                            'all' => array($role)
//                        );
//                    }else{
//                        //'all' key already exists for the resource, add role
//                        $allowedResources[$permission->code]['all'][] = $role;
//                    }
//                }

                //divisions
                $divisionsAr = array_merge($divisionsAr, $group->visibleDivisions);
            }
        }
        //save roles
        $user->roles = $roles;
        //save allowed resources
        $user->allowedResources = $allowedResources;
        //save divisions
        $divisions = array();
        //re order array to get divisions and bitmask as key
        if(!empty($divisionsAr)){
            foreach ($divisionsAr as $div) {
                /** @var $div Division */
                $divisions[$div->bitmask] = $div;
            }

        }
        $user->divisions = $divisions;
    }
}