<?php


namespace Legacy\Ticketing;


use Api\Manager\Base;
use Application\Helper\ExprDateTime;
use Application\InjectorBase;
use Application\Model\User;
use Legacy\Ticketing\Session;

class Search extends InjectorBase
{
    /**
     * Search and get bus schedules
     * @param $from
     * @param $to
     * @param $date
     * @return array
     */
    public function getResults($from, $to, $date)
    {
        /** @var $oSession \Api\Manager\Session */
        $oSession = $this->getServiceLocator()->get('Api\Manager\Session');
        //create a session
        $session = $oSession->create();
        //save session
        /** @var Session $oLegacySession */
        $oLegacySession = $this->getServiceLocator()->get('Legacy\Ticketing\Session');
        $oLegacySession->setSearchSession($session);

        $auth = $this->getServiceLocator()->get('AuthService');
        /**@var $search \Api\Manager\Schedule\Search */
        $search = $this->getServiceLocator()->get('Api\Manager\Schedule\Search');
        $search->setSession($session);
        $search->setLoggedInUserId($auth->getIdentity()->id);

        $clientId = User::DEFAULT_USER_ID;
        //create a criteria object
        /** @var \Api\Client\Soap\Core\SearchCriteria $criteria */
        $criteria = Base::getEntityObject('SearchCriteria');
        $criteria->clientId = $clientId;
        $criteria->source = "";
        $criteria->discountCode = "";
        $criteria->fromCityId = $from;
        $criteria->toCityId = $to;
        $criteria->roundTrip = false;
        $criteria->ip = $this->getServiceLocator()->get('Request')->getServer('REMOTE_ADDR');

        //create timestamp of the departure date
        $oDepartureDate = ExprDateTime::getDateFromString($date);
        //set date to start from 0,0 in order to load all the results for the day
        $oDepartureDate->setTime(0, 0, 0);

        //only one leg if one way
        /** @var \Api\Client\Soap\Core\LegCriteria $legCriteria */
        $legCriteria = Base::getEntityObject('LegCriteria');
        $legCriteria->departureTimestamp = $oDepartureDate->getTimestampMiliSeconds(); //convert into miliseconds
        //get end timestamp
        $endTimestamp = clone $oDepartureDate;
        $endTimestamp = $endTimestamp->add(new \DateInterval("P1D"));
        $endTimestamp->setTime(0, 0);
        $legCriteria->departureTimestampEnd = $endTimestamp->getTimestampMiliSeconds();
        $criteria->legCriterion = array(
            $legCriteria
        );
        $searchResponse = $search->doSearch($criteria);

        /**
         * loop through and save price in session
         */
        $busPrices = array();
        foreach($searchResponse['oneway'] as $forwardLeg){
            $scheduleId = count($forwardLeg['sectors'])>0 ? $forwardLeg['sectors'][0]['scheduleId'] : 0;

            $busPrices[ $forwardLeg['resultIndex'] ] = array(
                'price' => $forwardLeg['prices'],
                'scheduleId' => $scheduleId,
            );
        }
        //save price for later
        $oLegacySession->setSearchData($session, $busPrices);

        return $searchResponse;
    }
}