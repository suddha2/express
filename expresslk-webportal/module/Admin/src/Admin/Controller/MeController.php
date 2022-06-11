<?php


namespace Admin\Controller;


use Api\Operation\Request\QueryCriteria;
use Application\Helper\ExprDateTime;
use Zend\Mvc\Controller\AbstractActionController;
use Zend\View\Model\JsonModel;
use Zend\View\Model\ViewModel;
use Api\Client\Rest\Factory as RestFactory;





class MeController extends AbstractActionController
{
    
	public function indexAction()
    {
        $oView = new ViewModel();
        $oView->setTerminal(true);

        return $oView;
    }

    public function collectionAction()
    {
        $oView = new ViewModel();
        $oView->setTerminal(true);

        return $oView;
    }

    public function collectionreportAction()
    {
        $oView = new JsonModel();
        $auth = $this->getServiceLocator()->get('AuthService');
        $aParams = $this->params()->fromJson();
        //prepare parameters
        $fromTime = ExprDateTime::getDateFromString(empty($aParams['from'])? date('Y-m-d'): $aParams['from']);
        $toTime = ExprDateTime::getDateFromString(empty($aParams['to'])? date('Y-m-d'): $aParams['to']);

        $params = $auth->getIdentity()->id . '/'. $fromTime->setTime(0, 0, 0)->getTimestampMiliSeconds()
            .'/'. $toTime->setTime(0, 0, 0)->getTimestampMiliSeconds();

        $oCollection = new RestFactory($this->getServiceLocator(), 'userCollection/'. $params);
        $oQuery = new QueryCriteria();
        $oCollectionResult = $oCollection->getList($oQuery);

        $oView->collection = $oCollectionResult->body;
        return $oView;
    }
	
	// Collection Report function 
	public function collectionprintAction()
	{
		$auth = $this->getServiceLocator()->get('AuthService');
        
		$fromDate = $this->params()->fromQuery('from');
		$toDate = $this->params()->fromQuery('to');
		
        //prepare parameters
        //$fromTime = ExprDateTime::getDateFromString(empty($fromDate)? date('Y-m-d'): fromDate);
        //$toTime = ExprDateTime::getDateFromString(empty($toate)? date('Y-m-d'): toDate);
		
		
        // $params = $auth->getIdentity()->id . '/'. $fromTime->setTime(0, 0, 0)->getTimestampMiliSeconds()
            // .'/'. $toTime->setTime(0, 0, 0)->getTimestampMiliSeconds();
			
		$params = $auth->getIdentity()->id . '/'.$fromDate.'/'. $toDate;
		// Get data from remote service.
        $oCollection = new RestFactory($this->getServiceLocator(), 'userCollection/print/'. $params);
        $oQuery = new QueryCriteria();
        $oCollectionResult = $oCollection->getList($oQuery);
		
		if(count($oCollectionResult->body)<1){
			throw new \Exception('No Records! ');
		}
		
		// Prepare view 
		$view = new ViewModel();
        $view->setTerminal(true);
        
		$view->setVariable('tboxUserName', $auth->getIdentity()->username);
		$view->setVariable('tboxUserNIC', $auth->getIdentity()->nic);
		$view->setVariable('fromDate', $fromDate);
		$view->setVariable('toDate', $toDate);

		// Add Data to View 
		$view->setVariable('collectionData',$oCollectionResult->body);

		// Set additional variables required for the report. 
		$view->setVariable('generatedUser',$auth->getIdentity()->username);
		
		$adultSeatCount=0;
		$childSeatCount=0;
		$totalCollectedFare=0;
		$totalCollectedServiceCharge=0;
		
		foreach ($oCollectionResult->body as $row)
		{
				if(!($row->seatFare===NULL)&&!($row->passengerType===NULL)&&!($row->paymentOption===NULL) ){
					if($row->passengerType==='Adult'){
						$adultSeatCount+=$row->noOfSeats;
					}else if($row->passengerType==='Child'){
						$childSeatCount+=$row->noOfSeats;
					}
				}
				if($row->routeName===NULL){
					$totalCollectedFare=$row->collectedFare;
					$totalCollectedServiceCharge=$row->bookingFee;
				}
		}
		$view->setVariable('adultSeatCount',$adultSeatCount);
		$view->setVariable('childSeatCount',$childSeatCount);
		$view->setVariable('totalSeatCount',$adultSeatCount+$childSeatCount);
		
		$view->setVariable('totalCollectedFare',$totalCollectedFare);
		$view->setVariable('totalCollectedServiceCharge',$totalCollectedServiceCharge);

		
        return $view;
		
		
	}
	public function tripcollectionprintAction(){
		$scheduleID = $this->params()->fromQuery('schedule');
		
		// Get Schedule Detail
		$schedule = $this->getServiceLocator()->get('Api\Manager\Schedule');
		$scheduleObj = $schedule->fetch($scheduleID);
		
		// if schedule is not available throw error.
		if(!$schedule){
			throw new \Exception('Schedule not available');
		}

		$auth = $this->getServiceLocator()->get('AuthService');

		$params = $scheduleID;
		$oCollection = new RestFactory($this->getServiceLocator(), 'userCollection/tripcollection/'. $params);
        $oQuery = new QueryCriteria();
        $oCollectionResult = $oCollection->getList($oQuery);
		
		
		$oCollectionWebList = new RestFactory($this->getServiceLocator(), 'userCollection/tripcollection/web/'. $params);
        $oQueryWebList = new QueryCriteria();
        $oCollectionResultWebList = $oCollectionWebList->getList($oQueryWebList);
		if((count($oCollectionResult)<1)&&(count($oQueryWebList)<1)){
			throw new \Exception(' No Records! ');
		}
		// Prepare view 
		$view = new ViewModel();
        $view->setTerminal(true);
		//SLTB TIcket booking list
		$view->setVariable('collectionData',$oCollectionResult->body);
		//Booking list excluding SLTB TIcketbox 
		$view->setVariable('collectionDataWebList',$oCollectionResultWebList->body);
		
		$view->setVariable('plateNumber', $scheduleObj->bus->plateNumber.' ('.$scheduleObj->bus->busType->description.' '.$scheduleObj->bus->busType->seatingCapacity.' Seater )');
		$view->setVariable('terminalOutRoute', date('Y-m-d H:i',($scheduleObj->departureTime/1000)).' / '.$scheduleObj->busRoute->name);
		$view->setVariable('terminalName', $scheduleObj->scheduleStops[0]->stop->name);
		$view->setVariable('journeyOperator',$scheduleObj->bus->supplier->name);
		$view->setVariable('driver',$scheduleObj->driver->fullName);
		$view->setVariable('plate',$scheduleObj->bus->plateNumber);
		$view->setVariable('conductor',$scheduleObj->conductor->fullName);
		$view->setVariable('generatedUser',$auth->getIdentity()->username);
		
		
		foreach ($scheduleObj->bus->supplier->accounts as $account)
		{
			if($account->isPrimary){
				$view->setVariable('accountDetail',$account);
				break;
			}
		}
		
		// seat layout page 
		$params = $scheduleID;
		$params .="/".$scheduleObj->bus->busType->id;
		$oCollection = new RestFactory($this->getServiceLocator(), 'userCollection/seatreservation/'. $params);
        $oQuery = new QueryCriteria();
        $oCollectionResult = $oCollection->getList($oQuery);
		
		if(count($oCollectionResult)<1){
			throw new \Exception(' No Records! ');
		}
		
		$agent = new RestFactory($this->getServiceLocator(), 'agent');
        //build criteria
        $criteria = new QueryCriteria();
        $criteria->setLoadAll();
        $agents = $agent->getList($criteria);

        $result = $agents->body;
        foreach ($result as $key => $ag) {
            $result[$ag->id] = $ag;
        }
		
		$seats=[];
		
		foreach ($oCollectionResult->body as $key=>$row)
		{
			
			if(!is_null($row->reference))	
			{
				$obj=["booking_status"=>"CONF",
						"x"=>$row->seatX,"y"=>$row->seatY,
						"booked"=>true,"number"=>$row->seatNumber,
						"booking_ref"=>$row->reference,
						"name"=>$row->name,
						"mobile"=>$row->phone,
						"nic"=>$row->nic,
						"from"=>$row->fromStop];
						$seats[$key]=$obj;
			}else {
				$obj=["booking_status"=>"",
						"x"=>$row->seatX,"y"=>$row->seatY,
						"booked"=>false,"number"=>$row->seatNumber,
						"booking_ref"=>"",
						"name"=>"",
						"mobile"=>"",
						"nic"=>"",
						"from"=>""];
						$seats[$key]=$obj;
			}
		}
		
		$view->seats = $seats;
        $view->bus = $scheduleObj->bus->name;
        $view->agents = $result;
        $view->schedule = $scheduleObj;
		
		return $view;

		
	}
	
	public function seatreservationprintAction(){
		$scheduleID = $this->params()->fromQuery('schedule');
		
		// Get Schedule Detail
		$schedule = $this->getServiceLocator()->get('Api\Manager\Schedule');
		$scheduleObj = $schedule->fetch($scheduleID);
		
		// if schedule is not available throw error.
		if(!$schedule){
			throw new \Exception('Schedule not available');
		}
		
		$auth = $this->getServiceLocator()->get('AuthService');

		$params = $scheduleID;
		$oCollection = new RestFactory($this->getServiceLocator(), 'userCollection/tripcollection/'. $params);
        $oQuery = new QueryCriteria();
        $oCollectionResult = $oCollection->getList($oQuery);
		
		$view = new ViewModel();
        $view->setTerminal(true);
		
		$oCollectionWebList = new RestFactory($this->getServiceLocator(), 'userCollection/tripcollection/web/'. $params);
        $oQueryWebList = new QueryCriteria();
        $oCollectionResultWebList = $oCollectionWebList->getList($oQueryWebList);
		if((count($oCollectionResult->body)<1)&&(count($oCollectionResultWebList->body)<1)){
			throw new \Exception(' No Records! ');
		}
		
		//SLTB TIcket booking list
		$view->setVariable('collectionData',$oCollectionResult->body);
		//Booking list excluding SLTB TIcketbox 
		$view->setVariable('collectionDataWebList',$oCollectionResultWebList->body);
		
		
		$params = $scheduleID;
		$params .="/".$scheduleObj->bus->busType->id;
		$oCollection = new RestFactory($this->getServiceLocator(), 'userCollection/seatreservation/'. $params);
        $oQuery = new QueryCriteria();
        $oCollectionResultData = $oCollection->getList($oQuery);
		
		if(count($oCollectionResultData)<1){
			throw new \Exception(' No Records! ');
		}
		
		
		
		
		// Prepare view 
		
		$view->setVariable('plateNumber', $scheduleObj->bus->plateNumber.' ('.$scheduleObj->bus->busType->description.' '.$scheduleObj->bus->busType->seatingCapacity.' Seater )');
		$view->setVariable('plate', $scheduleObj->bus->plateNumber);
		$view->setVariable('terminalOutRoute', $scheduleObj->busRoute->name .' / '. date('Y-m-d h:i',($scheduleObj->departureTime/1000)));
		$view->setVariable('terminalName', $scheduleObj->scheduleStops[0]->stop->name);
		$view->setVariable('journeyOperator',$scheduleObj->bus->supplier->name);
		$view->setVariable('driver',$scheduleObj->driver->fullName);
		$view->setVariable('conductor',$scheduleObj->conductor->fullName);
		$view->setVariable('generatedUser',$auth->getIdentity()->username);
		
		
		
		// Total Seats for bus
		// Get Schedule Detail
		$view->setVariable('seatingCapacity',$scheduleObj->bus->busType->seatingCapacity);
		
		$view->setVariable('reservationData',$oCollectionResultData->body);
		
		$seatNumbers='';
		foreach ($oCollectionResultData->body as $row)
		{
			if(!is_null($row->reference))	
				$seatNumbers.=$row->seatNumber.",";
		}
		$seatNumbers=explode(',',rtrim($seatNumbers,','));
		sort($seatNumbers,SORT_NUMERIC);
		$view->setVariable('seatCount',count($seatNumbers));
		$view->setVariable('bookedSeats',$seatNumbers);
		
		
		//seat layout data.
		$agent = new RestFactory($this->getServiceLocator(), 'agent');
        //build criteria
        $criteria = new QueryCriteria();
        $criteria->setLoadAll();
        $agents = $agent->getList($criteria);

        $result = $agents->body;
        foreach ($result as $key => $ag) {
            $result[$ag->id] = $ag;
        }
		
		// seat layout page 
		$params = $scheduleID;
		$params .="/".$scheduleObj->bus->busType->id;
		$oCollection = new RestFactory($this->getServiceLocator(), 'userCollection/seatreservation/'. $params);
        $oQuery = new QueryCriteria();
        $oCollectionResult = $oCollection->getList($oQuery);
		
		if(count($oCollectionResult)<1){
			throw new \Exception(' No Records! ');
		}
		
		$agent = new RestFactory($this->getServiceLocator(), 'agent');
        //build criteria
        $criteria = new QueryCriteria();
        $criteria->setLoadAll();
        $agents = $agent->getList($criteria);

        $result = $agents->body;
        foreach ($result as $key => $ag) {
            $result[$ag->id] = $ag;
        }
		
		$seats=[];
		
		foreach ($oCollectionResult->body as $key=>$row)
		{
			
			if(!is_null($row->reference))	
			{
				$obj=["booking_status"=>"CONF",
						"x"=>$row->seatX,"y"=>$row->seatY,
						"booked"=>true,"number"=>$row->seatNumber,
						"booking_ref"=>$row->reference,
						"name"=>$row->name,
						"mobile"=>$row->phone,
						"nic"=>$row->nic,
						"from"=>$row->fromStop];
						$seats[$key]=$obj;
			}else {
				$obj=["booking_status"=>"",
						"x"=>$row->seatX,"y"=>$row->seatY,
						"booked"=>false,"number"=>$row->seatNumber,
						"booking_ref"=>"",
						"name"=>"",
						"mobile"=>"",
						"nic"=>"",
						"from"=>""];
						$seats[$key]=$obj;
			}
		}
		
		
		$view->seats = $seats;
        $view->bus = $scheduleObj->bus->name;
        $view->agents = $result;
        $view->schedule = $scheduleObj;
		
		
		return $view;
	}
}