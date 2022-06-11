<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 3/21/15
 * Time: 11:47 AM
 */

namespace Admin\Controller;


use Api\Client\Rest\Factory as RestFactory;
use Api\Client\Soap\Core\Bus;
use Api\Client\Soap\Core\BusRoute;
use Api\Client\Soap\Core\BusSchedule;
use Api\Client\Soap\Core\BusScheduleBusStop;
use Api\Client\Soap\Core\BusStop;
use Api\Client\Soap\Core\Conductor;
use Api\Client\Soap\Core\Division;
use Api\Client\Soap\Core\Driver;
use Api\Client\Soap\Core\OperationalStage;
use Api\Client\Soap\Core\SeatingProfile;
use Api\Manager\Base;
use Api\Manager\Booking;
use Api\Manager\Schedule\OperationalSchedule;
use Api\Operation\Request\QueryCriteria;
use Application\Helper\ExprDateTime;
use Zend\Crypt\Hash;
use Zend\I18n\Validator\DateTime;
use Zend\Mvc\Controller\AbstractActionController;
use Zend\View\Model\JsonModel;
use Zend\View\Model\ViewModel;

class BusServiceController extends AbstractActionController{

    public function busAction()
    {
        $response = array();
        $json   = $this->params()->fromJson();
        $idBus = $json['idBus'];
        try {
            /**
             * @var $oBus \Api\Manager\Bus get bus object
             */
            $oBus = $this->getServiceLocator()->get('Api\Manager\Bus');
            $bus = $oBus->getBus($idBus);

            $seatingProfile = new RestFactory($this->getServiceLocator(), 'seatingProfile');
            //build criteria
            $criteria = new QueryCriteria();
            $criteria->addParam('busTypeId', $bus->busType->id);
            $seatingProfiles = $seatingProfile->getList($criteria);

            $response['seatingProfiles'] = $seatingProfiles->body;

            $response['bus'] = $bus;
        } catch (\Exception $e) {
            $response['error'] = $e->getMessage();
        }

        return new JsonModel($response);
    }

    /**
     * Set stage for a schedule
     * @return JsonModel
     */
    public function setschedulestageAction()
    {
        $response = array();
        $json   = $this->params()->fromJson();
        $scheduleId = $json['scheduleId'];
        $stageCode = $json['stageCode'];

        try {
            /** @var OperationalSchedule $oOperationalSchedule */
            $oOperationalSchedule = $this->getServiceLocator()->get('Api\Manager\OperationalSchedule');
            //pass to do operations for the schedule
            $oOperationalSchedule->updateStage($scheduleId, $stageCode);
            $response['success'] = true;
        } catch (\Exception $e) {
            $response['error'] = $e->getMessage();
        }

        return new JsonModel($response);
    }

    public function mockschedulestageAction()
    {
        $oView = new ViewModel();
        $scheduleId = $this->params()->fromQuery('scheduleId');
        $stageCode = $this->params()->fromQuery('stageCode');
        $currentSchedule = null;

        try {
            /** @var \Api\Manager\Schedule $scheduleManager */
            $scheduleManager = $this->getServiceLocator()->get('Api\Manager\Schedule');
            //get schedule
            /** @var BusSchedule $currentSchedule */
            $currentSchedule = $scheduleManager->fetch($scheduleId);
        } catch (\Exception $e) {
            $oView->error = $e->getMessage();
        }

        //if next stage is close bookings
        if(OperationalSchedule::STAGE_CODE_CFB==$stageCode){
            //get bookings from API
            $oBookingRest = new RestFactory($this->getServiceLocator(), 'booking/bySchedule/'. $scheduleId);
            $rBookings = $oBookingRest->getList(new QueryCriteria());
            /** @var \Api\Client\Soap\Core\Booking[] $aBookings */
            $aBookings = $rBookings->body;
            $bookingCount = 0;
            foreach ($aBookings as $aBooking) {
                //only confirmed bookings with mobile number
                if($aBooking->status->code == Booking::STATUS_CODE_CONFIRM
                    && !empty($aBooking->client->mobileTelephone)){
                    $bookingCount++;
                }
            }
            $oView->bookingCount = $bookingCount;

            //get bus contact
            $oView->busContact = !empty($currentSchedule->bus->contact)? $currentSchedule->bus->contact : $currentSchedule->conductor->person->mobileTelephone;
            //get conductor sms sent numbers
            $aAdminContacts = array();
            if(!empty($currentSchedule->bus->adminContact)){
                $aAdminContacts[] = $currentSchedule->bus->adminContact;
            }
            $iBusContact = empty($currentSchedule->bus->contact)? $currentSchedule->bus->conductor->person->mobileTelephone : $currentSchedule->bus->contact;
            if(!empty($iBusContact)){
                $aAdminContacts[] = $iBusContact;
            }
            $oView->adminContacts = $aAdminContacts;
        }

        $oView->schedule = $currentSchedule;
        $oView->requestedStage = $stageCode;

        $oView->setTerminal(true);
        return $oView;
    }
	
	private function _new_createscheduleAction(){
		$response = array();
        $json   = $this->params()->fromJson();
		
		if(empty($json)){
			throw new \Exception ("Incomplete request!");
		}
		
		
        $busId = $json['busId'];
        $routes = $json['routes'];
        // $driverId = $json['driver'];
        // $conductorId = $json['conductor'];
		
		
		if(empty($routes)){
			throw new \Exception ("No route!");
		}
        $recurFrom = $json['from'];
        $recurTo = $json['to'];
        $isRecurring = $json['recurring'];
		$alternativeDays = $json['alternateDays'];
		
		
		// $myfile = fopen("C:\wamp64\logs\BusServiceController.txt", "w") or die("Unable to open file!");
		// fwrite($myfile,"\n ==================================".date('Y-m-d H:i:s'));
		// fwrite($myfile,"\n Processing Schedule Form submit DATA  \n");
		
		
		
		$busScheduleQueue = $this->getServiceLocator()->get('Data\Manager\BusScheduleQueue');
		try{
			foreach ($routes as $routeId => $route) {
				if(empty($route['busStops'])){
					throw new \Exception ("No Stops Defined for Route  -".$routeId."/".$route['name']);
				}
				
				$fromDateUTC = \DateTime::createFromFormat('Y-m-d\TH:i:s.u\Z', $route['startTime'],new \DateTimeZone('UTC'));
				$fromDate = $fromDateUTC;
				$fromDate->setTimezone(new \DateTimeZone(date_default_timezone_get()));
				
				
				$endTimeUTC  = \DateTime::createFromFormat('Y-m-d\TH:i:s.u\Z', $route['endTime'],new \DateTimeZone('UTC'));
				$endTime= $endTimeUTC;
				$endTime->setTimezone(new \DateTimeZone(date_default_timezone_get()));
				
				$travelTime = date_diff($endTime, $fromDate);
				
				$travelTimeinSeconds = ($travelTime->h*3600)+($travelTime->m*60)+($travelTime->s);
				
				if($travelTimeinSeconds<1800){
					throw new \Exception ("Travel time is low   -".$routeId."/".$route['name']);
				}
				
				if(!$isRecurring){
					$insertID = $busScheduleQueue->addToBusScheduleQueue($busId,$routeId,$fromDate->format("Y-m-d H:i"),$travelTimeinSeconds);
					if($insertID<1){
							throw new \Exception("Insert failed!!");
					}
				}else {
					// fwrite($myfile,"\n".print_r($toDate,true));
					$toDate = \DateTime::createFromFormat('Y-m-d\TH:i:s.u\Z', $recurTo);
					$toDate->setTimezone(new \DateTimeZone(date_default_timezone_get()));
					
					// fwrite($myfile,"\n".print_r($toDate,true));
					
					// Add 1 Day to include last day in the loop 
					//$toDate->add(\DateInterval::createFromDateString('1 day'));
					
					$interval = \DateInterval::createFromDateString('1 day');
					
					if($alternativeDays){
						$interval = \DateInterval::createFromDateString('2 day');
					}
					
					$period = new \DatePeriod($fromDate, $interval, $toDate);
					
					foreach ($period as $dt) {
						$insertID = $busScheduleQueue->addToBusScheduleQueue($busId,$routeId,
									$dt->format("Y-m-d H:i"),$travelTimeinSeconds);
						if($insertID<1){
							throw new \Exception("Insert failed!!");
						}
					}
				}
			}
			$response['success'] = true;
        }catch (\Exception $e) {
            $response['error'] = $e->getMessage();
        }
		// fclose($myfile);
        return new JsonModel($response);
		
	}

    public function createscheduleAction()
    {
        $response = array();
        $json   = $this->params()->fromJson();
        $busId = $json['busId'];
        $routes = $json['routes'];
        $driverId = $json['driver'];
        $conductorId = $json['conductor'];

        $recurFrom = $json['from'];
        $recurTo = $json['to'];
        $isChecked = $json['recurring'];

        //get bus obejct

        /**
         * @var $oMBus \Api\Manager\Bus get bus object
         */
        $oMBus = $this->getServiceLocator()->get('Api\Manager\Bus');
        $oBus = $oMBus->getBus($busId);

        $config = $this->getServiceLocator()->get('Config');
        //expire web bookings
        $scheduleExpireBefore = (is_numeric($oBus->secondsBeforeWebEnd) && $oBus->secondsBeforeWebEnd>0) ?
            $oBus->secondsBeforeWebEnd : $config['system']['webBookingExpireTime']; //seconds
        //expire TB bookings
        $tbExpireBefore = (is_numeric($oBus->secondsBeforeTBEnd) && $oBus->secondsBeforeTBEnd>0) ?
            $oBus->secondsBeforeTBEnd : $config['system']['TBBookingExpireTime']; //seconds
        //ticketing activates
        $ticketingActivatesBefore = (is_numeric($oBus->secondsBeforeTicketingActive) && $oBus->secondsBeforeTicketingActive>0) ?
            $oBus->secondsBeforeTicketingActive : $config['system']['ticketingActiveTime']; //seconds

        //set default operational stage
        /** @var OperationalStage $oStage */
        $oStage = Base::getEntityObject('OperationalStage');
        $oStage->code = OperationalSchedule::STAGE_CODE_OFB;

        //set seating profile
        $seatingProfile = null;
        if(is_numeric($json['seatingProfile'])){
            /** @var SeatingProfile $seatingProfile */
            $seatingProfile = Base::getEntityObject('SeatingProfile');
            $seatingProfile->id = $json['seatingProfile'];
        }

        /* @var $oSchedule \Api\Manager\Schedule */
        $oSchedule = $this->getServiceLocator()->get('Api\Manager\Schedule');

        try {
            if ($isChecked) {
                //loop dates
                $begin = $this->_getDateObjectFromString($recurFrom);
                $end = $this->_getDateObjectFromString($recurTo);

                $interval = \DateInterval::createFromDateString('1 day');
                $period = new \DatePeriod($begin, $interval, $end);
                //while(strtotime($begin) <= strtotime($end)){
                $lastIterationEnd = null;
                foreach ($period as $dt) {

                    foreach ($routes as $routeId => $route) {
                        //check if this route is disabled
                        if($route['disabled']===true){
                            //skip this route
                            continue;
                        }

                        $startTime  = $this->_getDateObjectFromString($route['startTime']);
                        $endTime    = $this->_getDateObjectFromString($route['endTime']);
                        //set time and remove seconds
                        $startTime->setTime($startTime->format("H"), $startTime->format("i"), 0);
                        $endTime->setTime($endTime->format("H"), $endTime->format("i"), 0);

                        //change date to period date field
                        $startTime->setDate($dt->format("Y"),$dt->format("m"),$dt->format("d"));
                        $endTime->setDate($dt->format("Y"),$dt->format("m"),$dt->format("d"));

                        //get load factor
                        $loadFactor = empty($route['loadFactor'])? 0 : $route['loadFactor'];

                        $id = null;
                        $active = true;
                        $arrivalTime = $endTime->getTimestamp() * 1000; //convert into miliseconds
                        /** @var Bus $bus */
                        $bus = Base::getEntityObject('Bus');
                        $bus->id = $busId;
                        /** @var BusRoute $busRoute */
                        $busRoute = Base::getEntityObject('BusRoute');
                        $busRoute->id = $routeId;
                        /** @var Conductor $conductor */
                        $conductor = Base::getEntityObject('Conductor');
                        $conductor->id = $conductorId;
                        //convert into miliseconds
                        $departureTime = $startTime->getTimestamp() * 1000;
                        /** @var Driver $driver */
                        $driver = Base::getEntityObject('Driver');
                        $driver->id = $driverId;
                        $allowedDivisions = null;
                        //set terminal intime
                        $termialInTime = null;
                        $busStopsArray = $this->_sortByKey($route['busStops'], 'index');
                        $busStop = current($busStopsArray);
                        $inTime = $this->_getDateObjectFromString($busStop['startTime']);
                        //change date
                        $inTime->setDate($startTime->format('Y'), $startTime->format('m'), $startTime->format('d'));
                        $inTime->setTime($inTime->format("H"), $inTime->format("i"), 0);
                        $termialInTime = $inTime->getTimestamp() * 1000; //convert into miliseconds

                        /** @var BusSchedule $oBusSchedule */
                        $oBusSchedule = Base::getEntityObject('BusSchedule');
                        $oBusSchedule->active           = $active;
                        //$oBusSchedule->allowedDivisions = $allowedDivisions;
                        $oBusSchedule->arrivalTime      = $arrivalTime;
                        $oBusSchedule->bus              = $bus;
                        $oBusSchedule->busRoute         = $busRoute;
                        $oBusSchedule->conductor        = $conductor;
                        $oBusSchedule->departureTime    = $departureTime;
                        $oBusSchedule->driver           = $driver;
                        $oBusSchedule->id               = $id;
                        $oBusSchedule->terminalInTime   = $termialInTime;
                        $oBusSchedule->seatingProfile   = $seatingProfile;
                        $oBusSchedule->bookingAllowed   = true;
                        $oBusSchedule->loadFactor       = $loadFactor;
                        $oBusSchedule->stage            = $oStage;
                        $oBusSchedule->webBookingEndTime= $this->_getSecondsSubstracted($termialInTime, $scheduleExpireBefore);
                        $oBusSchedule->tbBookingEndTime = $this->_getSecondsSubstracted($departureTime, $tbExpireBefore);
                        $oBusSchedule->ticketingActiveTime = $this->_getSecondsSubstracted($termialInTime, $ticketingActivatesBefore);
                        //create schedule
                        $newBusSchedule = $oSchedule->create($oBusSchedule);

                        //create stops
                        foreach ($route['busStops'] as $busStopKey => $busStop) {
                            $busStopId = $busStop['stopId'];
                            $startDate = $this->_getDateObjectFromString($busStop['startTime']);
                            //change date to match current loop date
                            $startDate->setDate($startTime->format('Y'), $startTime->format('m'), $startTime->format('d'));
                            $startDate->setTime($startDate->format("H"), $startDate->format("i"), 0);

                            $endDate = $this->_getDateObjectFromString($busStop['endTime']);
                            $endDate->setDate($startTime->format('Y'), $startTime->format('m'), $startTime->format('d'));
                            $endDate->setTime($endDate->format("H"), $endDate->format("i"), 0);
                            $scheduleId = $newBusSchedule->id;
                            $index = $busStop['index'];
                            $arrivalTime = $startDate->getTimestamp() * 1000; //convert into miliseconds
                            $departureTime = $endDate->getTimestamp() * 1000; //convert into miliseconds;

                            /** @var BusScheduleBusStop $scheduleStop */
                            $scheduleStop = Base::getEntityObject('BusScheduleBusStop');
                            $scheduleStop->arrivalTime = $arrivalTime;
                            /** @var BusStop $busScheduleBusStop */
                            $busScheduleBusStop = Base::getEntityObject('BusStop');
                            $busScheduleBusStop->id = $busStopId;
                            $scheduleStop->stop = $busScheduleBusStop;
                            $scheduleStop->departureTime = $departureTime;
                            $scheduleStop->idx = $index;
                            $scheduleStop->scheduleId = $scheduleId;

                            $oSchedule->createBusStop($scheduleStop);
                        }
                    }
                }
                $response['success'] = true;
            }
            else {
                foreach ($routes as $routeId => $route) {
                    //check if this route is disabled
                    if($route['disabled']===true){
                        //skip this route
                        continue;
                    }

                    $startDate = $this->_getDateObjectFromString($route['startTime']);
                    $startDate->setTime($startDate->format("H"), $startDate->format("i"), 0);
                    $endDate = $this->_getDateObjectFromString($route['endTime']);
                    $endDate->setTime($endDate->format("H"), $endDate->format("i"), 0);

                    //get load factor
                    $loadFactor = empty($route['loadFactor'])? 0 : $route['loadFactor'];

                    $id = null;
                    $active = true;
                    $arrivalTime = $endDate->getTimestamp() * 1000; //convert into miliseconds
                    /** @var Bus $bus */
                    $bus = Base::getEntityObject('Bus');
                    $bus->id = $busId;
                    /** @var BusRoute $busRoute */
                    $busRoute = Base::getEntityObject('BusRoute');
                    $busRoute->id = $routeId;
                    /** @var Conductor $conductor */
                    $conductor = Base::getEntityObject('Conductor');
                    $conductor->id = $conductorId;
                    $departureTime = $startDate->getTimestamp() * 1000; //convert into miliseconds;
                    /** @var Driver $driver */
                    $driver = Base::getEntityObject('Driver');
                    $driver->id = $driverId;
                    $allowedDivisions = null;
                    //set terminal intime
                    $termialInTime = null;
                    $busStopsArray = $this->_sortByKey($route['busStops'], 'index');
                    $busStop = current($busStopsArray);
                    $inTime = $this->_getDateObjectFromString($busStop['startTime']);
                    //change date
                    $inTime->setTime($inTime->format("H"), $inTime->format("i"), 0);
                    $termialInTime = $inTime->getTimestamp() * 1000; //convert into miliseconds

                    /** @var BusSchedule $oBusSchedule */
                    $oBusSchedule = Base::getEntityObject('BusSchedule');
                    $oBusSchedule->active           = $active;
                    //$oBusSchedule->allowedDivisions = $allowedDivisions;
                    $oBusSchedule->arrivalTime      = $arrivalTime;
                    $oBusSchedule->bus              = $bus;
                    $oBusSchedule->busRoute         = $busRoute;
                    $oBusSchedule->conductor        = $conductor;
                    $oBusSchedule->departureTime    = $departureTime;
                    $oBusSchedule->driver           = $driver;
                    $oBusSchedule->id               = $id;
                    $oBusSchedule->terminalInTime   = $termialInTime;
                    $oBusSchedule->seatingProfile   = $seatingProfile;
                    $oBusSchedule->bookingAllowed   = true;
                    $oBusSchedule->loadFactor       = $loadFactor;
                    $oBusSchedule->stage            = $oStage;
                    $oBusSchedule->webBookingEndTime= $this->_getSecondsSubstracted($termialInTime, $scheduleExpireBefore);
                    $oBusSchedule->tbBookingEndTime = $this->_getSecondsSubstracted($termialInTime, $tbExpireBefore);
                    $oBusSchedule->ticketingActiveTime = $this->_getSecondsSubstracted($termialInTime, $ticketingActivatesBefore);
                    //create schedule
                    $newBusSchedule = $oSchedule->create($oBusSchedule);

                    //create stops
                    foreach ($route['busStops'] as $busStopKey => $busStop) {
                        $busStopId = $busStop['stopId'];
                        $startDate = $this->_getDateObjectFromString($busStop['startTime']);
                        $startDate->setTime($startDate->format("H"), $startDate->format("i"), 0);
                        $endDate = $this->_getDateObjectFromString($busStop['endTime']);
                        $endDate->setTime($endDate->format("H"), $endDate->format("i"), 0);
                        $scheduleId = $newBusSchedule->id;
                        $index = $busStop['index'];
                        $arrivalTime = $startDate->getTimestamp() * 1000; //convert into miliseconds
                        $departureTime = $endDate->getTimestamp() * 1000; //convert into miliseconds;

                        /** @var BusScheduleBusStop $scheduleStop */
                        $scheduleStop = Base::getEntityObject('BusScheduleBusStop');
                        $scheduleStop->arrivalTime = $arrivalTime;
                        /** @var BusStop $busScheduleBusStop */
                        $busScheduleBusStop = Base::getEntityObject('BusStop');
                        $busScheduleBusStop->id = $busStopId;
                        $scheduleStop->stop = $busScheduleBusStop;
                        $scheduleStop->departureTime = $departureTime;
                        $scheduleStop->idx = $index;
                        $scheduleStop->scheduleId = $scheduleId;

                        $oSchedule->createBusStop($scheduleStop);
                    }
                }
                $response['success'] = true;
            }
        }catch (\Exception $e) {
            $response['error'] = $e->getMessage();
        }

        return new JsonModel($response);
    }

    private function _getDateObjectFromString($dateString)
    {
        $dt = new \DateTime();
        $dt->setTimestamp(strtotime($dateString));
        return $dt;
    }

    private function _sortByKey($array, $key)
    {
        $copiedArray = $array;
        usort($copiedArray, function ($a, $b) use ($key) {
            $sort = $a[$key] - $b[$key];
            return ($sort==0? 0 : ($sort>0)? 1 : -1);
        });

        return $copiedArray;
    }

    /**
     * @param $terminalInTS
     * @param $stopBeforeSeconds
     * @return int
     * @throws \Exception
     */
    private function _getSecondsSubstracted($terminalInTS, $stopBeforeSeconds)
    {
        //convert milisecond timestamp to date
        $now = ExprDateTime::getDateFromServices($terminalInTS);
        $now->sub(new \DateInterval("PT{$stopBeforeSeconds}S"));
        return $now->getTimestampMiliSeconds();
    }

    public function schedulelistAction()
    {
        $result = array();
        try {
            /* @var $oSchedule \Api\Manager\Schedule */
            $oSchedule = $this->getServiceLocator()->get('Api\Manager\Schedule');
            $schedules = $oSchedule->fetch();
            $result = array(
                'result'=> $schedules,
                'total'=> count($schedules),
            );
        } catch (\Exception $e) {
            $result['error'] = $e->getMessage();
        }
        return new JsonModel($result);
    }
}