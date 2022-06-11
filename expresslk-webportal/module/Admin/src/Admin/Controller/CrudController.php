<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 6/26/15
 * Time: 7:58 PM
 */

namespace Admin\Controller;


use Api\Client\Rest\Factory as RestFactory;
use Api\Client\Rest\Model\Booking;
use Api\Client\Rest\Model\AllowedDivisions;
use Api\Client\Rest\Model\Conductor;
use Api\Client\Rest\Model\Driver;
use Api\Client\Rest\Model\SupplierContactPerson;
use Api\Manager\Base;
use Api\Operation\CrudInterface;
use Api\Operation\Request\QueryCriteria;
use Zend\File\Transfer\Adapter\Http;
use Zend\Json\Json;
use Zend\Mvc\Controller\AbstractRestfulController;
use Zend\Mvc\MvcEvent;
use Zend\Session\Container;
use Zend\Session\SessionManager;
use Zend\Validator\File\Extension;
use Zend\Validator\File\Size;
use Zend\View\Model\JsonModel;
use Api\Client\Rest\Model\Api\Client\Rest\Model;
use Zend\ServiceManager\ServiceManager;

class CrudController extends AbstractRestfulController{

    /**
     * Define configurations for cruds
     */
    private function _getConfig(ServiceManager $sm)
    {
        return array(
            'bus' => array(
                /**
                 * Pass CrudInterface implemented class object or a string to create a RestFactory
                 */
                'gatewayObject' => 'bus',
                /**
                 * Model class maps to the backend
                 */
                'modelClass' => 'Bus',
            ),
            'busImage' => array(
                'gatewayObject' => 'busImage',
                'modelClass' => 'BusImage',
            ),
            'busBusRoute' => array(
                'gatewayObject' => 'busBusRoute',
                'modelClass' => 'BusBusRoute',
            ),
            'busLight' => array(
                'gatewayObject' => 'busLight',
                'modelClass' => 'BusLight',
            ),
            'busStop' => array(
                'gatewayObject' => 'busStop',
                'modelClass' => 'BusStop',
            ),
            'booking' => array(
                'gatewayObject' => new Booking($sm),
                'modelClass' => 'Booking',
            ),
            'bookingItem' => array(
                'gatewayObject' => 'bookingItem',
                'modelClass' => 'BookingItem',
            ),
            'bookingItemCharge' => array(
                'gatewayObject' => 'bookingItemCharge',
                'modelClass' => 'BookingItemCharge',
            ),
            'bookingItemMarkup' => array(
                'gatewayObject' => 'bookingItemMarkup',
                'modelClass' => 'BookingItemMarkup',
            ),
            'bookingItemDiscount' => array(
                'gatewayObject' => 'bookingItemDiscount',
                'modelClass' => 'BookingItemDiscount',
            ),
            'bookingItemTax' => array(
                'gatewayObject' => 'bookingItemTax',
                'modelClass' => 'BookingItemTax',
            ),
            'bookingItemPassenger' => array(
                'gatewayObject' => 'bookingItemPassenger',
                'modelClass' => 'BookingItemPassenger',
            ),
            'bookingLight' => array(
                'gatewayObject' => 'bookingLight',
                'modelClass' => 'BookingLight',
            ),
            'bookingStatus' => array(
                'gatewayObject' => 'bookingStatus',
                'modelClass' => 'BookingStatus',
            ),
            'amenity' => array(
                'gatewayObject' => 'amenity',
                'modelClass' => 'Amenity',
            ),
            'busRoute' => array(
                'gatewayObject' => 'busRoute',
                'modelClass' => 'BusRoute',
            ),
            'busRouteLight' => array(
                'gatewayObject' => 'busRouteLight',
                'modelClass' => 'BusRouteLight',
            ),
            'busSchedule' => array(
                'gatewayObject' => 'busSchedule',
                'modelClass' => 'BusSchedule',
            ),
            'busScheduleBusStop' => array(
                'gatewayObject' => 'busScheduleBusStop',
                'modelClass' => 'BusScheduleBusStop',
            ),
            'busScheduleLight' => array(
                'gatewayObject' => 'busScheduleLight',
                'modelClass' => 'BusScheduleLight',
            ),
            'busType' => array(
                'gatewayObject' => 'busType',
                'modelClass' => 'BusType',
            ),
            'cancellationCharge' => array(
                'gatewayObject' => 'bookingItemCancellation',
                'modelClass' => 'BookingItemCancellation',
            ),
            'change' => array(
                'gatewayObject' => 'change',
                'modelClass' => 'Change',
            ),
            'changeType' => array(
                'gatewayObject' => 'changeType',
                'modelClass' => 'ChangeType',
            ),
            'city' => array(
                'gatewayObject' => 'city',
                'modelClass' => 'City',
            ),
            'client' => array(
                'gatewayObject' => 'client',
                'modelClass' => 'Client',
            ),
            'conductor' => array(
                'gatewayObject' => new Conductor($sm),
                'modelClass' => 'Conductor',
            ),
            'division' => array(
                'gatewayObject' => 'division',
                'modelClass' => 'Division',
            ),
            'driver' => array(
                'gatewayObject' => new Driver($sm),
                'modelClass' => 'Driver',
            ),
            'module' => array(
                'gatewayObject' => 'module',
                'modelClass' => 'Module',
            ),
            'operationalSchedule' => array(
                'gatewayObject' => 'operationalSchedule',
                'modelClass' => 'OperationalSchedule',
            ),
            'passenger' => array(
                'gatewayObject' => 'passenger',
                'modelClass' => 'Passenger',
            ),
            'payment' => array(
                'gatewayObject' => 'payment',
                'modelClass' => 'Payment',
            ),
            'person' => array(
                'gatewayObject' => 'person',
                'modelClass' => 'Person',
            ),
            'refund' => array(
                'gatewayObject' => 'refund',
                'modelClass' => 'Refund',
            ),
            'rule' => array(
                'gatewayObject' => 'rule',
                'modelClass' => 'Rule',
            ),
            'supplier' => array(
                'gatewayObject' => 'supplier',
                'modelClass' => 'Supplier',
            ),
            'supplierGroup' => array(
                'gatewayObject' => 'supplierGroup',
                'modelClass' => 'SupplierGroup',
            ),
            'supplierAccount' => array(
                'gatewayObject' => 'supplierAccount',
                'modelClass' => 'SupplierAccount',
            ),
            'supplierContactPerson' => array(
                'gatewayObject' => new SupplierContactPerson($sm),
                'modelClass' => 'SupplierContactPerson',
            ),
            'travelClass' => array(
                'gatewayObject' => 'travelClass',
                'modelClass' => 'TravelClass',
            ),
            'allowedDivisions' => array(
                'gatewayObject' => new AllowedDivisions($sm),
                'modelClass' => 'AllowedDivisions',
            ),
            'agent' => array(
                'gatewayObject' => 'agent',
                'modelClass' => 'Agent',
            ),
            'seatingProfile' => array(
                'gatewayObject' => 'seatingProfile',
                'modelClass' => 'SeatingProfile',
            ),
            'user' => array(
                'gatewayObject' => 'user',
                'modelClass' => 'User',
            ),
            'userLight' => array(
                'gatewayObject' => 'userLight',
                'modelClass' => 'UserLight',
            ),
            'permission' => array(
                'gatewayObject' => 'permission',
                'modelClass' => 'Permission',
            ),
            'permissionGroup' => array(
                'gatewayObject' => 'permissionGroup',
                'modelClass' => 'PermissionGroup',
            ),
            'permissionSingle' => array(
                'gatewayObject' => 'permissionSingle',
                'modelClass' => 'PermissionSingle',
            ),
            'accountStatus' => array(
                'gatewayObject' => 'accountStatus',
                'modelClass' => 'AccountStatus',
            ),
            'userGroup' => array(
                'gatewayObject' => 'userGroup',
                'modelClass' => 'UserGroup',
            ),
			 'district' => array(
                'gatewayObject' => 'district',
                'modelClass' => 'District',
            ),
        );
    }


    public function get($id)
    {
        if (is_array($id)) {
            return $this->getList();
        }
		
        $entity = $this->params()->fromRoute('entity');
        $oGateway = $this->getGatewayObject($entity);

        $response = $oGateway->getOne($id);

        $responseObj = $this->getResponseWithHeader()
            ->setContent(Json::encode($response->body));
        return $responseObj;
    }

    public function getList()
    {
        $entity = $this->params()->fromRoute('entity');
        $oGateway = $this->getGatewayObject($entity);

        $queryParams = $this->params()->fromQuery();

        //build criteria
        $criteria = new QueryCriteria();
        $criteria->setParams($queryParams);
		//print_r($criteria);
		//print_r($oGateway);
		
		// $myfile = fopen("C:\\wamp64\\logs\\busbook-error.log", "w") or die("Unable to open file!");

		// fwrite($myfile, $criteria);
		
		// fclose($myfile);
		

        $response = $oGateway->getList($criteria);

        $responseObj = $this->getResponseWithHeader();
        $responseObj->getHeaders()->addHeaderLine('X-Total-Count', $response->headers->offsetGet('X-Total-Count'));
        $responseObj->setContent(Json::encode($response->body));
		
		// $myfile = fopen("C:\wamp64\logs\busbook-error.log", "w") or die("Unable to open file!");
		
		// fwrite($myfile, print_r($response->body,true));
		
		// fclose($myfile);

        return $responseObj;
    }

    public function create($data)
    {
        
		$entity = $this->params()->fromRoute('entity');
        
		$oGateway = $this->getGatewayObject($entity);

        //set uploaded files for each column if exists
        $this->setUploadedFileIfExists($entity, $data);

        $oModel = $this->getModelObject($entity, $data, $this->getServiceLocator());

        $response = $oGateway->createOne($oModel);

        $responseObj = $this->getResponseWithHeader()
            ->setContent(Json::encode($response->body));
		
		
        return $responseObj;
    }

    public function update($id, $data)
    {
        $entity = $this->params()->fromRoute('entity');
        $oGateway = $this->getGatewayObject($entity);

        $oModel = $this->getModelObject($entity, $data, $this->getServiceLocator());

        $response = $oGateway->updateOne($id, $oModel);

        $responseObj = $this->getResponseWithHeader()
            ->setContent(Json::encode($response->body)) ;
        return $responseObj;
    }

    public function delete($id)
    {
        $entity = $this->params()->fromRoute('entity');
        $oGateway = $this->getGatewayObject($entity);

        $response = $oGateway->deleteOne($id);

        $responseObj = $this->getResponseWithHeader();
        if ($response->code != 200) {
            $responseObj->setStatusCode($response->code);
            $responseObj->setContent(Json::encode($response->body));
        }
        return $responseObj;
    }

    public function uploadAction()
    {
        $entity = $this->params()->fromRoute('entity');
        $json = new JsonModel();

        try {
            if ($this->getRequest()->isPost()) {
                $files = $this->getRequest()->getFiles()->toArray();
                $post = $this->getRequest()->getPost()->toArray();

                foreach ($files as $f) {
                    $transferAdapter = new Http();
                    $transferAdapter->setValidators(array(
                        new Size(array('max' => 1000000)),
                        new Extension(array('extension' => array('png', 'jpg', 'gif', 'jpeg', 'JPG')))
                    ), $f['name']);

                    if ($transferAdapter->isValid()) {
                        //$transferAdapter->receive();
                        $sess = $this->getSession();
                        $sess->offsetSet($entity . '-' . $post['field_name'],
                            base64_encode(file_get_contents($f['tmp_name'])));
                        $json->setVariable('success', true);
                    } else {
                        throw new \Exception('Error in file upload');
                    }
                }

            } else {
                throw new \Exception('Error in file upload');
            }
        } catch (\Exception $e) {
            $json->setVariable('error', $e->getMessage());
        }
        return $json;
    }

    private function setUploadedFileIfExists($entity, &$fields)
    {
        $session = $this->getSession();

        foreach ($fields as $name=>$field) {
            $sessionKeyName = $entity .'-' .$name;

            //check if uploaded file exists
            if($session->offsetExists($sessionKeyName)){
                //set image data
                $fields[$name] = $session->offsetGet($sessionKeyName);
                //remove data
                $session->offsetUnset($sessionKeyName);
            }
        }

    }

    // configure response
    public function getResponseWithHeader()
    {
        $response = $this->getResponse();
        $response->getHeaders()
            //make can accessed by *
            ->addHeaderLine('Access-Control-Allow-Origin','*')
            //set allow methods
            ->addHeaderLine('Access-Control-Allow-Methods','POST PUT DELETE GET')
            ->addHeaderLine('Content-Type', 'application/json');

        return $response;
    }

    /**
     * Return validated gateway object
     * @param $entity
     * @param ServiceManager $sm
     * @return \Api\Operation\CrudInterface
     * @throws \Exception
     */
    protected function getGatewayObject($entity, ServiceManager $sm = null)
    {
        if ($sm === null) {
            $sm = $this->getServiceLocator();
        }
        $conf = $this->_getConfig($sm);
		
        //get configs for entity
        if(!isset($conf[$entity])){
            throw new \Exception('Entity is not defined in configuration');
        }
        $entityConf = $conf[$entity];
        //create gatewayObject
        $gateway = $entityConf['gatewayObject'];

        //if not an object pass through factory object
        if(!is_object($gateway)){
            $gateway = new RestFactory($sm, $gateway);
        }

        //check if it implements crud interface
        if(!($gateway instanceof CrudInterface)){
            throw new \Exception('Gateway object needs to implement Crud interface');
        }

        return $gateway;
    }

    protected function getModelObject($entity, $data, $sm)
    {
        $conf = $this->_getConfig($sm);

        $entityConf = $conf[$entity];
        $modelClassName = '\Api\Client\Soap\Core\\' . $entityConf['modelClass'];

        return Base::prepareEntityToSave($entityConf['modelClass'], $data);

//        //create object without calling constructor
//        $refClass = new \ReflectionClass($modelClassName);
//        $oModel = $refClass->newInstanceWithoutConstructor();
//
//        //set properties
//        foreach($data as $prop => $value){
//            //check if property exists
//            if($refClass->hasProperty($prop)){
//                //create object from type and assign value
//                if (!is_null($value) && preg_match('/@var\s+([^\s]+)/', $refClass->getProperty($prop)->getDocComment(), $matches)) {
//                    list(, $type) = $matches;
//                    $type = strtolower($type);
//
//                    //check if basic types
//                    if(!in_array($type, array('string', 'int', 'boolean', 'float', 'array', 'datetime', 'gender', 'paymentrefundmode', 'cancellationcause'))){
//                        if (is_array($value)) {
//                            if (! self::is_assoc($value)) { // this is a set of foreign key objects, set ids. On the other hand associative array is an object is array form
//                                foreach ($value as $k => &$v) {
//                                    if (is_numeric($v)) {
//                                        $v = array('id' => intval($v));
//                                    } else {
//                                        unset($value[$k]);
//                                    }
//                                }
//                            }
//                        } elseif (is_numeric($value)) { // this is a for a foreign key object, set id
//                            $value = array('id' => intval($value));
//                        }
//                    }
//                }
//
//                $oModel->{$prop} = $value;
//            } else {
//                // Ignore them for now. ng-admin errornously sends derived fields such as fullName field for driver
//                //throw new \Exception('Property '. $prop .' is not defined.');
//            }
//        }
//
//        return $oModel;
    }

    private static function is_assoc(array $array) {
          return (bool) count(array_filter(array_keys($array), 'is_string'));
    }

    /** @var Container  */
    private $_session;

    /**
     * @return Container
     */
    private function getSession()
    {
        if(!$this->_session){
            $this->_session = new Container('ngcrud');
        }
        return $this->_session;
    }
}