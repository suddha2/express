<?php

namespace Api\Client\Soap\Core;

include_once('Entity.php');

class ReportType extends Entity
{

    /**
     * @var int $allowedDivisions
     * @access public
     */
    public $allowedDivisions = null;

    /**
     * @var int $id
     * @access public
     */
    public $id = null;

    /**
     * @var ReportParameter[] $parameters
     * @access public
     */
    public $parameters = null;

    /**
     * @var string $reportName
     * @access public
     */
    public $reportName = null;

    /**
     * @var ReportTypeEnum $reportType
     * @access public
     */
    public $reportType = null;

    /**
     * @param int $allowedDivisions
     * @param int $id
     * @param string $reportName
     * @param ReportTypeEnum $reportType
     * @access public
     */
    public function __construct($allowedDivisions, $id, $reportName, $reportType)
    {
      $this->allowedDivisions = $allowedDivisions;
      $this->id = $id;
      $this->reportName = $reportName;
      $this->reportType = $reportType;
    }

}
