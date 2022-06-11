<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 4/13/14
 * Time: 10:44 PM
 */

namespace Application\Controller;

use Api\Client\Rest\Model\City;
use Api\Client\Soap\Core\PaymentRefundMode;
use Application\Acl\Acl;
use Application\Domain;
use Application\Helper\ExprDateTime;
use Application\Model\User;
use Application\Payment\Gateway\DialogEzCash;
use Application\Payment\Gateway\MobitelMCash;
use Application\Payment\Gateway\PayPalExpressCheckout;
use Application\Util\Alert;
use Data\Manager\WaitingList;
use Zend\Mvc\Controller\AbstractActionController;
use Zend\View\Model;
use Zend\Session\Container;
use Application\Controller\Plugin;
use Api\Manager\Base;
use Api\Client\Soap\Core\PaymentRefundCriteria;
use Api\Client\Soap\Core\PaymentRefundType;
use Api\Client\Soap\Core\VendorPaymentRefundMode;


class SearchController extends AbstractActionController
{
    /**
     * @var \Zend\Session\AbstractContainer
     */
    private $busResultPrices;

    private $_cacheResultPrices = 'cached-result-prices-search';

    public function __construct()
    {
        $this->busResultPrices = new Container();
    }
    public function searchAction()
    {
        $result = array();
        try {
            $json       = $this->params()->fromJson();
            $iFromId    = $json['from'];
            $iToId      = $json['to'];
            $sDate      = $json['date'];
            $sFromName  = $json['fromName'];
            $sToName    = $json['toName'];
            $sDiscountCode    = $json['offercode'];
            $sl         = $this->getServiceLocator();

            /**
             * @var $oSession \Api\Manager\Session
             */
            $oSession = $sl->get('Api\Manager\Session');
            //create a session
            $session = $oSession->create();
            //$session = $oSession->testCreate();

            $auth = $this->getServiceLocator()->get('AuthService');
            $loggedInUser = $auth->getIdentity() ? $auth->getIdentity()->id : User::DEFAULT_USER_ID;

            //validate date
            $oDate = \DateTime::createFromFormat('Y-m-d', $json['date']);
            if ($oDate === false){
                throw new \Exception('Incorrect date. Please check and try again.');
            }
            $oDate->setTime(0, 0);
            $oToday = new \DateTime();
            $oToday->setTime(0, 0);
            //check for less than today
            if($oDate < $oToday){
                throw new \Exception('Incorrect date range. Please check and try again.');
            }
            //check for more than 3 months
            $oToday->add(new \DateInterval('P2M'));
            if($oDate > $oToday){
                throw new \Exception('Incorrect max date range. Please check and try again.');
            }

            /**
             * @var $search \Api\Manager\Schedule\Search
             */
            $search = $sl->get('Api\Manager\Schedule\Search');
            $search->setSession($session);
            $search->setLoggedInUserId($loggedInUser);
            $search->setDiscountCode($sDiscountCode);
            $searchResponse = $search->getResult($iFromId, $iToId, false, $sDate, $sFromName, $sToName);
			
            //check if the result is suggested

            /**
             * loop through and save price in session
             */
            $busPrices = array();
            $aSuggestedResult = false;
            foreach($searchResponse['oneway'] as $forwardLeg){
                $scheduleId = count($forwardLeg['sectors'])>0 ? $forwardLeg['sectors'][0]['scheduleId'] : 0;

                /**
                 * Save Price
                 */
                $busPrices[ $forwardLeg['resultIndex'] ] = array(
                    'price' => $forwardLeg['prices'],
                    'scheduleId' => $scheduleId,
                );

                /**
                 * Check if result city names are equal to search city. If not this is suggested
                 */
                $sResultFromName = $forwardLeg['sectors'][0]['fromCityName'];
                $sResultToName = $forwardLeg['sectors'][0]['toCityName'];
                //goes through this only once
                if($aSuggestedResult===false &&
                    strtolower($sResultFromName)!=strtolower($sFromName) || strtolower($sResultToName)!=strtolower($sToName)){
                    $aSuggestedResult = array(
                        'fromId'        => $forwardLeg['sectors'][0]['fromCityId'],
                        'fromName'      => $sResultFromName,
                        'fromChanged'   => (strtolower($sResultFromName)!=strtolower($sFromName)),
                        'toId'          => $forwardLeg['sectors'][0]['toCityId'],
                        'toName'        => $sResultToName,
                        'toChanged'     => (strtolower($sResultToName)!=strtolower($sToName)),
                    );
                }
            }

            $this->getCache()->setItem($this->_cacheResultPrices . $session, $busPrices);

            $result = array(
                'result'=> $searchResponse,
                'total'=> count($searchResponse['oneway']),
                'suggested'=> $aSuggestedResult,
                'session' => $session,
				'validCoupon'=>'' //$searchResponse['validCoupon']
            );
        } catch (\Exception $e) {
            $result['error'] = $e->getMessage();
        }finally{
			$this->logSearchResultStatus($sDate,$sFromName,$sToName,$iFromId, $iToId,count($searchResponse['oneway']));
		}
        return new Model\JsonModel($result);
    }

    public function seatinfoAction()
    {
        $result = array();
        try {
            $reqestData = $this->params()->fromJson();
            $sl         = $this->getServiceLocator();

            $boardingLocation = $reqestData['boardingLocation'];
            $dropoffLocation  = $reqestData['dropoffLocation'];
            $adults     = 1;
            $busTypeId  = $reqestData['busType'];
            $children   = 0;
            $infants    = 0;
            $scheduleId = $reqestData['scheduleId'];

            /**
             * @var $scheduleAvailability \Api\Manager\Schedule\Availability
             */
            $scheduleAvailability = $sl->get('Api\Manager\Schedule\Availability');

            $scheduleAvailability->setSession($reqestData['session']);
            $seats = $scheduleAvailability->getResult($boardingLocation, $dropoffLocation, $adults, $busTypeId, $children, $infants, $scheduleId);
            //$seats = $scheduleAvailability->testGetResult();

            $result = array(
                'result' => array(
                    'seat_count' => count($seats),
                    'seats' => $seats
                ),
            );
        } catch (\Exception $e) {
            $result['error'] = $e->getMessage();
        }
        return new Model\JsonModel($result);
    }

    public function placebookingAction()
    {
        $result = array();
        try {
            $reqestData = $this->params()->fromJson();
            $sl = $this->getServiceLocator();
            $auth = $this->getServiceLocator()->get('AuthService');
            $config = $sl->get('Config');

            $sessionId      = $reqestData['session'];
            $fromCity       = $reqestData['fromCity'];
            $toCity         = $reqestData['toCity'];
            $resultIndex    = $reqestData['resultIndex'];
            $bookingData    = $reqestData['bookingData'];
            $isMobile       = (isset($reqestData['isMobile']) ? $reqestData['isMobile'] : false);

            $passengerData  = $bookingData['passenger'];
            $contactData    = $bookingData['contact'];
            $paymentType    = $bookingData['type'];

            $success = false;
            //get from cache
            $busResultPrices = $this->getCache()->getItem($this->_cacheResultPrices . $sessionId, $success);
            //validate
            if(!$success){
                throw new \Exception('The session has expired. You have to go back and re do the process.');
            }
            //check if temporary data exists
            if(!isset($busResultPrices[$resultIndex])){
                throw new \Exception('Incorrect Session data. Please refresh the web page.');
            }

            //get schedule object
            $scheduleId = $busResultPrices[$resultIndex]['scheduleId'];
            /** @var \Api\Manager\Schedule $scheduleManager */
            $scheduleManager = $this->getServiceLocator()->get('Api\Manager\Schedule');
            //get schedule
            $currentSchedule = $scheduleManager->fetch($scheduleId);
            //check if booking time has passed
            if($scheduleManager->hasWebBookingEnded($currentSchedule)){
                throw new \Exception('Booking time expired. Bookings has been closed for this bus.');
            }

            /**
             * get price
             */
            $busPrices = $busResultPrices[$resultIndex]['price'];
            $perSeatAmount = floatval($busPrices['total']);
            $perSeatPayAmount = floatval($busPrices['priceBeforeCharges']);
			$busCharges = floatval($busPrices['baseCharges']);

            $seats = array();
            $amount = 0;
            $payAmount = 0;
            //loop through to filter data
            foreach($passengerData as $passenger){
                $seats[] = $passenger["seatNo"];
                //add to total amount
                $amount += $perSeatAmount;
                $payAmount += ($perSeatPayAmount+$busCharges) ;
            }
	
            //switch payment type
            switch($paymentType){
                case 'ez_cash':
                    $paymentMode = PaymentRefundMode::eZCash;
                    break;
                case 'm_cash':
                    $paymentMode = PaymentRefundMode::mCash;
                    break;
                case 'credit_card':
                    $paymentMode = PaymentRefundMode::Card;
                    break;
				case 'payhere':
                    $paymentMode = PaymentRefundMode::Card;
                    break;
                case 'paypal':
                    $paymentMode = PaymentRefundMode::PayPal;
                    break;
                default:
                    throw new \Exception('Invalid payment type. Check the payment type and try again.');
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

            $loggedInUser = $auth->getIdentity() ? $auth->getIdentity()->id : User::DEFAULT_USER_ID;
            /**
             * Confirm booking
             * @var $oConfirmation \Api\Manager\Booking\ConfirmBooking
             */
            $oConfirmation = $sl->get('Api\Manager\Booking\ConfirmBooking');
            $oConfirmation->setSession($sessionId);
            $oConfirmation->setLoggedInUserId($loggedInUser);
			
			$oconfirmResponse = $oConfirmation->confirm($heldItemIndex, $bookingData);
            //set booking reference as transaction id
            $transactionId = $oconfirmResponse->reference;
			
			
            //save booking reference
            $uri = $this->getRequest()->getUri();
            $base = sprintf('%s://%s', $uri->getScheme(), $uri->getHost());
            /* @var $oDataStore \Zend\Cache\Storage\StorageInterface */
            $oDataStore = $this->getServiceLocator()->get('DataStore');
            $oDataStore->setItem('IPG_REF_DOMAIN_'. $transactionId, $base);

            /**
             * Check payment method and send encrypted data to send to the IPG
             */
            //dialog ezcash
            if($paymentType=='ez_cash'){
                /** @var DialogEzCash $oDialog */
                $oDialog = $this->getServiceLocator()->get('Payment\DialogEzCash');
                $oIpgRequest = $oDialog->getEncryptedRequest($transactionId, $payAmount);
                $result['IPGForm'] = $oIpgRequest->getRequestPayload();
                $result['IPGtype'] = $oIpgRequest->getRequestProcessType();
            }
            //mcash
            else if($paymentType=='m_cash'){
                /** @var MobitelMCash $oMobitel */
                $oMobitel = $this->getServiceLocator()->get('Payment\MobitelMCash');
                $oIpgRequest = $oMobitel->getEncryptedRequest($transactionId, $payAmount);
                $result['IPGForm'] = $oIpgRequest->getRequestPayload();
                $result['IPGtype'] = $oIpgRequest->getRequestProcessType();
            }
            //credit card
            else if($paymentType=='credit_card'){
                $sPayDescription = 'Payment for booking Seats at '. Domain::getDomain();
				$currentDomainName = Domain::getDomain();
                if(false){
                    //Old IPG
                    /** @var \Application\Payment\Gateway\SampathCcV1 $oSampath */
                    $oSampath = $this->getServiceLocator()->get('Payment\SampathCcV1');
                    $oIpgRequest = $oSampath->getEncryptedRequest($transactionId, $payAmount, $sPayDescription);
                }elseif ($currentDomainName==\Application\Domain::NAME_SLTB){
					$oPeopleIPG = $this->getServiceLocator()->get('Payment\PeopleIPG');
                    $oIpgRequest = $oPeopleIPG->getEncryptedRequest($transactionId, $payAmount, $sPayDescription);
						
				} else {
					//new IPG
					/** @var \Application\Payment\Gateway\SampathCcV2 $oSampathV2 */
					$oSampathV2 = $this->getServiceLocator()->get('Payment\SampathCcV2');
					$oIpgRequest = $oSampathV2->getEncryptedRequest($transactionId, $payAmount, $sPayDescription);
					//hnb
					/** @var \Application\Payment\Gateway\HNBipg $oHnb */
//                    $oHnb = $this->getServiceLocator()->get('Payment\Hnb');
//                    $oIpgRequest = $oHnb->getEncryptedRequest($transactionId, $payAmount, $sPayDescription);
				}

                $result['IPGForm'] = $oIpgRequest->getRequestPayload();
                $result['IPGtype'] = $oIpgRequest->getRequestProcessType();
            }else if($paymentType=='ndb_card'){
				
				
				$sPayDescription = 'Payment for booking Seats at '. Domain::getDomain();
                if(false){
                    //Old IPG
                    /** @var \Application\Payment\Gateway\SampathCcV1 $oSampath */
                    $oSampath = $this->getServiceLocator()->get('Payment\SampathCcV1');
                    $oIpgRequest = $oSampath->getEncryptedRequest($transactionId, round($payAmount*0.9,2), $sPayDescription);
                }else{
                    //new IPG
                    /** @var \Application\Payment\Gateway\SampathCcV2 $oSampathV2 */
                    $oSampathV2 = $this->getServiceLocator()->get('Payment\SampathCcV2');
                    $oIpgRequest = $oSampathV2->getEncryptedRequest($transactionId, round($payAmount*0.9,2), $sPayDescription,true);
						
				   //hnb
                    /** @var \Application\Payment\Gateway\HNBipg $oHnb */
//                    $oHnb = $this->getServiceLocator()->get('Payment\Hnb');
//                    $oIpgRequest = $oHnb->getEncryptedRequest($transactionId, $payAmount, $sPayDescription);
                }
				
                $result['IPGForm'] = $oIpgRequest->getRequestPayload();
                $result['IPGtype'] = $oIpgRequest->getRequestProcessType();
			}
            // paypal
            else if ($paymentType == 'paypal') {
                /** @var PayPalExpressCheckout $oPayPal */
                $oPayPal = $this->getServiceLocator()->get('Payment\PayPalExpressCheckout');
                $oIpgRequest = $oPayPal->getEncryptedRequest($transactionId, $payAmount);
                $result['IPGForm'] = $oIpgRequest->getRequestPayload();
                $result['IPGtype'] = $oIpgRequest->getRequestProcessType();
            } else {
                throw new \Exception('No Payment method defined. Please select a payment method');
            }

            //send email to admin about going to payment gateway
            try {
                //check if customer is one of the admins
                if(!in_array(strtoupper($oconfirmResponse->client->nic), Alert::$skipNICs)){
                    $oAlert = $this->getServiceLocator()->get('Application\Util\Alert');
                    $alertData = array();
                    $bkIt = is_array($oconfirmResponse->bookingItems)? $oconfirmResponse->bookingItems : array($oconfirmResponse->bookingItems);
                    foreach ($bkIt as $bki) {
                        /** @var $bki \Api\Client\Soap\Core\BookingItem */
                        $alertData[] = array(
                            'from' => $bki->fromBusStop->name,
                            'to' => $bki->toBusStop->name,
                            'Departure Date' => $bki->schedule->departureTime,
                            'Bus' => $bki->schedule->bus->name .' - '. $bki->schedule->bus->plateNumber,
                        );
                    }
                    $oAlert->sendEmailToAdmin('
                    <h2>Payer Data</h2><pre>'. var_export(array(
                        'sessionId'     => $sessionId,
                        'customer IP'   => $this->getServiceLocator()->get('Request')->getServer('REMOTE_ADDR'),
                        'contactData'   => $contactData,
                        'passengerData' => $passengerData,
                        'amount'        => $amount,
                        'payAmount'     => $payAmount,
                        'paymentType'   => $paymentMode,
                        'bookingNo'          => $oconfirmResponse->reference,
                        'bookingDate'   => $oconfirmResponse->bookingTime,
                        'journey'          => $alertData,
                    ), true) .'</pre>
                ', 'ExpressBot : '. $paymentMode .' Payment - '. $oconfirmResponse->reference);
                }

            } catch (\Exception $e) {

            }

        } catch (\Exception $e) {
            $result['error'] = $e->getMessage();
            //if it's already holded, release them
            if($heldItemIndex){
                try {
                    /**
                     * @var $oHoldSeats \Api\Manager\Schedule\Hold
                     */
                    $oHoldSeats = $this->getServiceLocator()->get('Api\Manager\Schedule\Hold');
                    $oHoldSeats->releaseHolded($sessionId, $heldItemIndex);
                } catch (\Exception $e) {
                    $result['error'] = $result['error'] . ' - ' . $e->getMessage();
                }
            }
        }

        return new Model\JsonModel($result);
    }

    public function getdestinationsAction()
    {
        $result = array();

        try{
            $reqestData = $this->params()->fromJson();

            $oCities = new City($this->getServiceLocator());
            $oCities->setShouldTranslate(false);
            $destinations = $oCities->getTerminusCityList($reqestData['cityId']);

            $result['destinations'] = $destinations;
        }catch (\Exception $e){
            $result['error'] = $e->getMessage();
        }

        return new Model\JsonModel($result);
    }

    public function getcitylistAction()
    {
        $result = array();

        try{
            //get city list from service
            $oCities = new City($this->getServiceLocator());
            $oCities->setShouldTranslate(false);
            $aCityList = $oCities->getTerminusCityList();

            $result['cities'] = $aCityList;
        }catch (\Exception $e){
            $result['error'] = $e->getMessage();
        }

        return new Model\JsonModel($result);
    }

    public function getcitysuggestionAction()
    {
        $aResult = array();
        $reqestData = $this->params()->fromJson();

        try {
            $sTerm = $reqestData['term'];
            //get city list from service
            $oCities = new City($this->getServiceLocator());
            $aCityList = array();

            if ($reqestData['type'] == 'dest') {
                //if destination is selected
                $sOrigin = $reqestData['origin'];
                $aResult = $oCities->getDestinationsBySuggestion($sOrigin, $sTerm);
            } else {
                $aResult = $oCities->getCitiesBySuggestion($sTerm);
            }
        } catch (\Exception $e) {
            //$result['error'] = $e->getMessage();
        }

        return new Model\JsonModel($aResult);
    }

    /**
     * Waiting list
     * @return Model\JsonModel
     */
    public function waitinglistAction()
    {
        $aResult = array();
        $reqestData = $this->params()->fromJson();

        try {
            $busScheduleId = $reqestData['scid'];
            $name = $reqestData['name'];
            $email = $reqestData['email'];
            $phone = $reqestData['phonenumber'];
            $nic = isset($reqestData['nic'])? $reqestData['nic'] : null;
            $seats = isset($reqestData['seats'])? $reqestData['seats'] : 1;
            if (isset($_SERVER['HTTP_X_FORWARDED_FOR']) && $_SERVER['HTTP_X_FORWARDED_FOR']) {
                $ip = $_SERVER['HTTP_X_FORWARDED_FOR'];
            } else {
                $ip = $_SERVER['REMOTE_ADDR'];
            }

            /** @var WaitingList $waitingList */
            $waitingList = $this->getServiceLocator()->get('Data\Manager\WaitingList');
            $waitingList->saveWaitingList($busScheduleId, $name, $email, $phone, $nic, $seats, $ip);

            $aResult['success'] = true;

            try {
                /** @var Alert $oAlert */
                $oAlert = $this->getServiceLocator()->get('Application\Util\Alert');
                /** @var \Api\Manager\Schedule $scheduleManager */
                $scheduleManager = $this->getServiceLocator()->get('Api\Manager\Schedule');
                //get schedule
                $currentSchedule = $scheduleManager->fetch($busScheduleId);
                $scName = $currentSchedule->busRoute->name . '-'. $currentSchedule->bus->plateNumber .'@'. ExprDateTime::getDateFromServices($currentSchedule->departureTime)->format('Y-m-d H:i:s');
                $oAlert->sendEmailToAdmin('<pre>'. var_export($reqestData, true) .'</pre>', 'Waitiling list bot for: ' . $scName);
            } catch (\Exception $e) {
            }

        } catch (\Exception $e) {
            $aResult['error'] = $e->getMessage();
        }

        return new Model\JsonModel($aResult);
    }

    /**
     * @return array|object
     */
    private function getCache()
    {
        return $this->getServiceLocator()->get('cache');
    }
	private function logSearchResultStatus($searchDate,$searchFromCity,$searchToCity,$searchFromCityId,$searchToCityId,$searchResultCount){
		$config = $this->getServiceLocator()->get('Config');
		$domain = $config['system']['siteName'];
		$resultSearchResultLog = $this->getServiceLocator()->get('Data\Manager\WebResultSearchResultLog');
		$resultSearchResultLog->add($searchDate,$searchFromCity,$searchToCity,$searchFromCityId,$searchToCityId,$searchResultCount,$domain);
	}
}