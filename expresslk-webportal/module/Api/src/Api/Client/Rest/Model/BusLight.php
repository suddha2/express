<?php

namespace Api\Client\Rest\Model;

use Api\Client\Rest\Connector;

class BusLight extends Connector {

    /**
     * @return mixed
     */
    public function getBusList()
    {
        $url = $this->getBaseUrl() . '/busLight?sortField=plateNumber';
        $response = $this->get($url);

        return $response->body;
    }
	
	public function getBusListByPlateNumber($params)
    {
        $url = $this->getBaseUrl() . '/busLight?plateNumber*='.$params;
        $response = $this->get($url);

        return $response->body;
    }

    /**
     * @return mixed
     */
    public function getBusListFull()
    {
        $url = $this->getBaseUrl() . '/busLight?sortField=plateNumber&pageRows=-1';
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
        $url = $this->getBaseUrl() . '/busLight/'. $idBus;
        $response = $this->get($url);

        return $response->body;
    }
}