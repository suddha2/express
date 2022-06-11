<?php

namespace Api\Client\Soap\Core;

include_once('ExpResponse.php');
include_once('ValueType.php');
include_once('Entity.php');
include_once('LightEntity.php');
include_once('BusLight.php');
include_once('Gender.php');
include_once('ReportTypesResponse.php');
include_once('ReportType.php');
include_once('ReportParameter.php');
include_once('ReportCriteria.php');
include_once('GenerateReportResponse.php');
include_once('ReportInfo.php');
include_once('ReportTypeEnum.php');
include_once('BookingLight.php');
include_once('BusRouteLight.php');
include_once('BusScheduleLight.php');
include_once('UserLight.php');
include_once('ApplicationType.php');
include_once('Rule.php');
include_once('RuleCondition.php');
include_once('reportTypesData.php');

class ExpressLKReportServiceImplService extends \SoapClient
{

    /**
     * @var array $classmap The defined classes
     * @access private
     */
    private static $classmap = array(
      'ExpResponse' => 'Api\Client\Soap\Core\ExpResponse',
      'Entity' => 'Api\Client\Soap\Core\Entity',
      'LightEntity' => 'Api\Client\Soap\Core\LightEntity',
      'BusLight' => 'Api\Client\Soap\Core\BusLight',
      'ReportTypesResponse' => 'Api\Client\Soap\Core\ReportTypesResponse',
      'ReportType' => 'Api\Client\Soap\Core\ReportType',
      'ReportParameter' => 'Api\Client\Soap\Core\ReportParameter',
      'ReportCriteria' => 'Api\Client\Soap\Core\ReportCriteria',
      'GenerateReportResponse' => 'Api\Client\Soap\Core\GenerateReportResponse',
      'ReportInfo' => 'Api\Client\Soap\Core\ReportInfo',
      'BookingLight' => 'Api\Client\Soap\Core\BookingLight',
      'BusRouteLight' => 'Api\Client\Soap\Core\BusRouteLight',
      'BusScheduleLight' => 'Api\Client\Soap\Core\BusScheduleLight',
      'UserLight' => 'Api\Client\Soap\Core\UserLight',
      'Rule' => 'Api\Client\Soap\Core\Rule',
      'RuleCondition' => 'Api\Client\Soap\Core\RuleCondition',
      'reportTypesData' => 'Api\Client\Soap\Core\reportTypesData');

    /**
     * @param array $options A array of config values
     * @param string $wsdl The wsdl file to use
     * @access public
     */
    public function __construct(array $options = array(), $wsdl = 'http://167.114.96.6:7575/ExpressLK-reports/reports?wsdl')
    {
      foreach (self::$classmap as $key => $value) {
        if (!isset($options['classmap'][$key])) {
          $options['classmap'][$key] = $value;
        }
      }
      
      parent::__construct($wsdl, $options);
    }

    /**
     * @access public
     * @return ReportTypesResponse
     */
    public function getReportTypes()
    {
      return $this->__soapCall('getReportTypes', array());
    }

    /**
     * @access public
     * @return boolean
     */
    public function heartBeat()
    {
      return $this->__soapCall('heartBeat', array());
    }

    /**
     * @param ReportCriteria $arg0
     * @access public
     * @return GenerateReportResponse
     */
    public function generateReport(ReportCriteria $arg0)
    {
      return $this->__soapCall('generateReport', array($arg0));
    }

}
