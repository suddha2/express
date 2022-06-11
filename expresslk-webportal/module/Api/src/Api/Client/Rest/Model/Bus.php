<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 3/19/15
 * Time: 3:17 PM
 */

namespace Api\Client\Rest\Model;


use Api\Client\Rest\Connector;
use Api\Operation\Request\QueryCriteria;

class Bus extends Connector{

    /**
     * @return mixed
     */
    public function getBusList(QueryCriteria $criteria = null)
    {
        $url = $this->getBaseUrl() . '/bus';
        $response = $this->get($url, $criteria);

        return $response->body;
    }

    /**
     * @return mixed
     */
    public function getBusListFull()
    {
        $url = $this->getBaseUrl() . '/bus?sortField=plateNumber&pageRows=-1';
        $response = $this->get($url);

        return $response->body;
    }

    /**
     * Get bus details by id
     * @param $idBus
     * @return mixed
     */
    public function getBus($idBus)
    {
        $url = $this->getBaseUrl() . '/bus/'. $idBus;
        $response = $this->get($url);

        return $response->body;
    }
}