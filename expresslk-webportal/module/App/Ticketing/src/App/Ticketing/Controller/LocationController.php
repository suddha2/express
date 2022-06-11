<?php


namespace App\Ticketing\Controller;


use App\Base\Controller\AbstractController;
use Application\Model\Db\BusMobileLocation;
use Application\Util\Log;

class LocationController extends AbstractController
{

    /**
     * @param $username
     * @param $token
     * @throws \Exception
     */
    private function isAuthorized($username, $token)
    {
        /** @var \App\Base\Auth $auth */
        $auth = $this->getServiceLocator()->get('App\Auth');
        if(!$auth->isAuthorised($username, $token)){
            throw new \Exception('Authorization failed. Please login again');
        }
    }

    public function receiveAction()
    {
        try{
            $params = $this->params()->fromJson();
            $username = $params['username'];
            $token = $params['token'];
            $busId = $params['bs'];
            $lat = $params['lt'];
            $long = $params['ln'];
            $bearing = $params['b'];
            $speed = $params['s'];

            //authenticate user
            $this->isAuthorized($username, $token);

            /**
             * @todo move this to either proper ORM or Service call
             */
            /** @var BusMobileLocation $oBusLocation */
            $oBusLocation = $this->getServiceLocator()->get('Db\BusMobileLocation');
            $oBusLocation->addLocation(array(
                'bus' => $busId,
                'lat' => $lat,
                'lon' => $long,
                'speed' => $bearing,
                'bearing' => $speed,
            ));
            
            //set response
            $this->addSuccess(array(
                'l'=> 1,
            ));
        } catch (\Exception $e) {
            (new Log())->alert(var_export($e->getMessage(), true));
            //do not send errors on ping
            //$this->addError($e);
        }

        return $this->getJsonResponse();
    }
}