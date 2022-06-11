<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 10/22/14
 * Time: 9:54 AM
 */

namespace Admin\Controller;


use Api\Operation\Request\QueryCriteria;
use Application\Helper\ExprDateTime;
use Application\Model\Db\BusMobileLocation;
use Zend\Mvc\Controller\AbstractActionController;
use Zend\View\Model\JsonModel;
use Zend\View\Model\ViewModel;
use Api\Client\Rest\Factory as RestFactory;

class BusController extends AbstractActionController{

    public function locationviewAction()
    {
        $oView = new ViewModel();
        $oView->setTerminal(true);

        return $oView;
    }
    
    public function loadlocationbuslistAction()
    {
        $result = array();

        try{
            /** @var BusMobileLocation $oBusLocation */
            $oBusLocation = $this->getServiceLocator()->get('Db\BusMobileLocation');
            $busIdList = $oBusLocation->getBusList();
            $busIds = array();
            foreach ($busIdList as $item) {
                $busIds[] = $item['bus_id'];
            }

            //get buses from API
            $oBusApi = new RestFactory($this->getServiceLocator(), 'busLight');
            $query = new QueryCriteria();
            $query->setParams(array(
                'id' => $busIds
            ));
            $busResponse = $oBusApi->getList($query);

            $result['bus'] = $busResponse->body;
        }catch (\Exception $e){
            $result['error'] = $e->getMessage();
        }

        return new JsonModel($result);
    }
    
    public function getlocationsAction()
    {
        $result = array();

        try{
            $reqestData = $this->params()->fromJson();
            $busId = $reqestData['busId'];
            $fromDate = empty($reqestData['fromDate'])?
                (new ExprDateTime())->sub(new \DateInterval('PT8H'))->format('Y-m-d H:i:s') :
                ExprDateTime::getDateFromString($reqestData['fromDate'])->format('Y-m-d H:i:s');
            $toDate = empty($reqestData['toDate'])? false :
                ExprDateTime::getDateFromString($reqestData['toDate'])->format('Y-m-d H:i:s');

            /** @var BusMobileLocation $oBusLocation */
            $oBusLocation = $this->getServiceLocator()->get('Db\BusMobileLocation');
            $aBusLocations = $oBusLocation->getBusLastLocations($busId, $fromDate, $toDate);

            $lastLocation = array();
            $locations = array();
            foreach ($aBusLocations as $aBusLocation) {
                $currentLoc = array(
                    'id' => $aBusLocation['id'],
                    'latitude' => $aBusLocation['lattitude'],
                    'longitude' => $aBusLocation['longitude'],
                    'speed' => $aBusLocation['speed'],
                    'bearing' => $aBusLocation['bearing'],
                    'created_time' => $aBusLocation['created_time'],
                );
                //set first one as the last location
                if(empty($lastLocation)){
                    $lastLocation = $currentLoc;
                }

                $locations[] = $currentLoc;
            }

            $result['locations'] = $locations;
            $result['current'] = $lastLocation;
        }catch (\Exception $e){
            $result['error'] = $e->getMessage();
        }

        return new JsonModel($result);
    }

} 