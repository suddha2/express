<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 10/5/14
 * Time: 6:06 PM
 */

namespace Api\Manager;

use Application\Exception\SessionTimeoutException;
use Application\Util;
use Zend\ServiceManager\ServiceManager;

class Base {

    protected $serviceManager;

    public function __construct(ServiceManager $serviceManager)
    {
        $this->serviceManager = $serviceManager;
    }

    /**
     * Check if the server response is valid else throw exception
     * @param \Api\Client\Soap\Core\ExpResponse $response
     * @return bool
     * @throws SessionTimeoutException
     * @throws \Exception
     */
    protected function responseIsValid($response)
    {
        if($response->status>0){
            return true;
        }else{
            //check if session timeout
            if($response->status == -2){
                throw new SessionTimeoutException("You took too long. Session has expired. Please refresh the page.");
            }else{
                //throw a generic error
                throw new \Exception( empty($response->message)? 'A server error occurred. Please try again' : $response->message );
            }
        }
    }

    /**
     * @return \Api\Client\Soap\ReservationService
     */
    public function getSearchService()
    {
        return $this->serviceManager->get('Api\SearchService');
    }

    /**
     * @return \Api\Client\Soap\ReportService
     */
    public function getReportService()
    {
        return $this->serviceManager->get('Api\ReportService');
    }

    /**
     * @return ServiceManager
     */
    public function getServiceManager()
    {
        return $this->serviceManager;
    }

    /**
     * Get SOAP entity object from class without calling constructor
     * @param $className
     * @return object
     */
    public static function getEntityObject($className)
    {
        $modelClassName = '\Api\Client\Soap\Core\\' . $className;

        //create object without calling constructor
        $refClass = new \ReflectionClass($modelClassName);
        $oModel = $refClass->newInstanceWithoutConstructor();

        return $oModel;
    }

    /**
     * !Important. The passed entity should be an object with children mapping to exact objects
     * @param $oEntity
     */
    public static function flattenEntityObject(&$oEntity)
    {
        foreach ($oEntity as $key=> &$value) {
            //if value is array
            if(is_array($value) || is_object($value)){
                foreach ($value as $k => &$v) {
                    //if key is 'id' save it as integer value
                    if (strtolower($k)=='id') {
                        $v = intval($v);
                    }
                    //else set the value as null
                    else {
                        if(is_object($value)){
                            unset($value->{$k});
                        }else{
                            unset($value[$k]);
                        }
                    }
                }
            }
        }
    }

    /**
     * Prepare SOAP entity objects for update and insert
     * @param $className String Entity class name
     * @param $oEntityMap Object With properties to be matched to entity
     * @return object
     */
    public static function prepareEntityToSave($className, $oEntityMap)
    {
        $oModel = self::getEntityObject($className);
        $refClass = new \ReflectionClass(get_class($oModel));
        //create a deep copy by serializing. So the original objects aren't changed
        $oEntity = unserialize(serialize($oEntityMap));

        /**
         * @futureUdantha This is FUCKING UGLY as hell.
         * Change this to a proper mapping structure when you have time.
         */
        //set properties
        foreach($oEntity as $prop => $value){
            //check if property exists
            if($refClass->hasProperty($prop)){
                //create object from type and assign value
                if (!is_null($value) && preg_match('/@var\s+([^\s]+)/', $refClass->getProperty($prop)->getDocComment(), $matches)) {
                    list(, $type) = $matches;
                    $type = strtolower($type);

                    //check if basic types. If not a basic type this is a reference object or array of objects
                    if(!in_array($type, array('string', 'int', 'boolean', 'float', 'datetime', 'gender', 'paymentrefundmode', 'cancellationcause'))){
                        //convert to array of it's object
                        if(is_object($value)){
                            $value = json_decode(json_encode($value), true);
                        }

                        if (is_array($value)) {
                            //check if 'id' key exists. If so only include that
                            foreach ($value as $k => &$v) {
                                if (strtolower($k)=='id') {
                                    $v = intval($v);
                                }
                                //if both key and value ae numbers, this is a reference object
                                elseif(is_numeric($k) && is_numeric($v)){
                                    $v = array('id' => intval($v));
                                }
                                //if value is an object or array, recurse through to filter only reference objects
//                                elseif (is_object($v) || is_array($v)){
//                                    //preserve all the properties. Remove reference properties
//                                    foreach ($v as $refKey => &$refVal) {
//                                        //if both key and value ae numbers, this is a reference object
//                                        if(is_numeric($refKey) && is_numeric($refVal)){
//                                            $refVal = array('id' => intval($refVal));
//                                        }else{
//                                            //remove value if this is a reference object
//                                            if(is_object($refVal)){
//                                                unset($v->{$refKey});
//                                            }elseif(is_array($refVal)){
//                                                unset($v[$refKey]);
//                                            }
//                                        }
//                                    }
//                                }
                                else {
                                    //remove element
                                    if(is_object($value)){
                                        unset($value->{$k});
                                    }else{
                                        unset($value[$k]);
                                    }
                                }
                            }
                        }
                        //it is relatively safe to assume all the numeric values here are not class level numeric
                        //Class level numeric values are filtered out by outer if condition
                        elseif (is_numeric($value)) {
                            $value = array('id' => intval($value));
                        }
                    }
                }

                $oModel->{$prop} = $value;
            } else {
                // Ignore them for now. ng-admin errornously sends derived fields such as fullName field for driver
                //throw new \Exception('Property '. $prop .' is not defined.');
            }
        }
        //var_dump($oModel);die;
        return $oModel;
    }
}