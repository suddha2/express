<?php

namespace Api\Client\Mobitel\MCash;

include_once('ping.php');
include_once('pingResponse.php');
include_once('chargeFromCustomer.php');
include_once('chargingRequest.php');
include_once('chargeFromCustomerResponse.php');
include_once('chargingResult.php');
include_once('reverseToCustomer.php');
include_once('reversalRequest.php');
include_once('reverseToCustomerResponse.php');
include_once('reversalResult.php');

class ExternalChargingService extends \SoapClient
{

    /**
     * @var array $classmap The defined classes
     * @access private
     */
    private static $classmap = array(
      'ping' => 'Api\Client\Mobitel\MCash\ping',
      'pingResponse' => 'Api\Client\Mobitel\MCash\pingResponse',
      'chargeFromCustomer' => 'Api\Client\Mobitel\MCash\chargeFromCustomer',
      'chargingRequest' => 'Api\Client\Mobitel\MCash\chargingRequest',
      'chargeFromCustomerResponse' => 'Api\Client\Mobitel\MCash\chargeFromCustomerResponse',
      'chargingResult' => 'Api\Client\Mobitel\MCash\chargingResult',
      'reverseToCustomer' => 'Api\Client\Mobitel\MCash\reverseToCustomer',
      'reversalRequest' => 'Api\Client\Mobitel\MCash\reversalRequest',
      'reverseToCustomerResponse' => 'Api\Client\Mobitel\MCash\reverseToCustomerResponse',
      'reversalResult' => 'Api\Client\Mobitel\MCash\reversalResult');

    /**
     * @param array $options A array of config values
     * @param string $wsdl The wsdl file to use
     * @access public
     */
    public function __construct(array $options = array(), $wsdl = 'https://apphubdev.mobitel.lk/services/MWT/ExternalChargingService?wsdl')
    {
      foreach (self::$classmap as $key => $value) {
    if (!isset($options['classmap'][$key])) {
      $options['classmap'][$key] = $value;
    }
  }
  
  parent::__construct($wsdl, $options);
    }

    /**
     * @param chargeFromCustomer $chargeFromCustomer
     * @access public
     * @return chargeFromCustomerResponse
     */
    public function chargeFromCustomer(chargeFromCustomer $chargeFromCustomer)
    {
      return $this->__soapCall('chargeFromCustomer', array($chargeFromCustomer));
    }

    /**
     * @param ping $ping
     * @access public
     * @return pingResponse
     */
    public function ping(ping $ping)
    {
      return $this->__soapCall('ping', array($ping));
    }

    /**
     * @param reverseToCustomer $reverseToCustomer
     * @access public
     * @return reverseToCustomerResponse
     */
    public function reverseToCustomer(reverseToCustomer $reverseToCustomer)
    {
      return $this->__soapCall('reverseToCustomer', array($reverseToCustomer));
    }

}
