<?php
namespace Cron\Controller;

use Zend\Mvc\Controller\AbstractActionController;

use Data\Manager\BusScheduleQueue;
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
use Api\Manager\Schedule\OperationalSchedule;
use Api\Manager\Base;
use Application\Helper\ExprDateTime;

class AddScheduleController extends AbstractActionController{
	
	private function _getConfig()
    {
		$config = $this->getServiceLocator()->get('Config');
        //get username/password
        return $config['cron']['auth'];
    }
	private function signInCRONUser()
    {
        $config = $this->_getConfig();
        //get username/password
        // $sUsername = $config['username'];
        // $sPassword = $config['password'];
		$sUsername = 'andy';
        $sPassword = 'sltb418';
		
		$authService = $this->getServiceLocator()->get('AuthService');
		$adapter = $authService->getAdapter();
		$adapter->setIdentity($sUsername)->setCredential($sPassword);
		$result = $authService->authenticate();
	
    }
	public function addScheduleAction(){
		$scheduleQueue = $this->getServiceLocator()->get('Data\Manager\BusScheduleQueue');
	   
		// Get all pending entries
		$queuedSchedules = $scheduleQueue->getBusScheduleQueueItems();
		// exit();
		
		// If no items in table, print message and exit process.
		if(count($queuedSchedules)<1){
			echo date('Y-m-d H:i:s')." \t No queued items  to process \n";
			exit();
		}
		$this->signInCRONUser();
		
		$oMBus = $this->getServiceLocator()->get('Api\Manager\Bus');
		$config = $this->getServiceLocator()->get('Config');
		
		$oStage = Base::getEntityObject('OperationalStage');
        $oStage->code = OperationalSchedule::STAGE_CODE_OFB;
		
		//echo count($queuedSchedules)." records found ! ";
		
		// $myfile = fopen("C:\wamp64\logs\AddScheduleController.txt", "w") or die("Unable to open file!");
		// fwrite($myfile,"\n ==================================".date('Y-m-d H:i:s'));
		// fwrite($myfile,"\n Processing Schedule QUEUE DATA  \n");
		
		$oSchedule = $this->getServiceLocator()->get('Api\Manager\Schedule');
		
		foreach($queuedSchedules as $scheduleItem){ 
			
			echo "\n bus_id \t bus_route_id \t start_time ";
			echo "\n ====== \t ============ \t ========== ";
			echo "\n  ".$scheduleItem['bus_id']." \t ".$scheduleItem['bus_route_id']." \t ".$scheduleItem['start_time']."\n";

			$oBus = $oMBus->getBus($scheduleItem['bus_id']);
			// fwrite($myfile,"\n oMBus->oBus  \n");
			// fwrite($myfile,print_r($oBus,true));
			
			$conductorId = $oBus->conductor->id;
			$driverId = $oBus->conductor->id;
			
			// fwrite($myfile,"\n  conductor id \t ");
			// fwrite($myfile,print_r($conductorId,true));
			// fwrite($myfile,"\n   driver id \t ");
			// fwrite($myfile,print_r($driverId,true));
			
			
			$scheduleExpireBefore = (is_numeric($oBus->secondsBeforeWebEnd) && $oBus->secondsBeforeWebEnd>0) ?
			$oBus->secondsBeforeWebEnd : $config['system']['webBookingExpireTime']; //seconds
			//expire TB bookings
			$tbExpireBefore = (is_numeric($oBus->secondsBeforeTBEnd) && $oBus->secondsBeforeTBEnd>0) ?
				$oBus->secondsBeforeTBEnd : $config['system']['TBBookingExpireTime']; //seconds
			//ticketing activates
			$ticketingActivatesBefore = (is_numeric($oBus->secondsBeforeTicketingActive) && $oBus->secondsBeforeTicketingActive>0) ?
				$oBus->secondsBeforeTicketingActive : $config['system']['ticketingActiveTime']; //seconds
			
			$bus_routes = $oBus->busRoutes;
			
			$departureTime =  \DateTime::createFromFormat('Y-m-d H:i:s',$scheduleItem['start_time']);
			
			$arrivalTime = \DateTime::createFromFormat('Y-m-d H:i:s',$scheduleItem['start_time']);
			
			$arrivalTime->add(\DateInterval::createFromDateString($scheduleItem['travel_time'].' seconds'));
			
			$termialInTime = \DateTime::createFromFormat('Y-m-d H:i:s',$scheduleItem['start_time']);
			
			
			foreach($bus_routes as $route){
				if ($route->busRoute->id==$scheduleItem['bus_route_id']){
					try {
						$bus = Base::getEntityObject('Bus');
						$bus->id = $scheduleItem['bus_id'];
						
						$busRoute = Base::getEntityObject('BusRoute');
						$busRoute->id = $scheduleItem['bus_route_id'];
						
						$conductor = Base::getEntityObject('Conductor');
						$conductor->id = $conductorId;
						
						$driver = Base::getEntityObject('Driver');
						$driver->id = $driverId;
						
						$loadFactor = $route->loadFactor;
						
						$seatingProfile = Base::getEntityObject('SeatingProfile');
						$seatingProfile->id = $oBus->seatingProfile->id;
						
						$oBusSchedule = Base::getEntityObject('BusSchedule');
						$oBusSchedule->active           = true;
						$oBusSchedule->arrivalTime      = $arrivalTime->getTimestamp()*1000;
						$oBusSchedule->bus              =  $bus;
						$oBusSchedule->busRoute         =  $busRoute;
						$oBusSchedule->conductor        = $conductor;
						$oBusSchedule->departureTime    = $departureTime->getTimestamp()*1000;
						$oBusSchedule->driver           = $driver;
						$oBusSchedule->id               = null;
						$oBusSchedule->terminalInTime   = $termialInTime->getTimestamp()*1000;
						$oBusSchedule->seatingProfile   = $seatingProfile;
						$oBusSchedule->bookingAllowed   = true;
						$oBusSchedule->loadFactor       = $loadFactor;
						$oBusSchedule->stage            = $oStage;
						$oBusSchedule->webBookingEndTime= $this->_getSecondsSubstracted($termialInTime, $scheduleExpireBefore);
						$oBusSchedule->tbBookingEndTime = $this->_getSecondsSubstracted($departureTime, $tbExpireBefore);
						$oBusSchedule->ticketingActiveTime = $this->_getSecondsSubstracted($termialInTime, $ticketingActivatesBefore);
						$newBusSchedule = $oSchedule->create($oBusSchedule);
						
						if($newBusSchedule->id>0){
							$route_stops = $route->busRoute->routeStops;
							foreach($route_stops as $stop){
								
								echo "\nSTOP INDEX : \t".$stop->index."\n";
								
								// Convert waiting time to seconds.
								$waiting_time = explode(":",$stop->waitingTime);
								$waiting_h=$waiting_time[0]*60;
								$waiting_m=$waiting_time[1]*60;
								$waiting_s=$waiting_time[2]*60;
								
								$last_stop_departure = $departureTime;
								 
								$scheduleStop =  Base::getEntityObject('BusScheduleBusStop');
								
								/** @var BusStop $busScheduleBusStop */
								$busScheduleBusStop = Base::getEntityObject('BusStop');
								$busScheduleBusStop->id = $stop->id;
								$scheduleStop->stop = $busScheduleBusStop;
								$scheduleStop->idx = $stop->index;
								
								//print_r($last_stop_departure);
								
								if($stop->index==1){
									$last_stop_departure->sub(\DateInterval::createFromDateString($waiting_h+$waiting_m+$waiting_s.' seconds'));
									 
									$scheduleStop->arrivalTime = clone $last_stop_departure;
									//$scheduleStop->departureTime = $departureTime;
									
									$oBusSchedule->terminalInTime = clone $last_stop_departure;
									$oBusSchedule->webBookingEndTime= $this->_getSecondsSubstracted($last_stop_departure, $scheduleExpireBefore);
									$oBusSchedule->ticketingActiveTime = $this->_getSecondsSubstracted($last_stop_departure, $ticketingActivatesBefore);
									
									$scheduleStop->departureTime =  clone $scheduleStop->arrivalTime;
									$scheduleStop->departureTime->add(\DateInterval::createFromDateString($waiting_h+$waiting_m+$waiting_s.' seconds'));
									$departureTime = $scheduleStop->departureTime;
								}
								else{
									// Convert travel time to seconds.
									$travel_time =  explode(":",$stop->travelTime);
									$travel_h=$travel_time[0]*60;
									$travel_m=$travel_time[1]*60;
									$travel_s=$travel_time[2]*60;
									
									$last_stop_departure->add(\DateInterval::createFromDateString($travel_h+$travel_m+$travel_s.' seconds')); 
									$scheduleStop->arrivalTime = $last_stop_departure;
									$scheduleStop->departureTime =  $scheduleStop->arrivalTime;
									$scheduleStop->departureTime->add(\DateInterval::createFromDateString($waiting_h+$waiting_m+$waiting_s.' seconds'));
								}
								echo "\n".$route->busRoute->id."\t".$stop->id."\t".$stop->index."\t".$scheduleStop->arrivalTime->format('Y-m-d H:i:s')."\t".$scheduleStop->departureTime->format('Y-m-d H:i:s');
								// convert times to milliseconds
								$scheduleStop->arrivalTime=$scheduleStop->arrivalTime->getTimestamp()*1000;
								$scheduleStop->departureTime=$scheduleStop->departureTime->getTimestamp()*1000;
								$scheduleStop->scheduleId=$newBusSchedule->id;
								try{
									$oSchedule->createBusStop($scheduleStop);
								}catch(\Exception $ex){
									echo "\n".$scheduleStop->scheduleId."\t".$scheduleStop->stop->id."\t".$scheduleStop->idx."\t".date('Y-m-d H:i:s',strtotime($scheduleStop->arrivalTime))."\t".date('Y-m-d H:i:s',strtotime($scheduleStop->departureTime));
									echo "\n ".$ex->getMessage(); 
									$scheduleQueue->updateBusScheduleQueue($scheduleItem['id'], 'ERROR');
								}
							}
							
							$scheduleQueue->updateBusScheduleQueue($scheduleItem['id'], 'CREATED');
							echo "\n SAVED  : \t".$scheduleItem['bus_id']." \t ".$scheduleItem['bus_route_id']." \t ".$scheduleItem['start_time'];
						}else {
							throw new \Exception("Schedule ID is less than 1");
							$scheduleQueue->updateBusScheduleQueue($scheduleItem['id'], 'ERROR');
						}
					}catch(\Exception $ex){
						//print_r($ex);
						echo "\n ".$ex->getMessage();
						echo "\n FAILED ADDING : \t".$scheduleItem['bus_id']." \t ".$scheduleItem['bus_route_id']." \t ".$scheduleItem['start_time'];
						$scheduleQueue->updateBusScheduleQueue($scheduleItem['id'], 'ERROR');
					}
				}
			}
		}		
		// fclose($myfile);
		
	}

	private function _getSecondsSubstracted($terminalInTS, $stopBeforeSeconds)
    {
        //convert milisecond timestamp to date
        //$now = ExprDateTime::getDateFromServices($terminalInTS);
		$now = clone $terminalInTS;
        $now->sub(new \DateInterval("PT{$stopBeforeSeconds}S"));
        return $now->getTimestamp()*1000;
    }
}