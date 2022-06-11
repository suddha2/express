<?php

namespace Api\Client\Soap\Core;

class ReportCriteria
{

    /**
     * @var string[] $dispatchMethods
     * @access public
     */
    public $dispatchMethods = null;

    /**
     * @var string $format
     * @access public
     */
    public $format = null;

    /**
     * @var string $langCode
     * @access public
     */
    public $langCode = null;

    /**
     * @var ReportParameter[] $reportParams
     * @access public
     */
    public $reportParams = null;

    /**
     * @var ReportTypeEnum $reportType
     * @access public
     */
    public $reportType = null;

    /**
     * @param string $format
     * @param string $langCode
     * @param ReportTypeEnum $reportType
     * @access public
     */
    public function __construct($format, $langCode, $reportType)
    {
      $this->format = $format;
      $this->langCode = $langCode;
      $this->reportType = $reportType;
    }

}
