<?php

namespace Api\Client\Soap;

use Application\Acl\Acl;

trait ExpressWebService {

	private $_config;

    protected $_serviceLocator;

	/**
	 * Set soap headers
	 */
	protected function setHeaders()
	{
		$ns = 'http://namespace.example.com/'; //Namespace of the WS.

		//Create Soap Header for authentication.
        //get logged in user
        $oUser = Acl::getAuthUser($this->_serviceLocator);
		$headers = array();
		$headers[] = new \SoapHeader($ns, 'username', $oUser->username);
		$headers[] = new \SoapHeader($ns, 'token', $oUser->token);

		//save headers
		$this->__setSoapHeaders($headers);
	}
}
