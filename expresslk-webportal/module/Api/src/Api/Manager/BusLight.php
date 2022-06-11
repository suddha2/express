<?php
namespace Api\Manager;

class BusLight extends Base {

    /**
     * Get bus list
     * @return mixed
     */
    public function getBusList()
    {
        $bus = new \Api\Client\Rest\Model\BusLight($this->getServiceManager());
        return $bus->getBusList();
    }

	
	public function getBusListByPlateNumber($params){
		$bus = new \Api\Client\Rest\Model\BusLight($this->getServiceManager());
        return $bus->getBusListByPlateNumber($params);
	}
    /**
     * Get bus list in full
     * @return mixed
     */
    public function getBusListFull()
    {
        $bus = new \Api\Client\Rest\Model\BusLight($this->getServiceManager());
        return $bus->getBusListFull();
    }


    /**
     * Get bus details
     * @param $idBus
     * @return mixed
     */
    public function getBus($idBus)
    {
        $bus = new \Api\Client\Rest\Model\BusLight($this->getServiceManager());
        return $bus->getBus($idBus);
    }
}