<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 10/14/14
 * Time: 8:04 AM
 */

namespace Admin\Controller;

use Api\Client\Rest\Factory as RestFactory;
use Api\Client\Rest\Model\City;
use Api\Client\Soap\Core\BusSchedule;
use Api\Client\Soap\Core\CancellationCause;
use Api\Client\Soap\Core\LegCriteria;
use Api\Client\Soap\Core\PaymentMethod;
use Api\Client\Soap\Core\PaymentMethodCriteria;
use Api\Client\Soap\Core\PaymentRefundMode;
use Api\Client\Soap\Core\PaymentRefundCriteria;
use Api\Client\Soap\Core\PaymentRefundType;
use Api\Client\Soap\Core\SearchCriteria;
use Api\Client\Soap\Core\VendorPaymentRefundMode;
use Api\Manager\Base;
use Api\Manager\Booking;
use Api\Operation\Request\QueryCriteria;
use Application\Domain;
use Application\Helper\ExprDateTime;
use Application\Sms\Template;
use Application\Util;
use Zend\Mvc\Controller\AbstractActionController;
use Zend\View\Model\JsonModel;
use Zend\Session\Container;
use Zend\View\Model\ViewModel;
use Application\Model\User;

use Data\Manager\SmsQueue;

class TicketboxAjaxController extends AbstractActionController
{
    const PAY_TYPE_CASH = 'Cash';
    const PAY_TYPE_AGENTS = 'agents';
    const PAY_TYPE_ATBUS = 'AtBus';
    const PAY_TYPE_DIFF_PAY = 'diifPay';
    const PAY_TYPE_EZCASH = 'ezCash';
    const PAY_TYPE_MCASH = 'mCash';
    const PAY_TYPE_EZCASH_REF = 'ezCashRef';
    const PAY_TYPE_CARD = 'Card';
    const PAY_TYPE_BANKTRANSFER = 'BankTr';

    /**
     * @var \Zend\Session\AbstractContainer
     */
    private $busResultPrices;

    private $_cacheResultPrices = 'cached-result-prices-search';

    public function __construct()
    {
        $this->busResultPrices = new Container();
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

        return new JsonModel($result);
    }

    public function getcitiesAction()
    {
        $result = array();

        try{
            $oCities = new City($this->getServiceLocator());
            $oCities->setShouldTranslate(false);
            $aCityList = $oCities->getTerminusCityList();

            $result = $aCityList;
        }catch (\Exception $e){
            $result['error'] = $e->getMessage();
        }

        return new JsonModel($result);
    }

    public function getagentsAction()
    {
        $result = array();

        try{
            $agent = new RestFactory($this->getServiceLocator(), 'agent');
            //build criteria
            $criteria = new QueryCriteria();
            $criteria->setLoadAll();
            $agents = $agent->getList($criteria);

            $result = $agents->body;
            foreach ($result as $key => $ag) {
                $ag->color = ''. Util::getColorByNumber($ag->id) .'';
                $result[$key] = $ag;
            }

        }catch (\Exception $e){
            $result['error'] = $e->getMessage();
        }

        return new JsonModel($result);
    }

    public function releaseholdedAction()
    {
        $result = array();

        try{
            $reqestData = $this->params()->fromJson();
            $reference = $reqestData['reference'];

            /**
             * @var $bookingManager \Api\Manager\Booking
             */
            $bookingManager = $this->getServiceLocator()->get('Api\Manager\Booking');
            $oBooking = $bookingManager->getBookingRefById($reference);

            /**
             * get cancellation
             * @var $cancellation \Api\Manager\Booking\Cancellation
             */
            $cancellation = $this->getServiceLocator()->get('Api\Manager\Booking\Cancellation');
            $cancellation->cancelTicket($oBooking->id, false,
                CancellationCause::NonPayment, 'Cancellation due to payment failure by payment gateway.');

            $result['success'] = true;
        }catch (\Exception $e){
            $result['error'] = $e->getMessage();
        }

        return new JsonModel($result);
    }

    public function cancelticketAction()
    {
        $result = array();

        try{
            $reqestData = $this->params()->fromJson();
            $reference = $reqestData['reference'];

            /**
             * @var $bookingManager \Api\Manager\Booking
             */
            $bookingManager = $this->getServiceLocator()->get('Api\Manager\Booking');
            $oBooking = $bookingManager->getBookingRefById($reference);

            /**
             * get cancellation
             * @var $cancellation \Api\Manager\Booking\Cancellation
             */
            $cancellation = $this->getServiceLocator()->get('Api\Manager\Booking\Cancellation');
            $cancellation->cancelTicket($oBooking->id, false,
                CancellationCause::NonPayment, 'Cancellation due to payment failure by payment gateway.');

            $result['success'] = true;
        }catch (\Exception $e){
            $result['error'] = $e->getMessage();
        }

        return new JsonModel($result);
    }

    public function resendsmsAction()
    {
        $result = array();

        try{
            $reqestData = $this->params()->fromJson();
            $reference = $reqestData['reference'];

            /**
             * @var $bookingManager \Api\Manager\Booking
             */
            $bookingManager = $this->getServiceLocator()->get('Api\Manager\Booking');
            $oBooking = $bookingManager->getBookingRefById($reference);
            $template = '';
            if($oBooking->status->code == Booking::STATUS_CODE_CONFIRM){
                $template = Template::BOOKING_SUCCESS;
            }else{
                throw new \Exception('The selected booking is not confirmed');
            }

            /**
             * get sms object
             * @var $oSms \Application\Alert\Sms
             */
            $oSms = $this->getServiceLocator()->get('Alert\Sms');
            $oSms->sendBookingSms($oBooking, $template);

            $result['success'] = true;
            $result['phone'] = $oBooking->client->mobileTelephone;
        }catch (\Exception $e){
            $result['error'] = $e->getMessage();
        }

        return new JsonModel($result);
    }

    public function resendemailAction()
    {
        $result = array();

        try{
            $reqestData = $this->params()->fromJson();
            $reference = $reqestData['reference'];

            /**
             * @var $bookingManager \Api\Manager\Booking
             */
            $bookingManager = $this->getServiceLocator()->get('Api\Manager\Booking');
            $oBooking = $bookingManager->getBookingRefById($reference);
            // $template = '';
            if($oBooking->status->code != Booking::STATUS_CODE_CONFIRM){
               throw new \Exception('The selected booking is not confirmed');
			}
            /**
             * Send email
             * @var $oEmail \Application\Alert\Email
             */
            $oEmail = $this->getServiceLocator()->get('Alert\Email');
            $oEmail->sendBookingSuccessEmail($oBooking);

            $result['success'] = true;
            $result['email'] = $oBooking->client->email;
        }catch (\Exception $e){
            $result['error'] = $e->getMessage();
        }

        return new JsonModel($result);
    }

    public function displayscheduleAction()
    {
        $result = array();
        try {
            $json   = $this->params()->fromJson();
            $sl     = $this->getServiceLocator();
            $from   = $json['from'];
            $to     = $json['to'];
            $date   = $json['date'];

            /**
             * @var $oSession \Api\Manager\Session
             */
            $oSession = $sl->get('Api\Manager\Session');
            //create a session
            $session = $oSession->create();

            $auth = $this->getServiceLocator()->get('AuthService');

            /**
             * @var $search \Api\Manager\Schedule\Search
             */
            $search = $sl->get('Api\Manager\Schedule\Search');
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
            $criteria->ip = $this->getRequest()->getServer('REMOTE_ADDR');

            //create timestamp of the departure date
            $oDepartureDate = ExprDateTime::getDateFromString($date);
            $currentTime = new ExprDateTime();
            if ($oDepartureDate->format('Y-m-d') == $currentTime->format('Y-m-d')) { // compare date parts only
                // if this is today, set current time, so that stale results won't show up
                //$oDepartureDate->setTime($currentTime->format('H'), $currentTime->format('i'), $currentTime->format('s'));
                //Temporarily set time to 0,0 in order to show all the results for the day. The users think we don't have buses
                $oDepartureDate->setTime(0, 0, 0);
            }else{
                //set date to start from 0,0 in order to load all the results for the day
                $oDepartureDate->setTime(0, 0, 0);
            }

            //only one leg if one way
            /** @var \Api\Client\Soap\Core\LegCriteria $legCriteria */
            $legCriteria = Base::getEntityObject('LegCriteria');
            $legCriteria->departureTimestamp = $oDepartureDate->getTimestampMiliSeconds(); //convert into miliseconds
            //get end timestamp
            $endTimestamp = clone $oDepartureDate;
            $endTimestamp = $endTimestamp->add(new \DateInterval("P1D"));
            $endTimestamp->setTime(0, 0);
            $legCriteria->departureTimestampEnd = $endTimestamp->getTimestampMiliSeconds();
//            var_dump(array(
//                $oDepartureDate->format('Y-m-d H:i:s'),
//                $endTimestamp->format('Y-m-d H:i:s'),
//            ));die;
            $criteria->legCriterion = array(
                $legCriteria
            );
            error_log("###############################");
            $searchResponse = $search->doSearch($criteria);
            error_log("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            //$searchResponse = $search->getResult($from, $to, false, $date);

            //save oneway result
            //$this->busResultPrices->oneWay = $searchResponse['oneway'];
            /**
             * loop through and save price in session
             * @note this is an utterly stupid way of keeping price. The API doesnt yet support
             *       so there is no alternative until the API can handle prices on it's booking function
             */
            $busPrices = array();
            foreach($searchResponse['oneway'] as $forwardLeg){
                $scheduleId = count($forwardLeg['sectors'])>0 ? $forwardLeg['sectors'][0]['scheduleId'] : 0;

                $busPrices[ $forwardLeg['resultIndex'] ] = array(
                    'price' => $forwardLeg['prices'],
                    'scheduleId' => $scheduleId,
                );
            }
            //$this->busResultPrices->busPrices = $busPrices;
            $this->getCache()->setItem($this->_cacheResultPrices . $session, $busPrices);

            $result = array(
                'result'=> $searchResponse,
                'total'=> count($searchResponse['oneway']),
                'session' => $session
            );
        } catch (\Exception $e) {
            error_log($e);
            $result['error'] = $e->getMessage();
        }
        return new JsonModel($result);
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
            $scheduleAvailability->setShowDetailed(true); //show full details about bookings
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
        return new JsonModel($result);
    }

    public function placebookingAction()
    {
        $reqestData = $this->params()->fromJson();
		
		if($_SESSION['bookingInProgress']===true){
			$oAlert = $this->getServiceLocator()->get('Application\Util\Alert');
			 
            $oAlert->sendEmailToAdmin(' <h2>Double booking detected. Please check!</h2>
                                <h2>Seat Numbers</h2><pre>' . json_encode($reqestData) . '</pre>
                            ', 'Double booking detected : ' );
			$_SESSION['bookingInProgress']=false;
			if($_SESSION['ticketData'] !=""){
				return new JsonModel($_SESSION['ticketData']);
			}
			throw new \Exception('Double booking request detected!. Please start again.');
		}else {
			$_SESSION['bookingInProgress']=true;
		}
		
		$result = array();
        $heldItemIndex = null;
        try {
            
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
			
			$passengerDataWithFare = [];
			
			// Warrant & Pass
			$warrantPass	= $bookingData['warrantPass'];
			$warrantPassRef	= $bookingData['warrantPassRef'];
			
			
			// $myfile = fopen("C:\wamp64\logs\PlaceBooking_DATA.txt", "a") or die("Unable to open file!");
			// fwrite($myfile,"\n==================================".date('Y-m-d H:i:s'));
			// fwrite($myfile,"\nRESULT_INDEX");
			// fwrite($myfile,print_r($resultIndex,true));
			// fwrite($myfile,"\nBOOKINGDATA");
			// fwrite($myfile,print_r($bookingData,true));
			// fwrite($myfile,"\nPASSENGERDATA");
			// fwrite($myfile,print_r($passengerData,true));
			// fwrite($myfile,"\nCONTACTDATA");
			// fwrite($myfile,print_r($contactData,true));
			// fwrite($myfile,"\nPAYMENTDATA");
			// fwrite($myfile,print_r($paymentData,true));
			// fwrite($myfile,"\nPAYMENTTYPE");
			// fwrite($myfile,print_r($paymentType,true));
			// fclose($myfile);
			
            /**
             * @todo set below value from user's permission from backend
             */
            $isVendor  = (isset($bookingData['vendor']) && $bookingData['vendor']==false)? false : true;

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
            /**
             * get price
             */
            $busPrices = $busResultPrices[$resultIndex]['price'];
            $perSeatAmount = floatval($busPrices['total']);
            $perSeatPayAmount = floatval($busPrices['priceBeforeCharges']);

			// $myfile = fopen("C:\wamp64\logs\PlaceBooking_PRICE_DATA.txt", "a") or die("Unable to open file!");
			// fwrite($myfile,"\n ==================================".date('Y-m-d H:i:s'));
			// fwrite($myfile,"\n BUS PRICES ");
			// fwrite($myfile,print_r($busPrices,true));
			// fwrite($myfile,"\n PERSEATAMOUNT ");
			// fwrite($myfile,print_r($perSeatAmount,true));
			// fwrite($myfile,"\n PERSEATPAYAMOUNT ");
			// fwrite($myfile,print_r($perSeatPayAmount,true));			
			// fclose($myfile);
			
            //get schedule object
            $scheduleId = $busResultPrices[$resultIndex]['scheduleId'];
            /** @var \Api\Manager\Schedule $scheduleManager */
            $scheduleManager = $this->getServiceLocator()->get('Api\Manager\Schedule');
            //get schedule
            /** @var BusSchedule $currentSchedule */
            $currentSchedule = $scheduleManager->fetch($scheduleId);
            $departureDate = ExprDateTime::getDateFromServices($currentSchedule->departureTime);
            /**
             * validate user's booking time. Only allow bookings before one hour
             * @todo move last booking time to systemwide settings
             */
            if($scheduleManager->hasTBBookingEnded($currentSchedule)){
                throw new \Exception('Bookings has been closed for this bus.');
            }

            $seats = array();
            $amount = 0;
            $payAmount = 0;
			// $myfile = fopen("C:\wamp64\logs\PlaceBooking_BUSPRICES_DATA.txt", "a") or die("Unable to open file!");
			// fwrite($myfile,"\n ==================================".date('Y-m-d H:i:s'));
			// fwrite($myfile,"\n BUS PRICES DATA  \n");
			// fwrite($myfile,print_r($busPrices,true));		
			// fclose($myfile);
			
			$busFareTotal = 0;
			$busReservationFeeTotal = 0;
			
            //loop through to filter data
            foreach($passengerData as $passenger){
                $seats[] = $passenger["seatNo"];
				if(strcasecmp($passenger["passengerType"],'adult')==0){
					$passenger['fare']=$busPrices['fare'];
					$busFareTotal += $busPrices['fare'];
					$payAmount += ($busPrices['fare']+$busPrices['baseMarkups']+$busPrices['baseCharges'])+$busPrices['baseDiscount'];
					$busReservationFeeTotal += ($busPrices['baseMarkups']+$busPrices['baseCharges'])+$busPrices['baseDiscount'];
				}else if(strcasecmp($passenger["passengerType"],'child')==0){
					$passenger['fare']=$busPrices['childFare'];
					$busFareTotal += $busPrices['childFare'];
					$payAmount += ($busPrices['childFare']+$busPrices['baseMarkups']+$busPrices['baseCharges'])+$busPrices['baseDiscount'];
					$busReservationFeeTotal += ($busPrices['baseMarkups']+$busPrices['baseCharges'])+$busPrices['baseDiscount'];
				}
				array_push($passengerDataWithFare,$passenger);
                //$payAmount += $amount ;
            }
			// Set passenger data 
			$bookingData['passenger']=$passengerDataWithFare;
			
			            $oHoldSeats = $sl->get('Api\Manager\Schedule\Hold');
            //execute hold call
            $oHoldSeats->setSession($sessionId);
            $holdResponse = $oHoldSeats->executeHold($bookingData['boardingLocation'], $bookingData['dropoffLocation'], $resultIndex, $seats);
            //var_dump($holdResponse);
            $heldItemIndex = $holdResponse->heldItemIds;
			
            $paymentMethodCriteria = null;
            $oPaymentcriteria = array();
            $agentId = null;
            $smsTemplate = Template::BOOKING_SUCCESS_TB;

            // payment
            switch($paymentType){
                case self::PAY_TYPE_CASH:
                    /** @var PaymentRefundCriteria $oPaymentcriteria */
                    $oPaymentcriteriaCash = Base::getEntityObject('PaymentRefundCriteria');
                    $oPaymentcriteriaCash->amount = $payAmount;
                    $oPaymentcriteriaCash->actualAmount = $payAmount;
                    $oPaymentcriteriaCash->actualCurrency = 'LKR';
                    $oPaymentcriteriaCash->mode = PaymentRefundMode::Cash;
                    $oPaymentcriteriaCash->type = PaymentRefundType::Payment;
                    //check if a vendor payment
                    if($isVendor){
                        $oPaymentcriteriaCash->mode = PaymentRefundMode::Vendor;
                        $oPaymentcriteriaCash->vendorMode = VendorPaymentRefundMode::Cash;
                    }
                    //check for sms alert
                    if(isset($paymentData['alertCashCustomer']) && $paymentData['alertCashCustomer']){
                        $alertCustomer = true;
                    }
					if((isset($warrantPass)) &&(($warrantPass==='warrant')||($warrantPass==='pass')) && (!is_null($warrantPass)) ){
						$oPaymentcriteriaWarrantPass = Base::getEntityObject('PaymentRefundCriteria');
						$oPaymentcriteriaWarrantPass->amount = $busFareTotal;
						$oPaymentcriteriaWarrantPass->actualAmount = $payAmount;
						$oPaymentcriteriaWarrantPass->actualCurrency = 'LKR';
						$oPaymentcriteriaWarrantPass->mode = PaymentRefundMode::Cash;
						$oPaymentcriteriaWarrantPass->type = PaymentRefundType::Payment;
						$oPaymentcriteriaWarrantPass->reference = $warrantPassRef;
						// Set Cash amount to booking fee 
						$oPaymentcriteriaCash->amount = $busReservationFeeTotal;
						
						
						//check if a vendor payment
						if($isVendor){
							$oPaymentcriteriaWarrantPass->mode = PaymentRefundMode::Vendor;
							if($warrantPass==='pass'){
								$oPaymentcriteriaWarrantPass->vendorMode = VendorPaymentRefundMode::Pass;
							}else{
								$oPaymentcriteriaWarrantPass->vendorMode = VendorPaymentRefundMode::Warrant;
							}
						}
					}
					array_push($oPaymentcriteria,$oPaymentcriteriaCash);
					if((isset($warrantPass)) &&(($warrantPass==='warrant')||($warrantPass==='pass')) && (!is_null($warrantPass)) ){
						array_push($oPaymentcriteria,$oPaymentcriteriaWarrantPass);
					}
                    break;

                case self::PAY_TYPE_AGENTS:
                    $reference      = null;
                    if (!empty($paymentData['agent']) && !empty($paymentData['agent']['id'])) {
                        $agentId  = $bookingData['payment']['agent']['id'];
                    }else{
                        throw new \Exception('Please select an Agent');
                    }
                    /** @var PaymentRefundCriteria $oPaymentcriteria */
                    $oPaymentcriteriaAgents = Base::getEntityObject('PaymentRefundCriteria');
                    $oPaymentcriteriaAgents->amount = $payAmount;
                    $oPaymentcriteriaAgents->actualAmount = $payAmount;
                    $oPaymentcriteriaAgents->actualCurrency = 'LKR';
                    $oPaymentcriteriaAgents->mode = PaymentRefundMode::Cash;
                    $oPaymentcriteriaAgents->type = PaymentRefundType::Payment;
                    //check if a vendor payment
                    if($isVendor){
                        $oPaymentcriteriaAgents->mode = PaymentRefundMode::Vendor;
                        $oPaymentcriteriaAgents->vendorMode = VendorPaymentRefundMode::Cash;
                    }
                    //disable sms alert.
                    $alertCustomer = false;
					array_push($oPaymentcriteria,$oPaymentcriteriaAgents);
                    break;

                case self::PAY_TYPE_EZCASH:
                    //disable sms alert. IPG return will send confirmation
                    $alertCustomer = false;
                    break;

                case self::PAY_TYPE_MCASH:
                    //disable sms alert. IPG return will send confirmation
                    $alertCustomer = false;
                    break;

                case self::PAY_TYPE_EZCASH_REF:
                    $reference      = null;
                    if (!empty($paymentData['ezcashRef'])) {
                        $reference  = $bookingData['payment']['ezcashRef'];
                    }else{
                        throw new \Exception('eZCash reference number is mandatory');
                    }
                    /** @var PaymentRefundCriteria $oPaymentcriteria */
                    $oPaymentcriteriaEzCashRef = Base::getEntityObject('PaymentRefundCriteria');
                    $oPaymentcriteriaEzCashRef->amount = $payAmount;
                    $oPaymentcriteriaEzCashRef->actualAmount = $payAmount;
                    $oPaymentcriteriaEzCashRef->actualCurrency = 'LKR';
                    $oPaymentcriteriaEzCashRef->mode = PaymentRefundMode::eZCash;
                    $oPaymentcriteriaEzCashRef->type = PaymentRefundType::Payment;
                    $oPaymentcriteriaEzCashRef->reference = $reference;
                    //check if a vendor payment
                    if($isVendor){
                        $oPaymentcriteriaEzCashRef->mode = PaymentRefundMode::Vendor;
                        $oPaymentcriteriaEzCashRef->vendorMode = VendorPaymentRefundMode::eZCash;
                    }
                    //check for sms alert
                    if(isset($paymentData['alertEzCustomer']) && $paymentData['alertEzCustomer']){
                        $alertCustomer = true;
                    }
					array_push($oPaymentcriteria,$oPaymentcriteriaEzCashRef);
                    break;

                case self::PAY_TYPE_ATBUS:
				// This method has 2 payment option
				// 1. Ticket fare is paid to the conductor directly.
				// 2. Booking Fee is collected as Cash at the ticket counter.
				// We record both payment options for reporting purpose.
                    $oPaymentcriteriaCashAtBus = Base::getEntityObject('PaymentRefundCriteria');
                    //$oPaymentcriteriaCashAtBus->paymentMethod = PaymentMethod::CashAtBus;
					$oPaymentcriteriaCashAtBus->amount = $busFareTotal;
                    $oPaymentcriteriaCashAtBus->actualAmount = $payAmount;
                    $oPaymentcriteriaCashAtBus->actualCurrency = 'LKR';
                    $oPaymentcriteriaCashAtBus->mode = PaymentRefundMode::Cash;
                    $oPaymentcriteriaCashAtBus->type = PaymentRefundType::Payment;
					//check if a vendor payment
                    if($isVendor){
                        $oPaymentcriteriaCashAtBus->mode = PaymentRefundMode::Vendor;
                        $oPaymentcriteriaCashAtBus->vendorMode = VendorPaymentRefundMode::PayAtBus;
                    }
                    //check for sms alert
                    if(isset($paymentData['alertPbCustomer']) && $paymentData['alertPbCustomer']){
                        $alertCustomer = true;
                    }
					
					$oPaymentcriteriaCash = Base::getEntityObject('PaymentRefundCriteria');
					$oPaymentcriteriaCash->amount = $busReservationFeeTotal;
					$oPaymentcriteriaCash->actualAmount = $payAmount;
					$oPaymentcriteriaCash->actualCurrency = 'LKR';
					$oPaymentcriteriaCash->mode = PaymentRefundMode::Cash;
					$oPaymentcriteriaCash->type = PaymentRefundType::Payment;
					//check if a vendor payment
					if($isVendor){
						$oPaymentcriteriaCash->mode = PaymentRefundMode::Vendor;
						$oPaymentcriteriaCash->vendorMode = VendorPaymentRefundMode::Cash;
					}
					// Pat at bus in SLTB scenario has a cash payment covering reservation charges. 
					// Other operators like superline doesn't have cash payment.
					// When reservation charges are 0 then we don't record a cash payment.
					if($busReservationFeeTotal>0){
						array_push($oPaymentcriteria,$oPaymentcriteriaCashAtBus,$oPaymentcriteriaCash);
					}else {
						array_push($oPaymentcriteria,$oPaymentcriteriaCashAtBus);
					}
                    break;

                case self::PAY_TYPE_DIFF_PAY:
                    $holdUntil = null;
                    if ($departureDate) {
                        //reduce 1 hour from departure date
                        $holdUntilDate  = new ExprDateTime();
                        //set hold until date
                        $holdUntilDate->setTimestamp($departureDate->sub(new \DateInterval('PT1H'))->getTimestamp());
                        $holdUntil = $holdUntilDate->getTimestamp();
                    }else{
                        throw new \Exception('Pay later needs a valid date');
                    }
                    /** @var PaymentMethodCriteria $paymentMethodCriteria */
                    $paymentMethodCriteria = Base::getEntityObject('PaymentMethodCriteria');
                    //set schedule end as last date
                    $paymentMethodCriteria->holdUntil = $holdUntil;
                    $paymentMethodCriteria->paymentMethod = PaymentMethod::Deferred;
                    //check for sms alert
                    if(isset($paymentData['alertPLCustomer']) && $paymentData['alertPLCustomer']){
                        $alertCustomer = true;
                        //set tentitive template
                        $smsTemplate = Template::BOOKING_TENTETIVE;
                    }

                    break;

                case self::PAY_TYPE_CARD:
                    $reference      = null;
                    if (!empty($paymentData['cardRef'])) {
                        $reference  = $paymentData['cardRef'];
                    }else{
                        throw new \Exception('Card reference number is mandatory');
                    }
                    /** @var PaymentRefundCriteria $oPaymentcriteria */
                    $oPaymentcriteriaCard = Base::getEntityObject('PaymentRefundCriteria');
                    $oPaymentcriteriaCard->amount = $payAmount;
                    $oPaymentcriteriaCard->actualAmount = $payAmount;
                    $oPaymentcriteriaCard->actualCurrency = 'LKR';
                    $oPaymentcriteriaCard->mode = PaymentRefundMode::Card;
                    $oPaymentcriteriaCard->type = PaymentRefundType::Payment;
                    $oPaymentcriteriaCard->reference = $reference;
                    //check if a vendor payment
                    if($isVendor){
                        $oPaymentcriteriaCard->mode = PaymentRefundMode::Vendor;
                        $oPaymentcriteriaCard->vendorMode = VendorPaymentRefundMode::Card;
                    }
					
					array_push($oPaymentcriteria,$oPaymentcriteriaCard);
					
                    break;

                case self::PAY_TYPE_BANKTRANSFER:
                    $reference      = null;
                    if (!empty($paymentData['bankRef'])) {
                        $reference  = $paymentData['bankRef'];
                    }else{
                        throw new \Exception('Bank reference number is mandatory');
                    }
                    /** @var PaymentRefundCriteria $oPaymentcriteria */
                    $oPaymentcriteriaBankTransfer = Base::getEntityObject('PaymentRefundCriteria');
                    $oPaymentcriteriaBankTransfer->amount = $payAmount;
                    $oPaymentcriteriaBankTransfer->actualAmount = $payAmount;
                    $oPaymentcriteriaBankTransfer->actualCurrency = 'LKR';
                    $oPaymentcriteriaBankTransfer->mode = PaymentRefundMode::BankTransfer;
                    $oPaymentcriteriaBankTransfer->type = PaymentRefundType::Payment;
                    $oPaymentcriteriaBankTransfer->reference = $reference;
                    //check if a vendor payment
                    if($isVendor){
                        $oPaymentcriteriaBankTransfer->mode = PaymentRefundMode::Vendor;
                        $oPaymentcriteriaBankTransfer->vendorMode = VendorPaymentRefundMode::BankTransfer;
                    }
					array_push($oPaymentcriteria,$oPaymentcriteriaBankTransfer);
                    break;

                default:
                    throw new \Exception('Invalid payment method');
            }

			
			// $myfile = fopen("C:\wamp64\logs\PlaceBooking_PAYMENTCRITERIA_DATA.txt", "a") or die("Unable to open file!");
			// fwrite($myfile,"\n ==================================".date('Y-m-d H:i:s'));
			// fwrite($myfile,"\n PAYMENT CRITERIA DATA\n");
			// fwrite($myfile,print_r($oPaymentcriteria,true));		
			// fclose($myfile);
			
			// $myfile = fopen("C:\wamp64\logs\PlaceBooking_PAYMENTMETHODCRITERIA_DATA.txt", "a") or die("Unable to open file!");
			// fwrite($myfile,"\n ==================================".date('Y-m-d H:i:s'));
			// fwrite($myfile,"\n PAYMENT METHOD CRITERIA DATA\n");
			// fwrite($myfile,print_r($paymentMethodCriteria,true));		
			// fclose($myfile);
			
            /**
             * @var $oHoldSeats \Api\Manager\Schedule\Hold
             */
            // $oHoldSeats = $sl->get('Api\Manager\Schedule\Hold');
            // //execute hold call
            // $oHoldSeats->setSession($sessionId);
            // $holdResponse = $oHoldSeats->executeHold($bookingData['boardingLocation'], $bookingData['dropoffLocation'], $resultIndex, $seats);
            // //var_dump($holdResponse);
            // $heldItemIndex = $holdResponse->heldItemIds;
			

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

			// $myfile = fopen("C:\wamp64\logs\PlaceBooking_CONFIRMATION_RESPONSE_DATA.txt", "a") or die("Unable to open file!");
			// fwrite($myfile,"\n ==================================");
			// fwrite($myfile,"\n CONFIRMATION RESPONSE DATA\n");
			// fwrite($myfile,print_r($oconfirmResponse,true));		
			// fclose($myfile);
			
			
            //if payment type is ezcash, send to IPG
            if($paymentType==self::PAY_TYPE_EZCASH){
                /** @var \Application\Payment\Gateway\DialogEzCash $oDialog */
                $oDialog = $this->getServiceLocator()->get('Payment\DialogEzCash');
                $oIpgRequest = $oDialog->getEncryptedRequest($transactionId, $payAmount);
                $result['IPGForm'] = $oIpgRequest->getRequestPayload();
                $result['IPGtype'] = $oIpgRequest->getRequestProcessType();
                $result['reference'] = $transactionId;
            }elseif($paymentType==self::PAY_TYPE_MCASH){
                /** @var \Application\Payment\Gateway\MobitelMCash $oMobitel */
                $oMobitel = $this->getServiceLocator()->get('Payment\MobitelMCash');
                $oIpgRequest = $oMobitel->getEncryptedRequest($transactionId, $payAmount);
                $result['IPGForm'] = $oIpgRequest->getRequestPayload();
                $result['IPGtype'] = $oIpgRequest->getRequestProcessType();
                $result['reference'] = $transactionId;
            }
            else{
                //get ticket response and send sms
                $result['ticketData'] = $this->getTicketInfo($contactData, $oconfirmResponse, $alertCustomer, $smsTemplate);
				$_SESSION['ticketData'] = $result;
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
        }finally{
			$_SESSION['bookingInProgress']=false;
			$_SESSION['ticketData'] ="";
		}

        return new JsonModel($result);
    }

    private function getTicketInfo($contactData, \Api\Client\Soap\Core\Booking $oconfirmResponse, $alertCustomer, $smsTemplate)
    {
		// $myfile = fopen("C:\wamp64\logs\getTicketInfo_OCONFIRMRESPONSE_DATA.txt", "a") or die("Unable to open file!");
		// fwrite($myfile,"\n ==================================".date('Y-m-d H:i:s'));
		// fwrite($myfile,"\n OCONFIRMRESPONSE DATA  \n");
		// fwrite($myfile,print_r($oconfirmResponse,true));		
		// fclose($myfile);
		// $response = array();
        /**
         * @var $bookingItems \Api\Client\Soap\Core\BookingItem
         */
        $bookingItems = $oconfirmResponse->bookingItems;
        $secDepartureTime = is_string($bookingItems->schedule->departureTime)? strtotime($bookingItems->schedule->departureTime) : floor($bookingItems->schedule->departureTime/1000);
        $secArrivalTime = is_string($bookingItems->schedule->arrivalTime)? strtotime($bookingItems->schedule->arrivalTime) : floor($bookingItems->schedule->arrivalTime/1000);

        //params
        //$customerEmail  = $oconfirmResponse->client->user->
        $beThereAt      = date('h:i A', ($secDepartureTime - 1200));
        $departureTime  = date('h:i A', $secDepartureTime);
        $date           = date('jS F Y', $secDepartureTime);
        $price          = $bookingItems->price;
        $bookingNo      = $oconfirmResponse->reference;
		// Verification code 
		$vCode = $oconfirmResponse->verficationCode;
        $clientTelephone= $oconfirmResponse->client->mobileTelephone;
        $clientEmail    = $oconfirmResponse->client->email;
		
        $adultSeatNumbers    = array();
        $childSeatNumbers    = array();
		$adultFare = "";
		$childFare = "";

        //attach each tiket per passenger
        $passengers = is_array($bookingItems->passengers) ? $bookingItems->passengers : array($bookingItems->passengers);
		
		foreach($passengers as $passenger){
			if($passenger->passengerType=='Adult'){
				$adultSeatNumbers[] = $passenger->seatNumber;
				$adultFare = $passenger->seatFare;
			}
			if($passenger->passengerType=='Child'){
				$childSeatNumbers[] = $passenger->seatNumber;
				$childFare = $passenger->seatFare;
			}
		}
		
		$bookedData = array(
			'beThereAt'         => $beThereAt,
			'travels'           => $bookingItems->schedule->bus->busType->type ,
			'busNumber'         => $bookingItems->schedule->bus->plateNumber,
			'departCity'        => $bookingItems->fromBusStop->name . '('. $bookingItems->fromBusStop->city->code .')',
			'boardingLocation'  => $bookingItems->fromBusStop->name,
			'seatType'          => '',
			'departureTime'     => $departureTime,
			'bookingNo'         => $bookingNo,
			'vCode'				=> $vCode,
			'routeName'         => $bookingItems->schedule->busRoute->displayNumber,
			'date'              => $date,
			'arrivalCity'       => $bookingItems->toBusStop->name . '('. $bookingItems->toBusStop->city->code .')',
			'sequenceNo'        => 1,
			'busType'           => $bookingItems->schedule->bus->busType->type,
			'cost'           	=> $bookingItems->cost, //bus price
			'price'           	=> $bookingItems->price, //price with markups
			'reservationCharges' => ($bookingItems->price - $bookingItems->cost),
			'bookedTime' 		=> date(('Y-m-d H:i:s')),//ExprDateTime::getDateFromServices($oconfirmResponse->bookingTime)->format('Y-m-d H:i:s'),
		);
		
		$bookingPayments = $oconfirmResponse->payments;
		if(is_array($bookingPayments)){
			foreach($bookingPayments as $bookingPayment){
				if($bookingPayment->vendorMode==VendorPaymentRefundMode::Warrant){
					$bookedData['warrant']=array("reference"=>$bookingPayment->reference);
				}
				if($bookingPayment->vendorMode==VendorPaymentRefundMode::Pass){
					$bookedData['pass']=array("reference"=>$bookingPayment->reference);
				}
				if($bookingPayment->vendorMode==VendorPaymentRefundMode::PayAtBus){
					$bookedData['payatbus']=$bookingPayment->amount;
				}
			}
		}
		// add Adults seats
		if (count($adultSeatNumbers)>0){
			$bookedData['adultPassengers']   = $adultSeatNumbers; // implode(', ', $adultSeatNumbers);
			$bookedData['adultFare'] = $adultFare;
		}
		if (count($childSeatNumbers)>0){
			$bookedData['childPassengers']   = $childSeatNumbers ; //implode(', ', $childSeatNumbers);
			$bookedData['childFare'] = $childFare;
		}
		
		// Addd Adult & Child Fare for ticket display purpose
		
		
		
		//encode and save
		$response[] = $bookedData;

        /**
         * Send SMS only if alert customer checkbox is checked
         */
        try {
			
            //send sms if only the phone number exists
            if($alertCustomer && isset($contactData['mobileNo']) && !empty($contactData['mobileNo'])){
				
				
				// $myfile = fopen("C:\wamp64\logs\PlaceBooking_ADDTOSMSQUEUE_DATA.txt", "a") or die("Unable to open file!");
				// fwrite($myfile,"\n ==================================".date('Y-m-d H:i:s'));
				// fwrite($myfile,"\n ADD TO SMS QUEUE DATA  \n");
				// fwrite($myfile,print_r($oconfirmResponse->reference,true));		
				// fclose($myfile);
				// Add to SMS queue table 
				$smsQueue = $this->getServiceLocator()->get('Data\Manager\SmsQueue');
				$test = $smsQueue->addToSMSQueue($oconfirmResponse->reference);
				
				
				/**
                 * get sms object
                 * @var $oSms \Application\Alert\Sms
                 */
                // $oSms = $this->getServiceLocator()->get('Alert\Sms');
                // $oSms->sendBookingSms($oconfirmResponse, $smsTemplate, $contactData['mobileNo']);
            }
        } catch (\Exception $e) {
            $alertBody = '
                <h2>SMS failed due to</h2><pre>'. $e->getMessage() .'</pre>
                <h2>SMS failed to</h2><pre>'. $contactData['mobileNo'] .'</pre>
                <h2>Booking Reference</h2><pre>'. $bookingNo .'</pre>
                ';
            $oAlert = $this->getServiceLocator()->get('Application\Util\Alert');
            $oAlert->sendEmailToAdmin($alertBody, 'ExpressBot - Sending sms failed on bookingTB');
        }

        try {
            //send email if the address is available and booking is confirmed.
            if(!empty($oconfirmResponse->client->email) && $oconfirmResponse->status->code==Booking::STATUS_CODE_CONFIRM){
                $emailQueue = $this->getServiceLocator()->get('Data\Manager\EmailQueue');
				$test = $emailQueue->addToEmailQueue($oconfirmResponse->reference); 
				
				// Add to email queue table 
				/**
                 * Send email
                 * @var $oEmail \Application\Alert\Email
                 */
                //$oEmail = $this->getServiceLocator()->get('Alert\Email');
                //$oEmail->sendBookingSuccessEmail($oconfirmResponse);
            }
        } catch (\Exception $e) {
            throw $e;
        }
		// Reset booking in progress flags.
		$_SESSION['bookingInProgress']=false;
		$_SESSION['ticketData'] ="";
		
        return base64_encode(json_encode($response));
    }

    /**
     * ======== Temporary functions========
     */
    public function seatreportAction()
    {
        $boardingLocation = $this->params()->fromQuery('boardingLocation');
        $dropoffLocation  = $this->params()->fromQuery('dropoffLocation');
        $adults     = 1;
        $busTypeId  = $this->params()->fromQuery('busType');
        $children   = 0;
        $infants    = 0;
        $scheduleId = $this->params()->fromQuery('scheduleId');
        $busName = $this->params()->fromQuery('bus');

        $agent = new RestFactory($this->getServiceLocator(), 'agent');
        //build criteria
        $criteria = new QueryCriteria();
        $criteria->setLoadAll();
        $agents = $agent->getList($criteria);

        $result = $agents->body;
        foreach ($result as $key => $ag) {
            $result[$ag->id] = $ag;
        }

        //get schedule based on id
        $oSchedule = new RestFactory($this->getServiceLocator(), 'busSchedule');
        /** @var BusSchedule $oneSchedule */
        $oneSchedule = $oSchedule->getOne($scheduleId)->body;
        /**
         * @var $scheduleAvailability \Api\Manager\Schedule\Availability
         */
        $scheduleAvailability = $this->getServiceLocator()->get('Api\Manager\Schedule\Availability');
        $scheduleAvailability->setShowDetailed(true); //show full details about bookings
        $scheduleAvailability->setSession($this->params()->fromQuery('session'));
        $seats = $scheduleAvailability->getResult($boardingLocation, $dropoffLocation, $adults, $busTypeId, $children, $infants, $scheduleId);

        /**
         * @var $bookingManager \Api\Manager\Booking
         */
        $bookingManager = $this->getServiceLocator()->get('Api\Manager\Booking');
        $cacheReferences = array();
        foreach($seats as $key=>$seat){
            if(isset($seat['booking_ref'])){
                $ref = $seat['booking_ref'];
                if(!isset($cacheReferences[$ref])){
                    $oBooking = $bookingManager->getBookingRefById($ref);
                    $cacheReferences[$ref] = $oBooking;
                }
                $seats[$key]['booking'] = $cacheReferences[$ref];
            }

        }

        $oView = new ViewModel();
        $oView->setTemplate('admin/ticketbox/seatreport.phtml');
        $oView->setTerminal(true);
        $oView->seats = $seats;
        $oView->bus = $busName;
        $oView->agents = $result;
        $oView->schedule = $oneSchedule;
        return $oView;
    }

    public function alertcondaAction()
    {
        $reqestData = $this->params()->fromJson();
        $sl         = $this->getServiceLocator();

        $boardingLocation = $reqestData['boardingLocation'];
        $dropoffLocation  = $reqestData['dropoffLocation'];
        $adults     = 1;
        $busTypeId  = $reqestData['busType'];
        $children   = 0;
        $infants    = 0;
        $scheduleId = $reqestData['scheduleId'];
        $result = array();

        try {
            //get schedule based on id
            $oSchedule = new RestFactory($this->getServiceLocator(), 'busSchedule');
            /** @var BusSchedule $oneSchedule */
            $oneSchedule = $oSchedule->getOne($scheduleId)->body;
            $secDepartureTime = is_string($oneSchedule->departureTime)? strtotime($oneSchedule->departureTime) : floor($oneSchedule->departureTime/1000);
            $secArrivalTime = is_string($oneSchedule->arrivalTime)? strtotime($oneSchedule->arrivalTime) : floor($oneSchedule->arrivalTime/1000);

            $conductorPhone = $oneSchedule->conductor->person->mobileTelephone;
            /**
             * @var $scheduleAvailability \Api\Manager\Schedule\Availability
             */
            $scheduleAvailability = $this->getServiceLocator()->get('Api\Manager\Schedule\Availability');
            $scheduleAvailability->setShowDetailed(true); //show full details about bookings
            $scheduleAvailability->setSession($reqestData['session']);
            $seats = $scheduleAvailability->getResult($boardingLocation, $dropoffLocation, $adults, $busTypeId, $children, $infants, $scheduleId);
            $sBookingList = '';
            usort($seats, function($a, $b){
                return $a['number'] - $b['number'];
            });
            foreach ($seats as $seat) {
                if($seat['booked'] && !$seat['dummy']){
                    $mobile = ($seat['booking_status']=='pay_at_bus'? "({$seat['mobile']})" : $seat['mobile']);
                    $sBookingList .= $seat['number'] .'='. $mobile . PHP_EOL;
                }
            }

            /**
             * Send SMS
             */
            $aSmsData = array(
                '::PLATE_NO::'      => $oneSchedule->bus->plateNumber,
                '::DATE::'          => date('Y-m-d',$secDepartureTime),
                '::FROM_NAME::'     => $oneSchedule->busRoute->fromCity->name,
                '::TO_NAME::'     => $oneSchedule->busRoute->toCity->name,
                '::DEPARTURE_TIME::' => date('h:i A', $secDepartureTime),
                '::SEAT_PASSENGER_NIC::'     => $sBookingList,
            );
            /* @var $oSms \Application\Sms\Client */
            $oSms = $this->getServiceLocator()->get('Sms');
            try {
                $oSms->sendSMS($conductorPhone, Template::CONDUCTOR_SCHEDULE_HAS_BOOKINGS, $aSmsData);
            } catch (\Exception $e) {
                //send alert to admin if sms failed
                $alertBody = '
                            <h2>Conductor SMS failed due to</h2><pre>'. $e->getMessage() .'</pre>
                            <h2>SMS sent to</h2><pre>'. $conductorPhone .'</pre>
                            <h2>SMS body</h2><pre>'. var_export($aSmsData, true) .'</pre>
                            ';
                $oAlert = $this->getServiceLocator()->get('Application\Util\Alert');
                $oAlert->sendEmailToAdmin($alertBody, 'ExpressBot - Sending sms failed for Conductor');
            }
        } catch (\Exception $e) {
            $result['error'] = $e->getMessage();
        }

        return new JsonModel($result);
    }

    /**
     * ======== Temporary functions========
     */

    /**
     * @return array|object
     */
    private function getCache()
    {
        return $this->getServiceLocator()->get('cache');
    }

}
