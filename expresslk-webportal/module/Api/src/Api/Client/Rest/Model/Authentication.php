<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 11/22/14
 * Time: 1:42 PM
 */

namespace Api\Client\Rest\Model;


use Api\Client\Rest\Connector;
use Application\Util\Log;
use Zend\ServiceManager\ServiceManager;

class Authentication extends Connector{

    /**
     * Login
     * @param $username
     * @param $password
     * @return mixed
     * @throws \Exception
     */
    public function login($username, $password)
    {
        try {
            $url = $this->getBaseUrl() . '/user/authenticate';
            $response = $this->post($url, array(
                'username' => $username,
                'password' => $password
            ));

            return $response->body;
        } catch (\Exception $e) {
            throw $e;
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
        $url = $this->getBaseUrl() . '/user/getToken';
        $response = $this->post($url, array(
            'username' => $username,
            'password' => $password
        ));

        return $response->body;
    }

    /**
     * @param $username
     * @param $password
     * @param $newPassword
     * @return array|object|string
     */
    public function changePassword($username, $password, $newPassword)
    {
        $url = $this->getBaseUrl() . '/user/changePassword';
        $response = $this->post($url, array(
            'username' => $username,
            'password' => $password,
            'newPassword' => $newPassword
        ));

        return $response->body;
    }

    /**
     * @param $sUsername
     * @param $sToken
     * @return array|object|string
     */
    public function verifyToken($sUsername, $sToken)
    {
        $url = $this->getBaseUrl() . '/user/verifyToken';
        $response = $this->post($url, array(
            'username' => $sUsername,
            'token' => $sToken
        ));

        return $response->body;
	}
	
	/**
	 * Get a toekn for forgot password
	 *
	 * @return token
	 */
	public function forgotPassword($sUsername)
	{
		$url = $this->getBaseUrl() . '/user/forgotPassword';
        $response = $this->post($url, array(
            'username' => $sUsername,
        ));

        return $response->body;
	}

	/**
	 * Reset the user's password using existing token and a password
	 *
	 * @param [type] $sUsername
	 * @param [type] $resetToken
	 * @param [type] $newPassword
	 * @return void
	 */
	public function resetPassword($sUsername, $resetToken, $newPassword) 
	{
		$url = $this->getBaseUrl() . '/user/resetPassword';
        $response = $this->post($url, array(
			'username' => $sUsername,
			'resetToken' => $resetToken,
			'newPassword' => $newPassword
        ));

        return $response->body;
	}

    /**
     * @param $userGroupCode
     * @return \Api\Client\Soap\Core\UserGroup
     */
    public function getUserGroupByCode($userGroupCode)
    {
        $url = $this->getBaseUrl() . '/userGroup/byCode/'. $userGroupCode;
        $response = $this->get($url);

        return $response->body;
    }

} 