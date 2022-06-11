<?php
/**
 * Created by PhpStorm.
 * User: Udantha
 * Date: 2/27/17
 * Time: 20:13
 */

namespace Data\Storage;

use Zend\Db\Adapter\Adapter;

/**
 * Class BusScheduleWaitingList
 * @package Data\Storage
 */
class BusScheduleWaitingList
{
    const TABLE_NAME = 'bus_schedule_waiting_list';

    protected $adapter;
    protected $tblGW;

    public function __construct(Adapter $adapter)
    {
        $this->adapter = $adapter;
        $this->tblGW = new \Zend\Db\TableGateway\TableGateway(self::TABLE_NAME, $this->adapter);
    }

    /**
     * @param $busScheduleId
     * @param $name
     * @param $email
     * @param $phone
     * @param $nic
     * @param $seats
     * @param $ip
     * @return int
     */
    public function create($busScheduleId, $name, $email, $phone, $nic, $seats, $ip)
    {
        return $this->tblGW->insert([
            'srv_bus_schedule_id' => $busScheduleId,
            'name' => $name,
            'email' => $email,
            'phonenumber' => $phone,
            'nic' => $nic,
            'no_of_seats' => $seats,
            'ip' => $ip
        ]);
    }
}