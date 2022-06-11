<?php


namespace App\Ticketing\Controller;


use Api\Client\Soap\Core\BusSchedule;
use Api\Client\Soap\Core\BusScheduleBusStop;
use Api\Client\Soap\Core\PaymentMethod;
use Api\Client\Soap\Core\PaymentMethodCriteria;
use Api\Client\Soap\Core\PaymentRefundCriteria;
use Api\Client\Soap\Core\PaymentRefundMode;
use Api\Client\Soap\Core\PaymentRefundType;
use Api\Client\Soap\Core\VendorPaymentRefundMode;
use Api\Manager\Base;
use Api\Manager\Booking;
use Api\Manager\Criteria;
use Api\Manager\Session;
use App\Base\Controller\AbstractController;
use Api\Client\Soap\Core\Bus;
use Api\Operation\Request\QueryCriteria;
use App\Ticketing\Entity\BookingEntity;
use App\Ticketing\Entity\ScheduleEntity;
use App\Ticketing\Schedule;
use App\Ticketing\Schedule\Availability;
use Application\Exception\SessionTimeoutException;
use Application\Helper\ExprDateTime;
use Application\Model\User;
use Application\Sms\Template;
use Application\Util;
use Zend\Session\Container;
use Zend\View\Model\JsonModel;
use Api\Client\Rest\Factory as RestFactory;

class TicketboxController extends AbstractController{

    const PAY_TYPE_CASH = 'Cash';
    const PAY_TYPE_AGENTS = 'agents';
    const PAY_TYPE_ATBUS = 'AtBus';

    /**
     * @var \Zend\Session\AbstractContainer
     */
    private $busResultPrices;

    private $_cacheResultPrices = 'cached-result-prices-search-app';

    public function __construct()
    {
        $this->busResultPrices = new Container();
    }

    /**
     * @param $username
     * @param $token
     * @return bool
     */
    private function isAuthorized($username, $token)
    {
        /** @var \App\Base\Auth $auth */
        $auth = $this->getServiceLocator()->get('App\Auth');
        if(!$auth->isAuthorised($username, $token)){
            throw new \Exception('Authorization failed. Please login again');
        }
    }

    public function getpastschedulesAction()
    {
        try{
            $params = $this->params()->fromJson();
            $username = $params['username'];
            $token = $params['token'];
            $busId = $params['bid'];

            //authenticate user
            $this->isAuthorized($username, $token);

            //get last 3 bus schedules
            $oToday = new ExprDateTime();
            $oQueryCriteria = new QueryCriteria();
            $oQueryCriteria->setParams(array(
                'bus' => $busId,
                'pageRows' => 3, //show only last 3 records
                'pageStart' => 0,
                'sortDir' => 'DESC',
                'sortField' => 'departureTime',
                'departureStart' => $oToday->sub(new \DateInterval('P20D'))->getTimestampMiliSeconds(),
                'departureEnd' => (new ExprDateTime())->getTimestampMiliSeconds(),
            ));

            /** @var Schedule $oScheduleManager */
            $oScheduleManager = $this->getServiceLocator()->get('App\Ticketing\V1\Schedule');

            //set response
            $this->addSuccess(array(
                'schedules' => $oScheduleManager->getScheduleCollections($oQueryCriteria)
            ));
        } catch (\Exception $e) {
            $this->addError($e);
        }

        return $this->getJsonResponse();
    }

    public function bookinglistAction()
    {
        try{
            $params = $this->params()->fromJson();
            $username = $params['username'];
            $token = $params['token'];
            $scheduleId = $params['scid'];
            $sessionId = isset($params['ssi'])? $params['ssi'] : false;

            //authenticate user
            $this->isAuthorized($username, $token);

            //refresh search session if parameter exists
            if(!empty($sessionId)){
                /** @var Session $oSession */
                $oSession = $this->getServiceLocator()->get('Api\Manager\Session');
                $oSession->refreshSession($sessionId);
            }

            //get bookings for a schedule
            /** @var \App\Ticketing\Booking $oBooking */
            $oBooking = $this->getServiceLocator()->get('App\Ticketing\V1\Booking');
            $aBookings = $oBooking->getBookingsForSchedule($scheduleId);

            //set response
            $this->addSuccess(array(
                'bookings'=> $aBookings,
            ));
        } catch (\Exception $e) {
            $this->addError($e);
        }

        return $this->getJsonResponse();
    }

    public function bookingdataAction()
    {
        try{
            $params = $this->params()->fromJson();
            $username = $params['username'];
            $token = $params['token'];
            $ref = $params['ref'];

            //authenticate user
            $this->isAuthorized($username, $token);

            $translator = $this->getServiceLocator()->get('translator');
            /** @var \App\Ticketing\Booking $oBooking */
            $oBooking = $this->getServiceLocator()->get('App\Ticketing\V1\Booking');
            $bookingResult = $oBooking->getByReference($ref);

            if($bookingResult['status']['code'] == Booking::STATUS_CODE_CANCELLED){
                throw new \Exception($translator->translate('Booking is already cancelled'));
            }

            //set response
            $this->addSuccess(array(
                'booking'=> $bookingResult,
            ));
        } catch (\Exception $e) {
            $this->addError($e);
        }

        return $this->getJsonResponse();
    }

    public function updatebookingAction()
    {
        try{
            $params = $this->params()->fromJson();
            $username = $params['username'];
            $token = $params['token'];
            $ref = $params['ref'];
            $action = $params['act'];
            $data = $params['data'];

            //authenticate user
            $this->isAuthorized($username, $token);

            $successResp = array();
            if($action=='JP'){
                $journeyPerformed = $data['jp'];
                $scheduleId = $data['scheduleId'];
                $seatNo = $data['seatNo'];
                /** @var ScheduleEntity $oSchedule */
                $oSchedule = $this->getServiceLocator()->get('App\Ticketing\V1\Entity\Schedule');
                $oSchedule->setJourneyPerformed($scheduleId, $seatNo, $journeyPerformed);
            }
            //change boarding point
            elseif ($action=='BP'){
                $bookingItemId = $data['itemId'];
                $boardingPointId = $data['bpId'];
                /** @var BookingEntity $oBookingEntity */
                $oBookingEntity = $this->getServiceLocator()->get('App\Ticketing\V1\Entity\Booking');
                $oBookingEntity->updateBoardingPoint($bookingItemId, $boardingPointId);
            }

            //set response
            $this->addSuccess($successResp);
        } catch (\Exception $e) {
            $this->addError($e);
        }

        return $this->getJsonResponse();
    }

    public function scheduleAction()
    {
        try {
            $params = $this->params()->fromJson();
            $username = $params['username'];
            $token = $params['token'];
            $busId = $params['busid'];

            //authenticate user
            $this->isAuthorized($username, $token);

            try {
                $now = time();
                //get next bus
                $busEntity = new RestFactory($this->getServiceLocator(), 'busSchedule/nextForBus');
                $nextSchedule = $busEntity->getOne($busId);
                /** @var BusSchedule $nextSchedule */
                $nextSchedule = $nextSchedule->body;
                //validate schedule object
                if(empty($nextSchedule)){
                    throw new \Exception('Schedule doesnt exists');
                }
                //check for ticketing active paramter
                $activeTime = ExprDateTime::getDateFromServices($nextSchedule->ticketingActiveTime)->getTimestamp();
                //if active time is not passed, don't show the schedule
                if($now < $activeTime){
                    throw new \Exception('Bus is not active yet.');
                }
            } catch (\Exception $e) {
                //next bus is not available. pass message to front end
                $this->addSuccess(array(
                    'resultstatus' => 'no bus'
                ));
                return $this->getJsonResponse();
            }

            //do search for bus
            /** @var \Api\Client\Soap\Core\SearchCriteria $searchCriteria */
            $searchCriteria = Base::getEntityObject('SearchCriteria');
            $searchCriteria->fromCityId = $nextSchedule->busRoute->fromCity->id;
            $searchCriteria->toCityId = $nextSchedule->busRoute->toCity->id;
            $searchCriteria->clientId = User::DEFAULT_USER_ID;
            $searchCriteria->roundTrip = false;
            $searchCriteria->discountCode = "";
            $searchCriteria->source = Criteria::SOURCE_CONDUCTOR_APP; //Company Code _ WEB (BBK_WEB), (BBK_CND), (BBK_TBX)
            $searchCriteria->ip = $this->getRequest()->getServer('REMOTE_ADDR');

            /** @var \Api\Client\Soap\Core\LegCriteria $legCriteria */
            $legCriteria = Base::getEntityObject('LegCriteria');
            //create bus filter
            /** @var \Api\Client\Soap\Core\BusFilter $busFilter */
            $busFilter = Base::getEntityObject('BusFilter');
            $busFilter->busId = $busId;
            //filter based on bus
            $legCriteria->filters = array(
                $busFilter
            );
            
            //set current date for departure date
            $currentTime = new ExprDateTime();
            //departure timestamp is 1 hour before bus leaves
            $departureTimestamp = $currentTime->sub(new \DateInterval('PT1H'))->getTimestamp();
            $legCriteria->departureTimestamp = $nextSchedule->departureTime - 100000; //convert into miliseconds
            //departure time end timestamp to 8 hours
            $departureEnd = (new ExprDateTime())->add(new \DateInterval('PT8H'))->getTimestamp();
            //$legCriteria->departureTimestampEnd = $departureEnd * 1000; //convert into miliseconds

            $searchCriteria->legCriterion = array(
                $legCriteria
            );

            /**
             * Create session
             * @var $oSession \Api\Manager\Session
             */
            $oSession = $this->getServiceLocator()->get('Api\Manager\Session');
            //create a session
            $session = $oSession->create();

            /**
             * Do a search based on filters
             * @var $search \Api\Manager\Schedule\Search
             */
            $search = $this->getServiceLocator()->get('Api\Manager\Schedule\Search');
            $search->setSession($session);
            $schedulesForBus = $search->doSearch($searchCriteria);
            //get oneway first result
            $aSchedule = array_pop($schedulesForBus['oneway']);
            //if search result is not available write to error log
            if(!$aSchedule){
                (new \Application\Util\Log())->emerg('Conductor schedule failed for - '. $username, [
                    'scheduleObject' => $aSchedule,
                    'searchCriteria' => $searchCriteria,                    
                ]);
            }

            /**
             * loop through and save price in session
             * @note this is an utterly stupid way of keeping price. The API doesnt yet support
             *       so there is no alternative until the API can handle prices on it's booking function
             */
            $busPrices = array();
            $busPrices[ $aSchedule['resultIndex'] ] = array(
                'price' => $aSchedule['prices'],
                'departureTime' => $aSchedule['actualDepartureTime']
            );
            //$this->busResultPrices->busPrices = $busPrices;
            $this->getCache()->setItem($this->_cacheResultPrices . $session, $busPrices);

            /**
             * get seat layout
             */
            $busRouteInfo = $aSchedule['sectors'][0]['busRoute'];
            //validate
            if(empty($busRouteInfo['boardingStops']) || empty($busRouteInfo['dropStops'])){
                throw new \Exception('Boarding location failed.');
            }

            $boardingLocation = $busRouteInfo['boardingStops'][0]['id'];
            $dropoffLocation  = $busRouteInfo['dropStops'][0]['id'];
            $adults     = 1;
            $busTypeId  = $aSchedule['sectors'][0]['bus']['busTypeId'];
            $children   = 0;
            $infants    = 0;
            $scheduleId = $aSchedule['sectors'][0]['scheduleId'];

            //clear booking data
            /** @var \App\Ticketing\Booking $oBooking */
            $oBooking = $this->getServiceLocator()->get('App\Ticketing\V1\Booking');
            $oBooking->init($scheduleId);

            /**
             * @var $scheduleAvailability Availability
             */
            $scheduleAvailability = $this->getServiceLocator()->get('App\Ticketing\V1\Schedule\Availability');
            $seats = $scheduleAvailability->getSeatAvailability($session, $boardingLocation, $dropoffLocation, $adults, $busTypeId, $children, $infants, $scheduleId);
            //append seat result to schedule object
            $aSchedule['seatLayout'] = array(
                'seat_count' => count($seats),
                'seats' => $seats
            );

            //get all agents
            $aSchedule['agents'] = $this->getAgents();

            $translator = $this->getServiceLocator()->get('translator');
            //get busstops
            $busStops = array();
            foreach ((Util::sortByKey($nextSchedule->scheduleStops, 'idx')) as $busScheduleBusStop){
                /**
                 * @todo skip the bus stop if the city is same as schedule's to city
                 */
                $busStops[] = array(
                    'id' => $busScheduleBusStop['stop']['id'],
                    'name' => $translator->translate($busScheduleBusStop['stop']['name'])
                );
            }
            //sort by idx to get stop list in ascending order
            $aSchedule['stops'] = $busStops;

            //set response
            $this->addSuccess(array(
                'result'=> $aSchedule,
                'session' => $session,
            ));
        } catch (\Exception $e) {
            $this->addError($e);
        }

        return $this->getJsonResponse();

    }

    public function seatinfoAction()
    {
        try {
            $reqestData = $this->params()->fromJson();
            $sl         = $this->getServiceLocator();
            $username = $reqestData['username'];
            $token = $reqestData['token'];

            //authenticate user
            $this->isAuthorized($username, $token);

            $boardingLocation = $reqestData['boardingLocation'];
            $dropoffLocation  = $reqestData['dropoffLocation'];
            $adults     = 1;
            $busTypeId  = $reqestData['busType'];
            $children   = 0;
            $infants    = 0;
            $scheduleId = $reqestData['scheduleId'];
            $session    = $reqestData['session'];

            /**
             * @var $scheduleAvailability Availability
             */
            $scheduleAvailability = $sl->get('App\Ticketing\V1\Schedule\Availability');
            $seats = $scheduleAvailability->getSeatAvailability($session, $boardingLocation, $dropoffLocation, $adults, $busTypeId, $children, $infants, $scheduleId);

            //set response
            $this->addSuccess(array(
                'result' => array(
                    'seat_count' => count($seats),
                    'seats' => $seats
                ),
            ));
        } catch (\Exception $e) {
            $this->addError($e);
        }

        return $this->getJsonResponse();
    }

    public function getagentsAction()
    {
        try{
            $reqestData = $this->params()->fromJson();
            $username = $reqestData['username'];
            $token = $reqestData['token'];

            //authenticate user
            $this->isAuthorized($username, $token);

            $result = $this->getAgents();
            //set response
            $this->addSuccess($result);
        }catch (\Exception $e){
            $this->addError($e);
        }

        return $this->getJsonResponse();
    }

    public function collectionAction()
    {
        try{
            $params = $this->params()->fromJson();
            $username = $params['username'];
            $token = $params['token'];
            $scheduleId = $params['scid'];

            //authenticate user
            $this->isAuthorized($username, $token);

            //get seat pricing
            /** @var Schedule $oScheduleManager */
            $oScheduleManager = $this->getServiceLocator()->get('App\Ticketing\V1\Schedule');
            $result = $oScheduleManager->getConductorCollection($scheduleId);
            //set response
            $this->addSuccess($result);
        }catch (\Exception $e){
            $this->addError($e);
        }

        return $this->getJsonResponse();

    }

    public function placebookingAction()
    {
        $result = array();
        $heldItemIndex = null;
        try {
            $reqestData = $this->params()->fromJson();
            $sl = $this->getServiceLocator();
            $auth = $this->getServiceLocator()->get('AuthService');

            $sessionId      = $reqestData['session'];
            $fromCity       = $reqestData['fromCity'];
            $toCity         = $reqestData['toCity'];
            $resultIndex    = $reqestData['resultIndex'];
            $bookingData    = $reqestData['bookingData'];

            $passengerData  = $bookingData['passenger'];
            $contactData    = $bookingData['contact'];
            $paymentType    = $bookingData['type'];
            $alertCustomer  = false;
            $paymentData    = $bookingData['payment'];

            $username = $reqestData['username'];
            $token = $reqestData['token'];

            //authenticate user
            $this->isAuthorized($username, $token);

            $success = false;
            //get from cache
            $busResultPrices = $this->getCache()->getItem($this->_cacheResultPrices . $sessionId, $success);
            //validate
            if(!$success){
                throw new SessionTimeoutException('The session has expired. You have to go back and re do the process.');
            }

            /**
             * get price
             */
            $busPrices = $busResultPrices[$resultIndex]['price'];
            $perSeatAmount = floatval($busPrices['total']);
            $perSeatPayAmount = floatval($busPrices['priceBeforeCharges']);
            //get departure time from session
            if($busResultPrices[$resultIndex]['departureTime']){
                $departureDate = ExprDateTime::getDateFromString($busResultPrices[$resultIndex]['departureTime']);
            }else{
                throw new \Exception('Departure time is not set from schedule.');
            }

            $seats = array();
            $amount = 0;
            $payAmount = 0;
            //loop through to filter data
            foreach($passengerData as $passenger){
                $seats[] = $passenger["seatNo"];
                //add to total amount
                $amount += $perSeatAmount;
                $payAmount += $perSeatPayAmount;
            }

            $paymentMethodCriteria = null;
            $oPaymentcriteria = null;
            $agentId = null;
            $smsTemplate = Template::CONDUCTOR_BOOKING_SUCCESS;
            // payment
            switch($paymentType){
                case self::PAY_TYPE_CASH:
                    /** @var PaymentRefundCriteria $oPaymentcriteria */
                    $oPaymentcriteria = Base::getEntityObject('PaymentRefundCriteria');
                    $oPaymentcriteria->amount = $payAmount;
                    $oPaymentcriteria->actualAmount = $payAmount;
                    $oPaymentcriteria->actualCurrency = 'LKR';
                    $oPaymentcriteria->type = PaymentRefundType::Payment;
                    /**
                     * set vendor mode to vendor type. If the app changes,
                     * this needs to be null if the conductor app's money comes to BBK
                     */
                    $oPaymentcriteria->mode = PaymentRefundMode::Vendor;
                    $oPaymentcriteria->vendorMode = VendorPaymentRefundMode::Cash;
                    //alert customer if phone number exists
                    $alertCustomer = true;
                    break;

                case self::PAY_TYPE_AGENTS:
                    $reference      = null;
                    if (!empty($paymentData['agent']) && !empty($paymentData['agent']['id'])) {
                        $agentId  = $bookingData['payment']['agent']['id'];
                    }else{
                        throw new \Exception('Please select an Agent');
                    }
                    /** @var PaymentRefundCriteria $oPaymentcriteria */
                    $oPaymentcriteria = Base::getEntityObject('PaymentRefundCriteria');
                    $oPaymentcriteria->amount = $payAmount;
                    $oPaymentcriteria->actualAmount = $payAmount;
                    $oPaymentcriteria->actualCurrency = 'LKR';
                    $oPaymentcriteria->type = PaymentRefundType::Payment;
                    /**
                     * set vendor mode to vendor type. If the app changes,
                     * this needs to be null if the conductor app's money comes to BBK
                     */
                    $oPaymentcriteria->mode = PaymentRefundMode::Vendor;
                    $oPaymentcriteria->vendorMode = VendorPaymentRefundMode::Cash;
                    //disable sms alert.
                    $alertCustomer = false;
                    break;

                case self::PAY_TYPE_ATBUS:
                    /** @var PaymentMethodCriteria $paymentMethodCriteria */
                    // $paymentMethodCriteria = Base::getEntityObject('PaymentMethodCriteria');
                    // $paymentMethodCriteria->paymentMethod = PaymentMethod::CashAtBus;
					$oPaymentcriteria = Base::getEntityObject('PaymentRefundCriteria');
                    $oPaymentcriteria->amount = $payAmount;
                    $oPaymentcriteria->actualAmount = $payAmount;
                    $oPaymentcriteria->actualCurrency = 'LKR';
                    $oPaymentcriteria->type = PaymentRefundType::Payment;
                    /**
                     * set vendor mode to vendor type. If the app changes,
                     * this needs to be null if the conductor app's money comes to BBK
                     */
                    $oPaymentcriteria->mode = PaymentRefundMode::Vendor;
                    $oPaymentcriteria->vendorMode = VendorPaymentRefundMode::PayAtBus;
                    $alertCustomer = true;
                    break;

                default:
                    throw new \Exception('Invalid payment method');
            }

            /**
             * @var $oHoldSeats \Api\Manager\Schedule\Hold
             */
            $oHoldSeats = $sl->get('Api\Manager\Schedule\Hold');
            //execute hold call
            $oHoldSeats->setSession($sessionId);
            $holdResponse = $oHoldSeats->executeHold($bookingData['boardingLocation'], $bookingData['dropoffLocation'], $resultIndex, $seats);
            //var_dump($holdResponse);
            $heldItemIndex = $holdResponse->heldItemIds;

            /**
             * Confirm booking
             * @var $oConfirmation \Api\Manager\Booking\ConfirmBooking
             */
            $oConfirmation = $sl->get('Api\Manager\Booking\ConfirmBooking');
            $oConfirmation->setSession($sessionId);
            $oConfirmation->setLoggedInUserId($auth->getIdentity()->id);
            $oconfirmResponse = $oConfirmation->confirm($heldItemIndex, $bookingData, $oPaymentcriteria, $paymentMethodCriteria, $agentId);
            //set booking reference as transaction id
            $transactionId = $oconfirmResponse->reference;

            $result['ticketData'] = $this->getTicketInfo($contactData, $oconfirmResponse, $alertCustomer, $smsTemplate);

            $this->addSuccess($result);
        } catch (\Exception $e) {
            $this->addError($e);
            $error = '';
            //if it's already holded, release them
            if($heldItemIndex){
                try {
                    /**
                     * @var $oHoldSeats \Api\Manager\Schedule\Hold
                     */
                    $oHoldSeats = $this->getServiceLocator()->get('Api\Manager\Schedule\Hold');
                    $oHoldSeats->releaseHolded($sessionId, $heldItemIndex);
                } catch (\Exception $e) {
                    $error = $error . ' - ' . $e->getMessage();
                }
            }
        }

        return $this->getJsonResponse();
    }

    private function getTicketInfo($contactData, \Api\Client\Soap\Core\Booking $oconfirmResponse, $alertCustomer, $smsTemplate)
    {
        $response = array();
        $bookingNo      = $oconfirmResponse->reference;

        /**
         * Send SMS only if alert customer checkbox is checked
         */
        try {
            //send sms if only the phone number exists
            if($alertCustomer && isset($contactData['mobileNo']) && !empty($contactData['mobileNo'])){
                /**
                 * get sms object
                 * @var $oSms \Application\Alert\Sms
                 */
                $oSms = $this->getServiceLocator()->get('Alert\Sms');
                $oSms->sendBookingSms($oconfirmResponse, $smsTemplate, $contactData['mobileNo']);
            }
        } catch (\Exception $e) {
            $alertBody = '
                <h2>SMS failed due to</h2><pre>'. $e->getMessage() .'</pre>
                <h2>SMS failed to</h2><pre>'. $contactData['mobileNo'] .'</pre>
                <h2>Booking Reference</h2><pre>'. $bookingNo .'</pre>
                ';
            $oAlert = $this->getServiceLocator()->get('Application\Util\Alert');
            $oAlert->sendEmailToAdmin($alertBody, 'ExpressBot - Sending sms failed on Conductor App');
        }

        try {
            //send email if the address is available
            if(!empty($oconfirmResponse->client->email)){
                /**
                 * Send email
                 * @var $oEmail \Application\Alert\Email
                 */
                $oEmail = $this->getServiceLocator()->get('Alert\Email');
                $oEmail->sendBookingSuccessEmail($oconfirmResponse);
            }
        } catch (\Exception $e) {
            throw $e;
        }

        return true;
    }

    /**
     * @return array|object
     */
    private function getCache()
    {
        return $this->getServiceLocator()->get('cache');
    }

    /**
     * @return mixed
     */
    private function getAgents()
    {
        $agent = new RestFactory($this->getServiceLocator(), 'agent');
        //build criteria
        $criteria = new QueryCriteria();
        $criteria->setLoadAll();
        $agents = $agent->getList($criteria);

        $result = $agents->body;
        foreach ($result as $key => $ag) {
            $ag->color = ''. Util::getColorByNumber($ag->id) .'';
            $result[$key] = array(
                'name' => $ag->name,
                'id' => $ag->id,
                'color' => $ag->color,
            );
        }
        return $result;
    }
}