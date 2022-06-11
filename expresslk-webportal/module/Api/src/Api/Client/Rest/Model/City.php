<?php
/**
 * Created by PhpStorm.
 * User: Kavinda
 * Date: 8/24/14
 * Time: 12:26 PM
 */

namespace Api\Client\Rest\Model;


use Api\Client\Rest\Factory as RestFactory;
use Api\Client\Rest\Connector;
use Api\Operation\Request\QueryCriteria;
use Application\Acl\Acl;
use Application\Util\Log;
use Zend\ServiceManager\ServiceManager;

class City extends Connector{

    protected $_cityId;
    protected $_shouldTranslate = true;

    public function __construct($sm, $cityId = null)
    {
        parent::__construct($sm);

        $this->_cityId = $cityId;
    }

    /**
     * @return boolean
     */
    public function isShouldTranslate()
    {
        return $this->_shouldTranslate;
    }

    /**
     * @param boolean $shouldTranslate
     * @return City
     */
    public function setShouldTranslate($shouldTranslate)
    {
        $this->_shouldTranslate = $shouldTranslate;
        return $this;
    }



    /**
     * Get available city list
     * @param QueryCriteria $params
     * @return array|mixed
     */
    public function getCityList(QueryCriteria $params = null)
    {
        if(empty($params)){
            $params = new QueryCriteria();
            $params->setLoadAll()
                ->setSortField('name')
                ->addParam('active', 'true');
        }
        $oUser = Acl::getAuthUser($this->getServiceManager());
        //set up cache
        $oCache = $this->getServiceManager()->get('cache');
        $key = sha1('Api-citylist' . $oUser->username . $params->createQuery());

        $success = false;
        //get from cache
        $result = $oCache->getItem($key, $success);
        if(!$success){
            try {
                $url = $this->getBaseUrl() . '/city';
                $response = $this->get($url, $params);

                $result = $response->body;
                //cache result only if the response is proper
                if(is_array($result) && count($result)>0){
                    $oCache->setItem($key, $result);
                }
            } catch (\Exception $e) {
                //log error
                $log = new Log();
                $log->emerg($e, array(
                    'REST_getCityList' => 'Exception in GetCity List. - '. $url
                ));
            }
        }

        //add only if it's an array
        $result = is_array($result)? $this->translateCityNames($result) : array();
        return $result;
    }

    /**
     * Get destination cities related to passed in city id
     *
     * @param $cityId integer
     * @return array
     */
    public function getDestinationsOfCity($cityId)
    {
        $oUser = Acl::getAuthUser($this->getServiceManager());
        //set up cache
        $oCache = $this->getServiceManager()->get('cache');
        $key = sha1('Api-destinationlist-'.$cityId . $oUser->username);

        $success = false;
        //get from cache
        $result = $oCache->getItem($key, $success);
        if(!$success){
            try {
                $url = $this->getBaseUrl() . '/destination/'. $cityId;
                $response = $this->get($url);

                $result = $response->body;
                //cache result only if the response is proper
                if(is_array($result) && count($result)>0){
                    $oCache->setItem($key, $result);
                }
            } catch (\Exception $e) {
            }
        }

        //add only if it's an array. translate names
        $result = is_array($result)? $this->translateCityNames($result) : array();
        return $result;
    }

    /**
     * Get city list which only belongs to the logged in user
     * @param null $startFrom
     * @param QueryCriteria $params
     * @return array|mixed
     */
    public function getTerminusCityList($startFrom = null, QueryCriteria $params = null)
    {
        if(empty($params)){
            $params = new QueryCriteria();
            $params->setLoadAll()
                ->setSortField('name')
                ->addParam('active', 'true');
        }
        $oUser = Acl::getAuthUser($this->getServiceManager());
        //set up cache
        $oCache = $this->getServiceManager()->get('cache');
        $key = 'Api-termcitylist_'. $oUser->username;
        //append query to key
        if(!empty($params)){
            $key = $key . $params->createQuery();
        }
        //append start from if exists
        if(!empty($startFrom)){
            $key = $key . $startFrom;
        }
        //hash the key
        $key = sha1($key);

        $success = false;
        //get from cache
        $result = $oCache->getItem($key, $success);
		
		$success = false;
        if(!$success){
            try {
                $url = $this->getBaseUrl() . '/terminusCity';
                //append start from city if exists
                if(!empty($startFrom)){
                    $url = $url . '/' . $startFrom;
                }
                $response = $this->get($url, $params);

                $result = $response->body;
                //cache result only if the response is proper
                if(is_array($result) && count($result)>0){
                    $oCache->setItem($key, $result);
                }
            } catch (\Exception $e) {
                //log error
                $log = new Log();
                $log->emerg($e, array(
                    'REST_getCityList' => 'Exception in GetCity List. - '. $url
                ));
            }
        }

        //add only if it's an array
        $result = is_array($result)? $this->translateCityNames($result) : array();
        return $result;
    }

    /**
     * Get cities by suggestion term
     * @param $sTerm
     * @return array|object|string
     */
    public function getCitiesBySuggestion($sTerm)
    {
        $oSuggest = new RestFactory($this->getServiceManager(), 'suggest/city/'. $sTerm);
        $oListResponse = $oSuggest->getList(new QueryCriteria());
        return $oListResponse->body;
    }

    public function getDestinationsBySuggestion($sOriginTerm, $sSearchTerm)
    {
        $oSuggest = new RestFactory($this->getServiceManager(), 'suggest/destination/'. $sOriginTerm . '/' . $sSearchTerm);
        $oListResponse = $oSuggest->getList(new QueryCriteria());
        return $oListResponse->body;
    }

    /**
     * Translate city names
     * @param $cities
     * @return mixed
     */
    private function translateCityNames($cities)
    {
        if($this->_shouldTranslate && !empty($cities)){
            foreach ($cities as &$city) {
                $city->name = $this->getServiceManager()->get('translator')->translate($city->name);
            }
        }
        return $cities;
    }

}