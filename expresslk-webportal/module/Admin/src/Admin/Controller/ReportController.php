<?php
namespace Admin\Controller;

use Zend\Mvc\Controller\AbstractActionController;
use Zend\View\Model\JsonModel;
use Zend\View\Model\ViewModel;
use Zend\ServiceManager\ServiceManager;
use Application\Acl\Acl;
use Api\Client\Rest\Factory as RestFactory;
use Api\Operation\Request\QueryCriteria;

class ReportController extends AbstractActionController
{
    public function indexAction()
    {
        //create html view
        $oView = new ViewModel();
        $oView->setTerminal(true);

        $oBus = $this->getServiceLocator()->get('Api\Manager\BusLight');
        $oView->buses = $oBus->getBusListFull();

        $oUser = $this->getServiceLocator()->get('Api\Manager\UserLight');
        $user = Acl::getAuthUser($this->getServiceLocator());
        $oView->users = $oUser->getUsersByDivision($user->divisionId);

        return $oView;
    }

    public function getreportlistAction()
    {
        $result = array();
        try {
            /* @var $reportManager \Api\Manager\Reports\ReportManager */
            $reportManager = $this->getServiceLocator()->get('Api\Manager\Reports\ReportManager');
            $result = $reportManager->getReportTypes();
        } catch (\Exception $e) {
            $result['error'] = $e->getMessage();
        }

        return new JsonModel($result);
    }
	
	public function getsupplierlistAction(){
		// Supplier Service
		$oSupplier = $this->getServiceLocator()->get('Api\Manager\Supplier');
		// Current logged in user instance.
		$auth = $this->getServiceLocator()->get('AuthService');
		$loggedUser = $auth->getIdentity();

		
		$suppliers =  $oSupplier->getSupplier(null,$loggedUser->visibleDivisionsBitmask);
		return new JsonModel($suppliers);
	}
	
    public function generatereportAction()
    {
        $result = array();
        try {
            $reqestData = $this->params()->fromPost();
            $typeName = $reqestData['type'];
            $parameters = $reqestData['parameters'];

            /* @var $reportManager \Api\Manager\Reports\ReportManager */
            $reportManager = $this->getServiceLocator()->get('Api\Manager\Reports\ReportManager');
            $reportTypes = $reportManager->getReportTypes();

            $type = null;
            foreach ($reportTypes as $reportType) {
                if ($typeName == $reportType->reportType) {
                    $type = $reportType;
                }
            }

            if ($type) {
                $report = $reportManager->generateReport($type, $parameters);
                //assume this is a pdf and return inline pdf
                header("Content-type: application/pdf");
                header('Content-Transfer-Encoding: binary');
                header("Content-Disposition: inline; filename='". urlencode($typeName).'-'.date('Y-m-d-H') ."'");
                echo $report->bytes;
                exit;
            } else {
                $result['error'] = 'Could not find the report';
            }
        } catch (\Exception $e) {
            $result['error'] = $e->getMessage();
        }

        return new JsonModel($result);
    }

    private function _getServicePath()
    {
        $config = $this->getServiceLocator()->get('Config');
        $parsed = parse_url($config['wsdl']['reports']);
        $url = $parsed['scheme'] . "://" . $config['system']['serverName'] . ":" . $parsed['port'] . "/" . explode("/", $parsed['path'])[1] . "/";
        return $url;
    }
	
	public function expinvoiceAction(){
		$oView = new ViewModel();
        $oView->setTerminal(true);
		 return $oView;
	}
	public function approvedRefundsAction(){
		$oView = new ViewModel();
		$oView->setTemplate('admin/report/approvedRefundReport');
        $oView->setTerminal(true);
		 return $oView;
	}
	public function approvedRefundsV2Action(){
		$oView = new ViewModel();
		$oView->setTemplate('admin/report/approvedRefundReportV2');
        $oView->setTerminal(true);
		 return $oView;
	}
	public function futureReservationReportAction(){
		$oView = new ViewModel();
		$oView->setTemplate('admin/report/futureReservationReport');
        $oView->setTerminal(true);
		return $oView;
	}
	public function dailyCashReconciliationAction(){
		$oView = new ViewModel();
		$oView->setTemplate('admin/report/dailyCashReconciliation');
        $oView->setTerminal(true);
		return $oView;
	}
	public function dailyCashReconciliationV2Action(){
		$oView = new ViewModel();
		$oView->setTemplate('admin/report/dailyCashReconciliationV2');
        $oView->setTerminal(true);
		return $oView;
	}
	public function cashReconciliationAction(){
		$oView = new ViewModel();
		$oView->setTemplate('admin/report/cashReconciliation');
        $oView->setTerminal(true);
		return $oView;
	}
	public function operatedBusFareAction(){
		$oView = new ViewModel();
		$oView->setTemplate('admin/report/operatedBusFare');
        $oView->setTerminal(true);
		return $oView;
	}
	public function operatedBusFeeAction(){
		$oView = new ViewModel();
		$oView->setTemplate('admin/report/operatedBusFee');
        $oView->setTerminal(true);
		return $oView;
	}
	public function ticketCounterReconciliationAction(){
		$oView = new ViewModel();
		$oView->setTemplate('admin/report/ticketCounterReconciliation');
        $oView->setTerminal(true);
		return $oView;
	}
	public function depotOperatedBusFareAction(){
		$oView = new ViewModel();
		$oView->setTemplate('admin/report/depotOperatedBusFare');
        $oView->setTerminal(true);
		
		return $oView;
	}
	public function expinvoiceListAction(){
		$oView = new JsonModel();
        $auth = $this->getServiceLocator()->get('AuthService');
        $aParams = $this->params()->fromJson();
		if(empty($aParams['from'])){
			$result['error'] ="Invalid from date - ".$aParams['from']." Must be <MM>-<YYYY>";
			return new JsonModel($result);
		}
		$fromTime =  $aParams['from'] ;
		// $result['error'] = $fromTime;
		// return new JsonModel($result);
		$fromDateUTC = \DateTime::createFromFormat('Y-m-d\TH:i:s.u\Z', $fromTime ,new \DateTimeZone('UTC'));
		
		
		$fromDate = $fromDateUTC;
		$fromDate->setTimezone(new \DateTimeZone(date_default_timezone_get()));
		$params = $fromDate->format("Y-m-d");
		
		
		$params = date('Y-m-01', strtotime($fromDate->format("Y-m-d")));
		$params .= "/".date('Y-m-t', strtotime($fromDate->format("Y-m-d")));
		
		// $result['error'] = $params;
		// return new JsonModel($result);
		
        $oCollection = new RestFactory($this->getServiceLocator(), 'report/expInvoice/'. $params );
        $oQuery = new QueryCriteria();
        $oCollectionResult = $oCollection->getList($oQuery);

        $result = $oCollectionResult->body;
        
		
		return new JsonModel($result);
	}
	public function  approvedRefundsListAction(){
		$oView = new JsonModel();
        $auth = $this->getServiceLocator()->get('AuthService');
        $aParams = $this->params()->fromJson();
		if(empty($aParams['from'])){
			$result['error'] ="Invalid from date - ".$aParams['from']." Must be <MM>-<YYYY>";
			return new JsonModel($result);
		}
		$fromTime =  $aParams['from'] ;
		// $result['error'] = $fromTime;
		// return new JsonModel($result);
		$fromDateUTC = \DateTime::createFromFormat('Y-m-d\TH:i:s.u\Z', $fromTime ,new \DateTimeZone('UTC'));
		
		
		$fromDate = $fromDateUTC;
		$fromDate->setTimezone(new \DateTimeZone(date_default_timezone_get()));
		$params = $fromDate->format("Y-m-d");
		
		
		$params = date('Y-m-01', strtotime($fromDate->format("Y-m-d")));
		$params .= "/".date('Y-m-t', strtotime($fromDate->format("Y-m-d")));
		
		// $result['error'] = $params;
		// return new JsonModel($result);
		
        if( $fromDate < '2018-10-01'){
			$oCollection = new RestFactory($this->getServiceLocator(), 'report/approvedRefunds/'. $params );
        }else {
			$oCollection = new RestFactory($this->getServiceLocator(), 'report/approvedRefundsV2/'. $params );
		}
		
		$oQuery = new QueryCriteria();
        $oCollectionResult = $oCollection->getList($oQuery);

        $result = $oCollectionResult->body;
        
		
		return new JsonModel($result);
	}
	public function  approvedRefundsV2ListAction(){
		$oView = new JsonModel();
        $auth = $this->getServiceLocator()->get('AuthService');
        $aParams = $this->params()->fromJson();
		if(empty($aParams['from'])){
			$result['error'] ="Invalid from date - ".$aParams['from']." Must be <MM>-<YYYY>";
			return new JsonModel($result);
		}
		$fromTime =  $aParams['from'] ;
		// $result['error'] = $fromTime;
		// return new JsonModel($result);
		$fromDateUTC = \DateTime::createFromFormat('Y-m-d\TH:i:s.u\Z', $fromTime ,new \DateTimeZone('UTC'));
		
		
		$fromDate = $fromDateUTC;
		$fromDate->setTimezone(new \DateTimeZone(date_default_timezone_get()));
		$params = $fromDate->format("Y-m-d");
		
		
		$params = date('Y-m-01', strtotime($fromDate->format("Y-m-d")));
		$params .= "/".date('Y-m-t', strtotime($fromDate->format("Y-m-d")));
		
		// $result['error'] = $params;
		// return new JsonModel($result);
		
        $oCollection = new RestFactory($this->getServiceLocator(), 'report/approvedRefundsV2/'. $params );
        $oQuery = new QueryCriteria();
        $oCollectionResult = $oCollection->getList($oQuery);

        $result = $oCollectionResult->body;
        
		
		return new JsonModel($result);
	}

	public function  cashreconListAction(){
		$oView = new JsonModel();
        $auth = $this->getServiceLocator()->get('AuthService');
        $aParams = $this->params()->fromJson();
		if(empty($aParams['from'])){
			$result['error'] ="Invalid from date - ".$aParams['from']." Must be <MM>-<YYYY>";
			return new JsonModel($result);
		}
		$fromTime =  $aParams['from'] ;
		// $result['error'] = $fromTime;
		// return new JsonModel($result);
		$fromDateUTC = \DateTime::createFromFormat('Y-m-d\TH:i:s.u\Z', $fromTime ,new \DateTimeZone('UTC'));
		
		
		$fromDate = $fromDateUTC;
		$fromDate->setTimezone(new \DateTimeZone(date_default_timezone_get()));
		$params = $fromDate->format("Y-m-d");
		
		
		$params = date('Y-m-01', strtotime($fromDate->format("Y-m-d")));
		$params .= "/".date('Y-m-t', strtotime($fromDate->format("Y-m-d")));
		
		// $result['error'] = $params;
		// return new JsonModel($result);
		
        $oCollection = new RestFactory($this->getServiceLocator(), 'report/cashReconciliation/'. $params );
        $oQuery = new QueryCriteria();
        $oCollectionResult = $oCollection->getList($oQuery);

        $result = $oCollectionResult->body;
        
		
		return new JsonModel($result);
	}
	public function dailycashreconListAction(){
		$oView = new JsonModel();
        $auth = $this->getServiceLocator()->get('AuthService');
        $aParams = $this->params()->fromJson();
		if(empty($aParams['from'])){
			$result['error'] ="Invalid from date - ".$aParams['from']." Must be <MM>-<YYYY>";
			return new JsonModel($result);
		}
		$fromTime =  $aParams['from'] ;
		// $result['error'] = $fromTime;
		// return new JsonModel($result);
		$fromDateUTC = \DateTime::createFromFormat('Y-m-d\TH:i:s.u\Z', $fromTime ,new \DateTimeZone('UTC'));
		
		
		$fromDate = $fromDateUTC;
		$fromDate->setTimezone(new \DateTimeZone(date_default_timezone_get()));
		$params = $fromDate->format("Y-m-d");
		
		
		$params = date('Y-m-01', strtotime($fromDate->format("Y-m-d")));
		$params .= "/".date('Y-m-t', strtotime($fromDate->format("Y-m-d")));
		
		// $result['error'] = $params;
		// return new JsonModel($result);
		
        $oCollection = new RestFactory($this->getServiceLocator(), 'report/dailyCashReconciliation/'. $params );
        $oQuery = new QueryCriteria();
        $oCollectionResult = $oCollection->getList($oQuery);

        $result = $oCollectionResult->body;
        
		return new JsonModel($result);
	}

	public function dailycashreconV2ListAction(){
		$oView = new JsonModel();
        $auth = $this->getServiceLocator()->get('AuthService');
        $aParams = $this->params()->fromJson();
		if(empty($aParams['from'])){
			$result['error'] ="Invalid from date - ".$aParams['from']." Must be <MM>-<YYYY>";
			return new JsonModel($result);
		}
		$fromTime =  $aParams['from'] ;
		// $result['error'] = $fromTime;
		// return new JsonModel($result);
		$fromDateUTC = \DateTime::createFromFormat('Y-m-d\TH:i:s.u\Z', $fromTime ,new \DateTimeZone('UTC'));
		
		
		$fromDate = $fromDateUTC;
		$fromDate->setTimezone(new \DateTimeZone(date_default_timezone_get()));
		$params = $fromDate->format("Y-m-d");
		
		
		$params = date('Y-m-01', strtotime($fromDate->format("Y-m-d")));
		$params .= "/".date('Y-m-t', strtotime($fromDate->format("Y-m-d")));
		
		// $result['error'] = $params;
		// return new JsonModel($result);
		
        $oCollection = new RestFactory($this->getServiceLocator(), 'report/dailyCashReconciliationV2/'. $params );
        $oQuery = new QueryCriteria();
        $oCollectionResult = $oCollection->getList($oQuery);

        $result = $oCollectionResult->body;
        
		return new JsonModel($result);
	}

	public function futureresListAction(){
		$oView = new JsonModel();
        $auth = $this->getServiceLocator()->get('AuthService');
        $aParams = $this->params()->fromJson();
		if(empty($aParams['from'])){
			$result['error'] ="Invalid from date - ".$aParams['from']." Must be <MM>-<YYYY>";
			return new JsonModel($result);
		}
		$fromTime =  $aParams['from'] ;
		// $result['error'] = $fromTime;
		// return new JsonModel($result);
		$fromDateUTC = \DateTime::createFromFormat('Y-m-d\TH:i:s.u\Z', $fromTime ,new \DateTimeZone('UTC'));
		
		
		$fromDate = $fromDateUTC;
		$fromDate->setTimezone(new \DateTimeZone(date_default_timezone_get()));
		$params = $fromDate->format("Y-m-d");
		
		
		$params = date('Y-m-01', strtotime($fromDate->format("Y-m-d")));
		$params .= "/".date('Y-m-t', strtotime($fromDate->format("Y-m-d")));
		
		// $result['error'] = $params;
		// return new JsonModel($result);
		
        $oCollection = new RestFactory($this->getServiceLocator(), 'report/futureresList/'. $params );
        $oQuery = new QueryCriteria();
        $oCollectionResult = $oCollection->getList($oQuery);

        $result = $oCollectionResult->body;
        
		return new JsonModel($result);
	}
	public function busfareListAction(){
		$oView = new JsonModel();
        $auth = $this->getServiceLocator()->get('AuthService');
        $aParams = $this->params()->fromJson();
		if(empty($aParams['from'])){
			$result['error'] ="Invalid from date - ".$aParams['from']." Must be <MM>-<YYYY>";
			return new JsonModel($result);
		}
		$fromTime =  $aParams['from'] ;
		// $result['error'] = $fromTime;
		// return new JsonModel($result);
		$fromDateUTC = \DateTime::createFromFormat('Y-m-d\TH:i:s.u\Z', $fromTime ,new \DateTimeZone('UTC'));
		
		
		$fromDate = $fromDateUTC;
		$fromDate->setTimezone(new \DateTimeZone(date_default_timezone_get()));
		$params = $fromDate->format("Y-m-d");
		
		
		$params = date('Y-m-01', strtotime($fromDate->format("Y-m-d")));
		$params .= "/".date('Y-m-t', strtotime($fromDate->format("Y-m-d")));
		
		// $result['error'] = $params;
		// return new JsonModel($result);
		
        $oCollection = new RestFactory($this->getServiceLocator(), 'report/busfareList/'. $params );
        $oQuery = new QueryCriteria();
        $oCollectionResult = $oCollection->getList($oQuery);

        $result = $oCollectionResult->body;
        
		return new JsonModel($result);
	}
	public function busfeeListAction(){
		$oView = new JsonModel();
        $auth = $this->getServiceLocator()->get('AuthService');
        $aParams = $this->params()->fromJson();
		if(empty($aParams['from'])){
			$result['error'] ="Invalid from date - ".$aParams['from']." Must be <MM>-<YYYY>";
			return new JsonModel($result);
		}
		$fromTime =  $aParams['from'] ;
		// $result['error'] = $fromTime;
		// return new JsonModel($result);
		$fromDateUTC = \DateTime::createFromFormat('Y-m-d\TH:i:s.u\Z', $fromTime ,new \DateTimeZone('UTC'));
		
		
		$fromDate = $fromDateUTC;
		$fromDate->setTimezone(new \DateTimeZone(date_default_timezone_get()));
		$params = $fromDate->format("Y-m-d");
		
		
		$params = date('Y-m-01', strtotime($fromDate->format("Y-m-d")));
		$params .= "/".date('Y-m-t', strtotime($fromDate->format("Y-m-d")));
		
		// $result['error'] = $params;
		// return new JsonModel($result);
		
        $oCollection = new RestFactory($this->getServiceLocator(), 'report/busfareList/'. $params );
        $oQuery = new QueryCriteria();
        $oCollectionResult = $oCollection->getList($oQuery);

        $result = $oCollectionResult->body;
        
		return new JsonModel($result);
	}
	public function ticketCounterReconciliationListAction(){
		$oView = new JsonModel();
        $auth = $this->getServiceLocator()->get('AuthService');
        $aParams = $this->params()->fromJson();
		if(empty($aParams['from'])){
			$result['error'] ="Invalid from date - ".$aParams['from']." Must be <MM>-<YYYY>";
			return new JsonModel($result);
		}
		$fromTime =  $aParams['from'] ;
		// $result['error'] = $fromTime;
		// return new JsonModel($result);
		$fromDateUTC = \DateTime::createFromFormat('Y-m-d\TH:i:s.u\Z', $fromTime ,new \DateTimeZone('UTC'));
		
		
		$fromDate = $fromDateUTC;
		$fromDate->setTimezone(new \DateTimeZone(date_default_timezone_get()));
		$params = $fromDate->format("Y-m-d");
		
		
		$params = date('Y-m-01', strtotime($fromDate->format("Y-m-d")));
		$params .= "/".date('Y-m-t', strtotime($fromDate->format("Y-m-d")));
		
		// $result['error'] = $params;
		// return new JsonModel($result);
		
        $oCollection = new RestFactory($this->getServiceLocator(), 'report/counterCashRecon/'. $params );
        $oQuery = new QueryCriteria();
        $oCollectionResult = $oCollection->getList($oQuery);

        $result = $oCollectionResult->body;
        
		return new JsonModel($result);
	}
	
	public function depotBusfareListAction(){
		$oView = new JsonModel();
        $auth = $this->getServiceLocator()->get('AuthService');
        $aParams = $this->params()->fromJson();
		if(empty($aParams['from'])){
			$result['error'] ="Invalid from date - ".$aParams['from']." Must be <MM>-<YYYY>";
			return new JsonModel($result);
		}
		$fromTime =  $aParams['from'] ;
		$supplierID = $aParams['supplier'] ;
		// $result['error'] = $fromTime;
		// return new JsonModel($result);
		$fromDateUTC = \DateTime::createFromFormat('Y-m-d\TH:i:s.u\Z', $fromTime ,new \DateTimeZone('UTC'));
		
		
		$fromDate = $fromDateUTC;
		$fromDate->setTimezone(new \DateTimeZone(date_default_timezone_get()));
		$params = $fromDate->format("Y-m-d");
		
		
		$params = date('Y-m-01', strtotime($fromDate->format("Y-m-d")));
		$params .= "/".date('Y-m-t', strtotime($fromDate->format("Y-m-d")));
		$params .= "/".$supplierID;
		// $result['error'] = $params;
		// return new JsonModel($result);
		
        $oCollection = new RestFactory($this->getServiceLocator(), 'report/depotBusfareList/'. $params );
        $oQuery = new QueryCriteria();
        $oCollectionResult = $oCollection->getList($oQuery);

        $result = $oCollectionResult->body;
        
		return new JsonModel($result);
	}
	
}	
