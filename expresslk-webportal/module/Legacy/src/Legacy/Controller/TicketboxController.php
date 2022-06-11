<?php

namespace Legacy\Controller;

use Api\Client\Mobitel\CGW\chargeFromMSISDN;
use Api\Client\Mobitel\CGW\chgRequest;
use Api\Client\Mobitel\CGWService;
use Api\Client\Rest\Model\City;
use Api\Client\Soap\Core\Booking;
use Api\Client\Soap\Core\BusSchedule;
use Api\Client\Soap\Core\PaymentRefundCriteria;
use Api\Client\Soap\Core\PaymentRefundMode;
use Api\Client\Soap\Core\PaymentRefundType;
use Api\Client\Soap\Core\VendorPaymentRefundMode;
use Api\Manager\Base;
use Application\Helper\ExprDateTime;
use Application\Sms\Template;
use Legacy\Extend\Mobitel\MobitelCustomerProvider;
use Legacy\Extend\ProviderBase;
use Legacy\Ticketing\Availability;
use Legacy\Ticketing\Search;
use Legacy\Ticketing\Session;
use Zend\Mvc\Controller\AbstractActionController;
use Zend\View\Model\JsonModel;
use Zend\View\Model\ViewModel;

class TicketboxController extends AbstractActionController
{

    public function indexAction()
    {
        $oView = new ViewModel();
        $aErrors = array();

        //get city list from service
        $oCities = new City($this->getServiceLocator());
        $oCities->setShouldTranslate(false);
        $aCityList = $oCities->getCityList();
        //create html view
        $oView->setVariable(
            'allCities',
            $aCityList
        );

        //check if form submitted
        $sFrom = $this->params()->fromQuery('from');
        $sTo = $this->params()->fromQuery('to');
        $sDate = $this->params()->fromQuery('date');
        if(is_numeric($sFrom) && is_numeric($sTo) && !empty($sDate)){
            try {//validate date
                $oDate = \DateTime::createFromFormat('Y-m-d', $sDate);
                if ($oDate === false) {
                    throw new \Exception('Incorrect date. Please check and try again.');
                }
                $oDate->setTime(0, 0);
                $oToday = new \DateTime();
                $oToday->setTime(0, 0);
                //check for less than today
                if ($oDate < $oToday) {
                    throw new \Exception('Incorrect date range. Please check and try again.');
                }
                //check for more than 3 months
                $oToday->add(new \DateInterval('P2M'));
                if ($oDate > $oToday) {
                    throw new \Exception('Incorrect max date range. Please check and try again.');
                }

                //get search result
                /** @var Search $oSearch */
                $oSearch = $this->getServiceLocator()->get('Legacy\Ticketing\Search');
                $aSearchResults = $oSearch->getResults($sFrom, $sTo, $sDate);
                $oView->results = $aSearchResults['oneway'];
            } catch (\Exception $e) {
                $aErrors[] = $e->getMessage();
            }
        }else{
            //check if submit button clicked
            if($this->params()->fromQuery('submit', false)!==false){
                $aErrors[] = "From/To and Date are required for a search.";
            }
        }
        //save form data
        $oView->searchForm = array(
            'from' => $sFrom,
            'to' => $sTo,
            'date' => $sDate,
        );

        try {
            /** @var Session $oSession */
            $oSession = $this->getServiceLocator()->get('Legacy\Ticketing\Session');
            $oView->provider = $oSession->getProvider();
            //get session id
            $oView->sessionId = $oSession->getSessionId();
        } catch (\Exception $e) {
            $aErrors[] = $e->getMessage();
        }
        $oView->errors = $aErrors;

        return $oView;
    }

    public function seatinfoAction()
    {
        $result = array();
        try {
            $reqestData = $this->params()->fromPost();

            $boardingLocation = $reqestData['boardingLocation'];
            $dropoffLocation  = $reqestData['dropoffLocation'];
            $busTypeId  = $reqestData['busType'];
            $scheduleId = $reqestData['scheduleId'];
            $sessionIdKey = $reqestData['sessionId'];

            /** @var Session $oLegacySession */
            $oLegacySession = $this->getServiceLocator()->get('Legacy\Ticketing\Session');
            //validate current session
            if(!$oLegacySession->sessionIdIsValid($sessionIdKey)){
                throw new \Exception('Session id is invalid. Please close this window.');
            }
            $session = $oLegacySession->getSearchSession();

            /** @var Availability $oAvailability */
            $oAvailability = $this->getServiceLocator()->get('Legacy\Ticketing\Availability');
            $seats = $oAvailability->getResult($session, $boardingLocation, $dropoffLocation, $busTypeId, $scheduleId);
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
        $result = array();
        $heldItemIndex = null;
        try {
            $bookingData = $this->params()->fromPost();
            $sl = $this->getServiceLocator();
            $auth = $this->getServiceLocator()->get('AuthService');
//var_dump($bookingData);die;
            $resultIndex    = $bookingData['resultIndex'];
            $passengerData  = $bookingData['passenger'];
            $contactData    = $bookingData['contact'];
            $paymentType    = $bookingData['type'];
            $alertCustomer  = true;
            $paymentData    = $bookingData['payment'];
            $sessionIdKey      = $bookingData['sessionId'];

            /** @var Session $oLegacySession */
            $oLegacySession = $this->getServiceLocator()->get('Legacy\Ticketing\Session');
            //validate current session
            if(!$oLegacySession->sessionIdIsValid($sessionIdKey)){
                throw new \Exception('Session id is invalid. Please close this window.');
            }
            $sessionId = $oLegacySession->getSearchSession();
            /** @var MobitelCustomerProvider $oProvider */
            $oProvider = $oLegacySession->getProvider();

            //get from cache
            $busResultPrices = $oLegacySession->getSearchData($sessionId);
            //check if temporary data exists
            if(!isset($busResultPrices[$resultIndex])){
                throw new \Exception('Incorrect Session data. Please refresh the web page.');
            }

            /**
             * @Todo If this is live environment, check for initiator IP against Mobitel IPs before charging.
             */


            /**
             * get price
             */
            $busPrices = $busResultPrices[$resultIndex]['price'];
            $perSeatAmount = floatval($busPrices['total']);
            $perSeatPayAmount = floatval($busPrices['priceBeforeCharges']);

            //get schedule object
            $scheduleId = $busResultPrices[$resultIndex]['scheduleId'];
            /** @var \Api\Manager\Schedule $scheduleManager */
            $scheduleManager = $this->getServiceLocator()->get('Api\Manager\Schedule');
            /** @var BusSchedule $currentSchedule */
            $currentSchedule = $scheduleManager->fetch($scheduleId);
            /**
             * validate user's booking time. Only allow bookings before one hour
             */
            if($scheduleManager->hasWebBookingEnded($currentSchedule)){
                throw new \Exception('Bookings has been closed for this bus.');
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
            $contactData['mobileNo'] = $oProvider->getPhoneNumber();

            //throw error if there are no selected seats
            if(count($seats)==0){
                throw new \Exception('Please select at least one seat to continue.');
            }
            //validate phone number
            if(empty($contactData['mobileNo'])){
                throw new \Exception('Phone number doesnt exist. Please re-start the process.');
            }

            $paymentMethodCriteria = null;
            $oPaymentcriteria = null;
            $agentId = null;
            $smsTemplate = Template::BOOKING_SUCCESS;

            /**
             * @var $oHoldSeats \Api\Manager\Schedule\Hold
             */
            $oHoldSeats = $sl->get('Api\Manager\Schedule\Hold');
            //execute hold call
            $oHoldSeats->setSession($sessionId);
            $holdResponse = $oHoldSeats->executeHold($bookingData['boardingLocation'], $bookingData['dropoffLocation'], $resultIndex, $seats);
            $heldItemIndex = $holdResponse->heldItemIds;

            /**
             * Confirm booking
             * @var $oConfirmation \Api\Manager\Booking\ConfirmBooking
             */
            $oConfirmation = $sl->get('Api\Manager\Booking\ConfirmBooking');
            $oConfirmation->setSession($sessionId);
            $oConfirmation->setLoggedInUserId($auth->getIdentity()->id);
            $oconfirmResponse = $oConfirmation->confirm($heldItemIndex, $bookingData);
            //set booking reference as transaction id
            $transactionId = $oconfirmResponse->reference;
            $payAmount = $oconfirmResponse->chargeable;
            
            error_log("placebookingAction \n", 3, "/var/log/mobitel-payment-debug.log");
            error_log(print_r($bookingData,true), 3, "/var/log/mobitel-payment-debug.log");

            //process alerts etc. after booking success.
            if($paymentType=='MobitelInternal'){
                //process mobitel charging result
                $smsTemplate = Template::BOOKING_SUCCESS_MB365;
                //process payment with Mobitel CGW
                /**
                 * @todo move this to a proper structure
                 */
                $aConfig = $this->getServiceLocator()->get('Config');
                $aCGWConfig = $aConfig['client-api']['mobitel']['cgw'];
                /** @var CGWService $oMCGW */
                $oMCGW = $this->getServiceLocator()->get('Api\Mobitel\CGW');
                $iCentAmount = $payAmount*100; //send the cent amount
                $oChargeReq = new chgRequest($iCentAmount, $transactionId, $oProvider->getPhoneNumber(), $aCGWConfig['serviceId'], $transactionId);
                $oCharge = new chargeFromMSISDN($oChargeReq, $aCGWConfig['username'], $aCGWConfig['password']);
                $oChargeResponse = $oMCGW->chargeFromMSISDN($oCharge);

                //if payment was success
                if($oChargeResponse->return->resultCode==1000){
                    try {

                        error_log("Mobitel Payment Request Result \n", 3, "/var/log/mobitel-payment-debug.log");
                        //add payment
                        /** @var PaymentRefundCriteria $oPaymentcriteria */
                        $oPaymentcriteria = Base::getEntityObject('PaymentRefundCriteria');
                        $oPaymentcriteria->bookingId = $oconfirmResponse->id;
                        $oPaymentcriteria->amount = $payAmount;
                        $oPaymentcriteria->actualAmount = $payAmount;
                        $oPaymentcriteria->actualCurrency = 'LKR';
                        $oPaymentcriteria->type = PaymentRefundType::Payment;
                        $oPaymentcriteria->mode = PaymentRefundMode::Vendor;
                        $oPaymentcriteria->vendorMode = VendorPaymentRefundMode::Mobitel;
                        $oPaymentcriteria->reference = $oChargeResponse->return->transactionId;

                        error_log(print_r($oPaymentcriteria,true), 3, "/var/log/mobitel-payment-debug.log");

                        /**
                         * @var $oPayment \Api\Manager\Booking\Payment
                         */
                        $oPayment = $sl->get('Api\Manager\Booking\Payment');
                        $paymentResponse = $oPayment->addPaymentRefund($oPaymentcriteria);
                    } catch (\Exception $e) {
                        error_log(print_r($oPaymentcriteria,true), 3, "/var/log/mobitel-payment-debug.log");
                        error_log(print_r($e,true), 3, "/var/log/mobitel-payment-debug.log");
                        //alert admins
                        $alertBody = '<h1>Mobitel payment deducted. But booking failed.</h1>
                        <h2>Booking ref </h2><pre>'. $oconfirmResponse->reference .'</pre>
                        <h2>Mobitel transaction Id </h2><pre>'. $oChargeResponse->return->transactionId .'</pre>
                        ';
                        $oAlert = $this->getServiceLocator()->get('Application\Util\Alert');
                        $oAlert->sendEmailToAdmin($alertBody, 'ExpressBot - Mobitel payment error');
                        //show error to client
                        throw new \Exception('Payment deducted but the seat was not booked. Please inform the administrator.');
                    }

                    //if no exception is thrown, booking is success.
                    //Send final alerts to the customer
                    $this->_sendBookingSuccess($oconfirmResponse, $smsTemplate);
                    $result['result'] = true;

                    //clear session
                    $oLegacySession->destroy();
                }else{
                    throw new \Exception(strval($oChargeResponse->return->resultCode).' '.$oChargeResponse->return->resultDesc);
                    //maybe cancel the booking
                }
            }else{
                throw new \Exception('Invalid payment type.');
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

        return new JsonModel($result);
    }

    /**
     * @param Booking $oconfirmResponse
     * @param $smsTemplate
     * @throws \Exception
     */
    private function _sendBookingSuccess($oconfirmResponse, $smsTemplate)
    {
        /**
         * Send SMS only if alert customer checkbox is checked
         */
        try {
            //send sms if only the phone number exists
            if($oconfirmResponse->client->mobileTelephone){
                /**
                 * get sms object
                 * @var $oSms \Application\Alert\Sms
                 */
                $oSms = $this->getServiceLocator()->get('Alert\Sms');
                $oSms->sendBookingSms($oconfirmResponse, $smsTemplate);
            }
        } catch (\Exception $e) {
            $alertBody = '
                <h2>SMS failed due to</h2><pre>'. $e->getMessage() .'</pre>
                <h2>SMS failed to</h2><pre>'. $oconfirmResponse->client->mobileTelephone .'</pre>
                <h2>Booking Reference</h2><pre>'. $oconfirmResponse->reference .'</pre>
                ';
            $oAlert = $this->getServiceLocator()->get('Application\Util\Alert');
            $oAlert->sendEmailToAdmin($alertBody, 'ExpressBot - Sending sms failed on Legacy-Mobitel');
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
    }
}

