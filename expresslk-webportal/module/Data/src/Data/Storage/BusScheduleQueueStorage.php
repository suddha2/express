<?php


namespace Data\Storage;

use Zend\Db\Adapter\Adapter;
use Zend\Db\Sql\Insert;


class BusScheduleQueueStorage
{
    const TABLE_NAME = 'bus_schedule_queue';
	const SCHEDULE_STATUS_PENDING = 'PENDING';
    protected $adapter;
    protected $tblGW;

    public function __construct(Adapter $adapter)
    {
        $this->adapter = $adapter;
        $this->tblGW = new \Zend\Db\TableGateway\TableGateway(self::TABLE_NAME, $this->adapter);
    }

	// public function addToBusScheduleQueue($busId,$busRouteId,$startTme,$endTime,$alternateDays)
	public function addToBusScheduleQueue($busId,$busRouteId,$startTime,$travelTime)
    {

		$temp = [
			'bus_id'=> $busId,
			'bus_route_id'=>$busRouteId,
			'start_time'=>$startTime,
			'travel_time'=>$travelTime,
			//'alternate_days'=>(string)$alternateDays,
			'created_date'=>date('Y-m-d H:i:s'),
			'changed_date'=>date('Y-m-d H:i:s'),
			'status'=>self::SCHEDULE_STATUS_PENDING,
		];
		
		$test= $this->tblGW->insert($temp);		
		
		return $test;
    }

    public function update($id,$status)
    {
        return $this->tblGW->update([
            'status' => $status,
            'changed_date' => date('Y-m-d H:i:s')
			], [
				'id' => $id
			]);
    }
	
	public function getBusScheduleQueueItems(){
		// return $this->tblGW->select(array('status' => self::EMAIL_STATUS_PENDING))->limit(10)->toArray();
		$resultSet = $this->adapter->query('SELECT * FROM '.self::TABLE_NAME.' WHERE status = ? order by start_time,bus_route_id ASC limit 10', [self::SCHEDULE_STATUS_PENDING])->toArray();
		return $resultSet;
	}
}