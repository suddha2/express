<?php
/**
 * Created by PhpStorm.
 * User: Udantha
 * Date: 2/27/17
 * Time: 20:25
 */

namespace Data\Manager;


use Data\InjectorBase;
use Data\Storage\BusScheduleWaitingList;

class WaitingList extends InjectorBase
{
    /** @var BusScheduleWaitingList  */
    private $busScheduleWaitingList = null;

    /**
     * Create waiting list
     * @param $busScheduleId
     * @param $name
     * @param $email
     * @param $phone
     * @param $nic
     * @param $seats
     * @param $ip
     * @return int
     */
    public function saveWaitingList($busScheduleId, $name, $email, $phone, $nic, $seats, $ip)
    {
        return $this->getBusScheduleWaitingList()->create($busScheduleId, $name, $email, $phone, $nic, $seats, $ip);
    }

    /**
     * @return BusScheduleWaitingList|null
     */
    private function getBusScheduleWaitingList()
    {
        if(is_null($this->busScheduleWaitingList)){
            $this->busScheduleWaitingList = new BusScheduleWaitingList($this->getServiceManager()->get('Zend\Db\Adapter\Adapter'));
        }
        return $this->busScheduleWaitingList;
    }
}