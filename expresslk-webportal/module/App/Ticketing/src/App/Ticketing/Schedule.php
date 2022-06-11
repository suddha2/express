<?php


namespace App\Ticketing;

use Api\Client\Soap\Core\BusSchedule;
use Api\Operation\Request\QueryCriteria;
use App\Base\Injector;
use Api\Client\Rest\Factory as RestFactory;

class Schedule extends Injector
{
    /**
     * Get collections for each schedule based on querycriteria
     * @param $oQueryCriteria QueryCriteria
     * @return array
     */
    public function getScheduleCollections($oQueryCriteria)
    {
        $aScheduleCollections = array();
        $oRestSchedules = new RestFactory($this->getServiceManager(), 'busScheduleLight');
        $oLightSchedules = $oRestSchedules->getList($oQueryCriteria);
        $aLightSchedules = $oLightSchedules->body;

        //get collections for each schedule and populate into an array
        foreach ($aLightSchedules as $aLightSchedule) {
            $aScheduleCollections[] = array(
                'name' => $aLightSchedule->busRouteName,
                'departureTime' => $aLightSchedule->departureTime,
                'money' => $this->getConductorCollection($aLightSchedule->id)
            );
        }

        return $aScheduleCollections;
    }
    /**
     * Get conductor's collection
     * @param $scheduleId
     * @return array
     */
    public function getConductorCollection($scheduleId)
    {
        $oConductorCollection = new RestFactory($this->getServiceManager(), 'conductorCollection/'. $scheduleId);
        //build criteria
        $criteria = new QueryCriteria();
        $criteria->setLoadAll();
        $aCollection = $oConductorCollection->getList($criteria);

        $collections = $aCollection->body;
        $result = array();

        foreach ($collections as $collection){
            //temporarily skip web bookings
            $amountDue = $collection->amountDue;
            if(isset($collection->name) && strtolower($collection->name)=='web'){
                $amountDue = 0;
            }
            $result[] = array(
                'name' => $collection->name,
                'seatNumbers' => $collection->seatNumbers,
                'amountDue' => $amountDue,
            );
        }

        return $result;
    }

}