<?php

namespace Api\Client\Mobitel;

use Api\Client\Mobitel\MCash\ExternalChargingService;
use Zend\ServiceManager\ServiceManager;

class MCashService extends ExternalChargingService
{
	protected $_config;
	protected $_serviceLocator;

	/**
	 * @param ServiceManager $serviceManager
	 */
	public function __construct(ServiceManager $serviceManager)
	{
		//save service manager
		$this->_serviceLocator = $serviceManager;
		//get config
		$this->_config = $serviceManager->get('Config');
		//call parent with wsdl and options
		parent::__construct(array(), $this->_config['client-api']['mobitel']['mCash']['wsdl']);
	}

	/**
	 * Overwrites the original method adding the security header. As you can
	 * see, if you want to add more headers, the method needs to be modified.
	 */
	public function __soapCall($function_name, $arguments, $options=null, $input_headers=null, &$output_headers=null)
	{
		return parent::__soapCall($function_name, $arguments, $options, $this->generateWSSecurityHeader());
	}

	/**
	 * Generate password digest
	 *
	 * Using the password directly may work also, but it's not secure to
	 * transmit it without encryption. And anyway, at least with
	 * axis+wss4j, the nonce and timestamp are mandatory anyway.
	 *
	 * @return string   base64 encoded password digest
	 */
	private function generatePasswordDigest($nonce, $timestamp)
	{
		$packedNonce = pack('H*', $nonce);
		$packedTimestamp = pack('a*', $timestamp);
		$packedPassword = pack('a*', $this->_config['client-api']['mobitel']['mCash']['password']);
		$hash = sha1($packedNonce . $packedTimestamp . $packedPassword);
		$packedHash = pack('H*', $hash);
		return base64_encode($packedHash);
	}

	/**
	 * Generates WS-Security headers
	 *
	 * @return \SoapHeader
	 */
	private function generateWSSecurityHeader()
	{
		$nonce = mt_rand();
		$timestamp = gmdate('Y-m-d\TH:i:s\Z');
		$passwordDigest = $this->generatePasswordDigest($nonce, $timestamp);
		$xml = '
<wsse:Security SOAP-ENV:mustUnderstand="1" xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
    <wsse:UsernameToken>
        <wsse:Username>' . $this->_config['client-api']['mobitel']['mCash']['username'] . '</wsse:Username>
        <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest">' . $passwordDigest . '</wsse:Password>
        <wsse:Nonce>' . base64_encode(pack('H*', $nonce)) . '</wsse:Nonce>
        <wsu:Created xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">' . $timestamp . '</wsu:Created>
    </wsse:UsernameToken>
</wsse:Security>
';
		return new \SoapHeader('http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd',
				'Security',
				new \SoapVar($xml, XSD_ANYXML),
				true
		);
	}
}