<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 10/6/14
 * Time: 1:38 PM
 */

namespace Api\Manager\Schedule;

use Api\Client\Soap\Core\BusRouteBusStop;
use Api\Client\Soap\Core\BusSchedule;
use Api\Client\Soap\Core\LegCriteria;
use Api\Client\Soap\Core\SearchCriteria;
use Api\Manager\Base;
use Api\Manager\Schedule;
use Application\Acl\Acl;
use Application\Discount;
use Application\Helper\ExprDateTime;
use Application\Model\User;
use Application\Util\Log;

class Search extends Base{

    protected $_session = null;

    private $_loggedInUserId = null;
    private $_routeMapping = array();
    private $_discountCode = '';

    /**
     * set session variable
     * @param $session
     */
    public function setSession($session)
    {
        $this->_session = $session;
    }

    /**
     * @return bool|mixed|string
     */
    public function getDiscountCode()
    {
        //priority given to object's code
        if(!empty($this->_discountCode)){
            return $this->_discountCode;
        }else{
            //check for saved discount code in session
            /** @var Discount $oDiscount */
            $oDiscount = $this->getServiceManager()->get('Application\Discount');
            $discountCode = $oDiscount->getCurrentDiscountCode();
            if(!empty($discountCode)){
                return $discountCode;
            }
        }
        return false;
    }

    /**
     * @param string $discountCode
     * @return Search
     */
    public function setDiscountCode($discountCode)
    {
        $this->_discountCode = $discountCode;
        return $this;
    }

    /**
     * Set user id
     * @param $userId
     */
    public function setLoggedInUserId($userId)
    {
        $this->_loggedInUserId = $userId;
    }

    /**
     * @return \Application\Model\User
     */
    private function getUserObject()
    {
        $oUser = $this->serviceManager->get('AuthService')->getIdentity();
        return $oUser ? $oUser : new User();
    }

    /**
     * @param $fromCityId
     * @param $toCityId
     * @param $roundTrip
     * @param $departureDate
     * @param $fromCityName
     * @param $toCityName
     * @return array
     */
    public function getResult($fromCityId, $toCityId, $roundTrip, $departureDate, $fromCityName = '', $toCityName = '')
    {
        //get discount codes
        $discountCode = $this->getDiscountCode();

        $oUser = $this->getUserObject();
        /**
         * @todo change this once backend completes a method to get client id from user
         */
        $clientId = User::DEFAULT_USER_ID;

        //create a criteria object
        /** @var \Api\Client\Soap\Core\SearchCriteria $criteria */
        $criteria = Base::getEntityObject('SearchCriteria'); //new SearchCriteria($clientId, "", "", $fromCityId, $roundTrip, $toCityId);
        $criteria->clientId = $clientId;
        $criteria->source = "";
        $criteria->discountCode = ($discountCode==false? '' : $discountCode);
        $criteria->roundTrip = $roundTrip;
        $criteria->ip = $this->getServiceManager()->get('Request')->getServer('REMOTE_ADDR');
        //set name or id based on validity
        if(!empty($fromCityId) && intval($fromCityId)>0){
            $criteria->fromCityId = $fromCityId;
        }
        //if id is not valid, use city name
        elseif(!empty($fromCityName) && strlen($fromCityName)>0){
            $criteria->fromCity = $fromCityName;
        }

        //to city
        if(!empty($toCityId) && intval($toCityId)>0){
            $criteria->toCityId = $toCityId;
        }
        //if id is not valid, use city name
        elseif(!empty($toCityName) && strlen($toCityName)>0){
            $criteria->toCity = $toCityName;
        }

        //create leg criteria - two if two way request
        if($roundTrip){
            /**
             * To be implemented
             */
            $leg = array(

            );
        }else{
            //create timestamp of the departure date
            $oDepartureDate = \DateTime::createFromFormat('Y-m-d', $departureDate);
            $currentTime = new \DateTime();
            if ($oDepartureDate->format('Y-m-d') == $currentTime->format('Y-m-d')) { // compare date parts only
                // if this is today, set current time, so that stale results won't show up
                //$oDepartureDate->setTime($currentTime->format('H'), $currentTime->format('i'), $currentTime->format('s'));
                //Temporarily set time to 0,0 in order to show all the results for the day. The users think we don't have buses
                $oDepartureDate->setTime(0, 0, 0);
            }else{
                //set date to start from 0,0 in order to load all the results for the day
                $oDepartureDate->setTime(0, 0, 0);
            }

            /** @var \Api\Client\Soap\Core\LegCriteria $legCriteria */
            $legCriteria = Base::getEntityObject('LegCriteria');
            $legCriteria->departureTimestamp = $oDepartureDate->getTimestamp() * 1000; //convert into miliseconds
            //get end timestamp
            $endTimestamp = $oDepartureDate->add(new \DateInterval("P1D"));
            $endTimestamp->setTime(0, 0);
            $legCriteria->departureTimestampEnd = $endTimestamp->getTimestamp() * 1000;
            //only one leg if one way
            $leg = array(
                $legCriteria
            );
        }
        $criteria->legCriterion = $leg;

        //return result
        return $this->doSearch($criteria);
    }

    /**
     * @param $criteria SearchCriteria
     * @return array
     * @throws \Application\Exception\SessionTimeoutException
     * @throws \Exception
     */
    public function doSearch($criteria)
    {		
		$SearchResponse = $this->getSearchService()->search($this->_session, $criteria);
		
        //check if status is okay
        if($this->responseIsValid($SearchResponse)){
            /* @var $data \Api\Client\Soap\Core\SearchResult */
            $data = $SearchResponse->data;
            //get one way and return results
            $oneWay = $data->legResults[0];
            $return = $data->legResults[1];

            return array(
                'oneway' => $this->formatSearchResult($oneWay),
                'return' => null,
            );
        }
    }

    /**
     * @param $searchResponse \Api\Client\Soap\Core\LegResult
     * @return array
     */
    private function formatSearchResult($searchResponse)
    {
        $data = array();

        $resultLegs = $searchResponse->legs;
        if(empty($resultLegs)){
            $data = array();
        }
        //if there is one resule leg get secotors
        else if(!is_array($resultLegs)){
            $data[] = $this->_formatLegs($resultLegs);
        }else{
            //iterate through results
            foreach($resultLegs as $leg){
                $data[] = $this->_formatLegs($leg);
            }
        }

        return $data;
    }

    /**
     * @param $leg \Api\Client\Soap\Core\ResultLeg
     * @return array
     */
    private function _formatLegs($leg)
    {
        //expire booking before 12 hours
        $EXIRATION_TIME = 60*60*12;

        $data = array();
        $translator = $this->getServiceManager()->get('translator');
        /** @var \Api\Manager\Schedule $scheduleManager */
        $scheduleManager = $this->getServiceManager()->get('Api\Manager\Schedule');

        //get leg data
        $data['resultIndex'] = $leg->resultIndex;
        $data['cost'] = $leg->cost;
        $data['price'] = $leg->price;
        //loop sectors
        $sectors = array();
        //convert to array if not an array
        $legSectors = !is_array($leg->sectors) ? array($leg->sectors) : $leg->sectors;
        foreach($legSectors as $sector){
            /**
             * @var $sector \Api\Client\Soap\Core\ResultSector
             */
            $schedule = $sector->schedule;

            //caluculate date difference
            $fromToDiff = date_diff(new \DateTime($sector->arrivalTime), new \DateTime($sector->departureTime));
            $sec = array(
                /**
                 * prices and taxes
                 * base             : cost => this will be shown on search result
                 * gross            : cost + markups : gross price
                 * priceBeforeTax   : gross price + discounts : price before tax
                 * priceBeforeCharges : price before tax + tax : price before charges => this is the actual payment amount
                 * total            : price before charges + charges (agent fee) : price => this is the face value
                 */
                'prices'        => array(
                    'cost'              => $sector->cost,
                    'baseMargins'       => ($sector->fare - $sector->cost),
                    'fare'              => $sector->fare,
                    'baseMarkups'       => ($sector->grossPrice - $sector->fare),
                    'gross'             => $sector->grossPrice,
                    'baseDiscount'      => ($sector->priceBeforeTax - $sector->grossPrice),
                    'priceBeforeTax'    => $sector->priceBeforeTax,
                    'baseTax'           => ($sector->priceBeforeCharges - $sector->priceBeforeTax),
                    'priceBeforeCharges'=> $sector->priceBeforeCharges,
                    'baseCharges'       => ($sector->price - $sector->priceBeforeCharges),
                    'total'             => $sector->price,
					'childFare'			=> $sector->childFare,
                ),
                "arrivalTime"   => ExprDateTime::getDateFromString($sector->arrivalTime)->formatForJs(),
                "departureTime" => ExprDateTime::getDateFromString($sector->departureTime)->formatForJs(),
                "duration"      => array(
                    'h' => $fromToDiff->h,
                    'i' => $fromToDiff->i,
                ),
                "fromCityName"  => $translator->translate($sector->fromCity->name),
                "fromCityId"    => $sector->fromCity->id,
                "toCityName"    => $translator->translate($sector->toCity->name),
                "toCityId"      => $sector->toCity->id,
                'scheduleId'    => $schedule->id,
                'schedule'      => array(
                    'actualArrivalTime' => ExprDateTime::getDateFromString($schedule->arrivalTime)->formatForJs(),
                    'actualDepartureTime' => ExprDateTime::getDateFromString($schedule->departureTime)->formatForJs(),
                    'actualFromCity'    => $translator->translate($schedule->busRoute->fromCity->name),
                    'actualToCity'    => $translator->translate($schedule->busRoute->toCity->name),
                ),
                'bus'   => array(
                    'amenities'     => (!is_array($schedule->bus->amenities) ? array($schedule->bus->amenities) : $schedule->bus->amenities),
                    'busType'       => $schedule->bus->busType->type,
                    'busTypeId'       => $schedule->bus->busType->id,
                    'busTypeDesc'   => $schedule->bus->busType->description,
                    'seatingCapacity' => $schedule->bus->busType->seatingCapacity,
                    'name'          => $schedule->bus->name,
                    'plateNumber'   => $schedule->bus->plateNumber,
                    'travelClassName'   => $schedule->bus->travelClass->name,
                    'travelClassCode'   => $schedule->bus->travelClass->code,
                    'id'            => $schedule->bus->id,
                    'image'         => (isset($schedule->bus->image)? $schedule->bus->image : null),
                ),
                'busRoute'   => array(
                    'fromCity'     => $translator->translate($schedule->busRoute->fromCity->name),
                    'routeNumber'  => $schedule->busRoute->displayNumber,
                    'routeName'     => $schedule->busRoute->name,
                    'toCity'       => $translator->translate($schedule->busRoute->toCity->name),
                    //'routeStops'   => $this->getScheduleStops($schedule),
                    'genderRequired' => $schedule->busRoute->genderRequired,
                    'boardingStops' => $this->getStopsAtCity($sector->fromCity->id, $schedule),
                    'dropStops'     => $this->getStopsAtCity($sector->toCity->id, $schedule),
                ),
                //'expired'           => ( (time()+$EXIRATION_TIME) > strtotime($sector->departureTime)),
                'expired'           => $scheduleManager->hasWebBookingEnded($schedule),
                'routemaster'       => $this->getRouteMappingUrl($schedule->busRoute->id),
            );

            $sectors[] = $sec;
        }
        $data['sectors'] = $sectors;
        /**
         * set price values - This is only until sectors implemented
         */
        $data['prices'] = count($sectors)>0 ? $sectors[0]['prices'] : array();
        $data['actualDepartureTime'] = count($sectors)>0 ? $sectors[0]['schedule']['actualDepartureTime'] : null;
        return $data;
    }

    /**
     * @param $routeId
     * @return mixed|null
     */
    private function getRouteMappingUrl($routeId)
    {
        if(empty($this->_routeMapping)){
            //load route maps
            $this->_routeMapping = include './data/routemaster/routemapping.php';
        }

        return isset($this->_routeMapping[$routeId])? $this->_routeMapping[$routeId] : null;
    }

    /**
     * @param BusSchedule $schedule
     * @return array
     */
    public function getScheduleStops($schedule)
    {
        $translator = $this->getServiceManager()->get('translator');
        $scheduleStops = array();
        
        //generate schedule stops with stop name and id
        foreach ($schedule->scheduleStops as $scheduleStop) {
            $scheduleStops[] = array(
                'stop' => array(
                    'id' => $scheduleStop->stop->id,
                    'name' => $translator->translate($scheduleStop->stop->name),
                    'city' => array(
                        'id' => $scheduleStop->stop->city->id
                    ),
                )
            );
        }
        return $scheduleStops;
    }

    /**
     * @param $cityId
     * @param BusSchedule $schedule
     * @return array
     */
    private function getStopsAtCity($cityId, $schedule)
    {
        $translator = $this->getServiceManager()->get('translator');
        $stopsAtCity = array();
        foreach ($schedule->scheduleStops as $scheduleStop) {
            //check if stop belongs to the city
            if($cityId==$scheduleStop->stop->city->id){
                //add the stop to array
                $stopsAtCity[] = array(
                    'id' => $scheduleStop->stop->id,
                    'name' => $translator->translate($scheduleStop->stop->name)
                );
            }

        }
        return $stopsAtCity;
    }

}
