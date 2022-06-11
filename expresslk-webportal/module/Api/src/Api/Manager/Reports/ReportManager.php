<?php
namespace Api\Manager\Reports;

use Api\Manager\Base;
use Api\Client\Soap\Core\ReportCriteria;
use Api\Client\Soap\Core\ReportType;
use Api\Client\Soap\Core\Report;
use Api\Client\Soap\Core\Parameter;
use Api\Client\Soap\Core\reportTypesData;
use Application\Util\Language;

class ReportManager extends Base {

	/**
	 * Returns the list of report types
	 *
	 * @return ReportType[]
	 */
	public function getReportTypes()
	{
		$reportTypesResponse = $this->getReportService()->getReportTypes();
		//check if status is okay
		if ($this->responseIsValid($reportTypesResponse)) {
			/* @var $typesData reportTypesData */
			$typesData = $reportTypesResponse->data;
			$reportTypes = $typesData->reportTypes;

			if (! is_array($reportTypes)) {
				$reportTypes = array($reportTypes);
			}
			foreach ($reportTypes as $reportType) {
				/* @var $reportType ReportType */
				if (! is_array($reportType->parameters)) {
					$reportType->parameters = array($reportType->parameters);
				}
			}
			return $reportTypes;
		}
	}

	/**
	 * Returns the generated report
	 *
	 * @param ReportType $type
	 * @param array      $params
	 *
	 * @return Report
	 */
	public function generateReport($type, $params)
	{
		// prepare report criteria
		$reportParams = array();
		foreach ($type->parameters as $p) {
			$p->values = array();
			if (isset($params[$p->name])) {
				if (is_array($params[$p->name])) {
					$p->values = $params[$p->name];
				} else {
					$p->values[] = $params[$p->name];
				}
			}
			$reportParams[] = $p;
		}

		/** @var ReportCriteria $reportCriteria */
		$reportCriteria = Base::getEntityObject('ReportCriteria');//new ReportCriteria($type->reportType);
		$reportCriteria->reportParams = $reportParams;
		$reportCriteria->reportType = $type->reportType;
		$reportCriteria->langCode = Language::getLocaleLang(Language::getCurrentLocale());

		// generate report
		$reportResponse = $this->getReportService()->generateReport($reportCriteria);
		//check if status is okay
		if ($this->responseIsValid($reportResponse)) {
			/* @var Report $data */
			$data = $reportResponse->data;
			return $data;
		}
	}
}
