<?php


namespace Data\Manager;


use Data\InjectorBase;
use Data\Storage\BusScheduleQueueStorage;

class BusScheduleQueue extends InjectorBase
{
    private $busScheduleQueueStorage = null;

    
    public function addToBusScheduleQueue($busId,$busRouteId,$startTme,$travelTime)
    {
		return $this->getBusScheduleQueueStorage()->addToBusScheduleQueue($busId,$busRouteId,$startTme,$travelTime);
    }

    public function updateBusScheduleQueue($id,$status){
        return $this->getBusScheduleQueueStorage()->update($id, $status);
    }

    private function getBusScheduleQueueStorage()
    {
        if(is_null($this->busScheduleQueueStorage)){
            $this->busScheduleQueueStorage = new BusScheduleQueueStorage($this->getServiceManager()->get('Zend\Db\Adapter\Adapter'));
        }
        return $this->busScheduleQueueStorage;
    }
	
	public function getBusScheduleQueueItems(){
		return $this->getBusScheduleQueueStorage()->getBusScheduleQueueItems();
	}
}