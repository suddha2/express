<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 3/19/15
 * Time: 3:37 PM
 */

namespace Api\Manager;


use Api\Operation\Request\QueryCriteria;

class Bus extends Base{

    /**
     * Get bus list
     * @return mixed
     */
    public function getBusList(QueryCriteria $criteria = null)
    {
        $bus = new \Api\Client\Rest\Model\Bus($this->getServiceManager());
        return $bus->getBusList($criteria);
    }

    /**
     * Get bus list in full
     * @return mixed
     */
    public function getBusListFull()
    {
        $bus = new \Api\Client\Rest\Model\Bus($this->getServiceManager());
        return $bus->getBusListFull();
    }


    /**
     * Get bus details
     * @param $idBus
     * @return \Api\Client\Soap\Core\Bus
     */
    public function getBus($idBus)
    {
        $bus = new \Api\Client\Rest\Model\Bus($this->getServiceManager());
        $bus = $bus->getBus($idBus);
        //format and return data
        return $this->_formatBusData($bus);
    }

    /**
     * @param $bus \Api\Client\Soap\Core\Bus
     * @return \Api\Client\Soap\Core\Bus
     */
    private function _formatBusData($bus)
    {
        //routes
        foreach ($bus->busRoutes as &$route) {
            //sort route stops
            usort($route->busRoute->routeStops, function($a, $b){
                $busIndexA = intval($a->index);
                $busIndexB = intval($b->index);
                //comparison
                if ($busIndexA == $busIndexB) {
                    return 0;
                }
                return ($busIndexA < $busIndexB) ? -1 : 1;
            });
        }

        return $bus;
    }
}