<?php

namespace Api\Client\Mobitel\CGW;

include_once('msisdnInfo.php');
include_once('msisdnInfoResponse.php');
include_once('infoResult.php');
include_once('chargeFromMSISDN.php');
include_once('chgRequest.php');
include_once('chargeFromMSISDNResponse.php');
include_once('chgResult.php');
include_once('hello.php');
include_once('helloResponse.php');
include_once('checkPrepaidBalance.php');
include_once('checkPrepaidBalanceResponse.php');
include_once('balanceResult.php');

class MCAccMgr extends \SoapClient
{

    /**
     * @var array $classmap The defined classes
     * @access private
     */
    private static $classmap = array(
      'msisdnInfo' => 'Api\Client\Mobitel\CGW\msisdnInfo',
      'msisdnInfoResponse' => 'Api\Client\Mobitel\CGW\msisdnInfoResponse',
      'infoResult' => 'Api\Client\Mobitel\CGW\infoResult',
      'chargeFromMSISDN' => 'Api\Client\Mobitel\CGW\chargeFromMSISDN',
      'chgRequest' => 'Api\Client\Mobitel\CGW\chgRequest',
      'chargeFromMSISDNResponse' => 'Api\Client\Mobitel\CGW\chargeFromMSISDNResponse',
      'chgResult' => 'Api\Client\Mobitel\CGW\chgResult',
      'hello' => 'Api\Client\Mobitel\CGW\hello',
      'helloResponse' => 'Api\Client\Mobitel\CGW\helloResponse',
      'checkPrepaidBalance' => 'Api\Client\Mobitel\CGW\checkPrepaidBalance',
      'checkPrepaidBalanceResponse' => 'Api\Client\Mobitel\CGW\checkPrepaidBalanceResponse',
      'balanceResult' => 'Api\Client\Mobitel\CGW\balanceResult');

    /**
     * @param array $options A array of config values
     * @param string $wsdl The wsdl file to use
     * @access public
     */
    public function __construct(array $options = array(), $wsdl = 'http://mapps.mobitel.lk/M_CGW/MCAccMngr?wsdl')
    {
      foreach (self::$classmap as $key => $value) {
        if (!isset($options['classmap'][$key])) {
          $options['classmap'][$key] = $value;
        }
      }
      
      parent::__construct($wsdl, $options);
    }

    /**
     * @param msisdnInfo $parameters
     * @access public
     * @return msisdnInfoResponse
     */
    public function msisdnInfo(msisdnInfo $parameters)
    {
      return $this->__soapCall('msisdnInfo', array($parameters));
    }

    /**
     * @param chargeFromMSISDN $parameters
     * @access public
     * @return chargeFromMSISDNResponse
     */
    public function chargeFromMSISDN(chargeFromMSISDN $parameters)
    {
      return $this->__soapCall('chargeFromMSISDN', array($parameters));
    }

    /**
     * @param hello $parameters
     * @access public
     * @return helloResponse
     */
    public function hello(hello $parameters)
    {
      return $this->__soapCall('hello', array($parameters));
    }

    /**
     * @param checkPrepaidBalance $parameters
     * @access public
     * @return checkPrepaidBalanceResponse
     */
    public function checkPrepaidBalance(checkPrepaidBalance $parameters)
    {
      return $this->__soapCall('checkPrepaidBalance', array($parameters));
    }

}
