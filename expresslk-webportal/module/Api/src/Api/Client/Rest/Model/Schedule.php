<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 3/25/15
 * Time: 8:42 AM
 */

namespace Api\Client\Rest\Model;


use Api\Client\Rest\Connector;

class Schedule extends Connector{

    /**
     * Create bus schudule
     * @param $schedule
     * @return mixed
     */
    public function create($schedule)
    {
        $url = $this->getBaseUrl() . '/busSchedule';
        $response = $this->put($url, $schedule);

        return $response->body;
    }

    /**
     * @param null $scheduleId
     * @param array $params
     * @return \Api\Client\Soap\Core\BusSchedule
     */
    public function fetch($scheduleId = null, $params = array())
    {
        $url = $this->getBaseUrl() . '/busSchedule' .
            (is_null($scheduleId)? '' : '/'. $scheduleId) .
            (!empty($params)? '?'. http_build_query($params) : '');
        $response = $this->get($url);

        return $response->body;
    }

    /**
     * Create bus stop
     * @param $busStop
     * @return mixed
     */
    public function createBusStop($busStop)
    {
        $url = $this->getBaseUrl() . '/busScheduleBusStop';
        $response = $this->put($url, $busStop);

        return $response->body;
    }
}