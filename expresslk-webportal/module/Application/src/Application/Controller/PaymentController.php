<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 1/12/15
 * Time: 1:53 PM
 */

namespace Application\Controller;

use Api\Client\Soap\Core\CancellationCause;
use Api\Client\Soap\Core\PaymentRefundMode;
use Api\Manager\Booking;
use Api\Manager\Company;
use Application\Domain;
use Application\Helper\ExprDateTime;
use Application\Model\Email;
use Application\Model\User;
use Application\Payment\Gateway\DialogEzCash;
use Application\Payment\Gateway\IO\Response;
use Application\Payment\Gateway\MobitelMCash;
use Application\Payment\Gateway\SampathCcV1;
use Application\Sms\Template;
use Application\Util\Alert;
use Application\Util\Log;
use Zend\Mime\Mime;
use Zend\Mime\Part;
use Zend\Mvc\Controller\AbstractActionController;
use Zend\Session\Container;
use Zend\View\Model\ViewModel;
use Api\Client\Soap\Core\PaymentRefundCriteria;
use Api\Client\Soap\Core\PaymentRefundType;
use Api\Client\Soap\Core\VendorPaymentRefundMode;


use Data\Manager\SmsQueue;

class PaymentController extends AbstractActionController{

    const PAYGATEWWAY_TYPE_EZCASH = '';

    public function dialogezcashAction()
    {
        $response = $this->params()->fromPost('merchantReciept');
        $successMessage = array();
        $errorMessage = array();
        $paymentSuccess = false;
        $bookingResponse = null;

        /** @var DialogEzCash $oDialog */
        $oDialog = $this->getServiceLocator()->get('Payment\DialogEzCash');

        try {
            $decryptedResponse = $oDialog->getDecryptedResponse($response);

            //save response and redirect to proper domain
            return $this->moveToProperDomain($decryptedResponse);

        } catch (\Exception $e) {
            $errorMessage[] = $e->getMessage();
        }

        $view = new ViewModel();
        $view->setTemplate('application/payment/response');
        $view->setVariables(array(
            'paymentSuccess' => $paymentSuccess,
            'successMessage' => $successMessage,
            'errorMessage' => $errorMessage,
            'bookingResponse' => $bookingResponse,
        ));

        return $view;
    }

    public function paypalAction()
    {
        $response = $this->params()->fromQuery();
        $successMessage = array();
        $errorMessage = array();
        $paymentSuccess = false;
        $bookingResponse = null;

        /* @var $oPayPal \Application\Payment\Gateway\PayPalExpressCheckout */
        $oPayPal = $this->getServiceLocator()->get('Payment\PayPalExpressCheckout');
        try {
            $decryptedResponse = $oPayPal->getDecryptedResponse($response);
            //save response and redirect to proper domain
            return $this->moveToProperDomain($decryptedResponse);
        } catch (\Exception $e) {
            $errorMessage[] = $e->getMessage();
        }

        $view = new ViewModel();
        $view->setTemplate('application/payment/response');
        $view->setVariables(array(
            'paymentSuccess' => $paymentSuccess,
            'successMessage' => $successMessage,
            'errorMessage' => $errorMessage,
            'bookingResponse' => $bookingResponse,
        ));

        return $view;
    }

    public function mobitelCallbackAction()
    {
        $view = new ViewModel();
        $view->setTerminal(true);

        try {
            $amount = $_POST ["amount"];
            $invoice_id = $_POST ["invoice_id"];
            $mcash_reference_id = $_POST ["mcash_reference_id"];
            $customer_mobile = $_POST ["customer_mobile"];
            $status_code = $_POST ["status_code"];
            $encrypted_verification_password = $_POST['encrypted_verification_password'];

            /** @var MobitelMCash $oMobitel */
            $oMobitel = $this->getServiceLocator()->get('Payment\MobitelMCash');
            $oMobitel->mobitelCallbackHandler($invoice_id, $encrypted_verification_password);
        } catch (\Exception $e) {

        }
        exit;
    }

    /**
     * Mobitel callback
     * @return ViewModel
     */
    public function mobitelAction()
    {
        $invoiceId = $_REQUEST['invoice_id'];
        $payment =$_REQUEST['payment'];

        $successMessage = array();
        $errorMessage = array();
        $paymentSuccess = false;
        $bookingResponse = null;

        /* @var $oMobitel \Application\Payment\Gateway\MobitelMCash */
        $oMobitel = $this->getServiceLocator()->get('Payment\MobitelMCash');
        try {
            $responseData = array(
                'invoiceId' => $invoiceId,
                'payment' => $payment,
            );
            $decryptedResponse = $oMobitel->getDecryptedResponse($responseData);

            //save response and redirect to proper domain
            return $this->moveToProperDomain($decryptedResponse);

        } catch (\Exception $e) {
            $errorMessage[] = $e->getMessage();
        }

        $view = new ViewModel();
        $view->setTemplate('application/payment/response');
        $view->setVariables(array(
            'paymentSuccess' => $paymentSuccess,
            'successMessage' => $successMessage,
            'errorMessage' => $errorMessage,
            'bookingResponse' => $bookingResponse,
        ));

        return $view;
    }

    public function sampathipgAction()
    {
        $response = $_POST;
        $successMessage = array();
        $errorMessage = array();
        $paymentSuccess = false;
        $bookingResponse = null;

        /** @var \Application\Payment\Gateway\SampathCcV1 $oSampath */
        $oSampath = $this->getServiceLocator()->get('Payment\SampathCcV1');

        try {
            $decryptedResponse = $oSampath->getDecryptedResponse($response);

            //save response and redirect to proper domain
            return $this->moveToProperDomain($decryptedResponse);

        } catch (\Exception $e) {
            $errorMessage[] = $e->getMessage();
        }

        $view = new ViewModel();
        $view->setTemplate('application/payment/response');
        $view->setVariables(array(
            'paymentSuccess' => $paymentSuccess,
            'successMessage' => $successMessage,
            'errorMessage' => $errorMessage,
            'bookingResponse' => $bookingResponse,
        ));

        return $view;
    }

    public function sampathipgpaycorpAction()
    {
        $response = $_POST;
        $successMessage = array();
        $errorMessage = array();
        $paymentSuccess = false;
        $bookingResponse = null;
        $reqId = $_GET['reqid'];

        /** @var \Application\Payment\Gateway\SampathCcV2 $oSampath */
        $oSampath = $this->getServiceLocator()->get('Payment\SampathCcV2');

        try {
            $decryptedResponse = $oSampath->getDecryptedResponse($reqId);
			
			// Add payment record before redirecting. 
			// This is to fix 'payment deducted, booking is cancelled' due to redirection not happening properly.
			$this->addIPGPayment($decryptedResponse->getBookingReference(),$decryptedResponse,$successMessage,$errorMessage,$paymentSuccess,$bookingResponse);
            //save response and redirect to proper domain. Set the page to load in an iframe
            //return $this->moveToProperDomain($decryptedResponse, array( 'frame' => 1 ));

        } catch (\Exception $e) {
            $errorMessage[] = $e->getMessage();
        }

        $view = new ViewModel();
        $view->setTemplate('application/payment/response');
        $this->layout('layout/iframe');
        $view->setVariables(array(
            'paymentSuccess' => $paymentSuccess,
            'successMessage' => $successMessage,
            'errorMessage' => $errorMessage,
            'bookingResponse' => $bookingResponse,
        ));

        return $view;
    }

    public function hnbipgAction()
    {
        $response = $_POST;
        $successMessage = array();
        $errorMessage = array();
        $paymentSuccess = false;
        $bookingResponse = null;

        /** @var \Application\Payment\Gateway\HNBipg $oHNB */
        $oHNB = $this->getServiceLocator()->get('Payment\Hnb');

        try {
            $decryptedResponse = $oHNB->getDecryptedResponse($response);

            //save response and redirect to proper domain. Set the page to load in an iframe
            return $this->moveToProperDomain($decryptedResponse);

        } catch (\Exception $e) {
            $errorMessage[] = $e->getMessage();
        }

        $view = new ViewModel();
        $view->setTemplate('application/payment/response');
        $this->layout('layout/iframe');
        $view->setVariables(array(
            'paymentSuccess' => $paymentSuccess,
            'successMessage' => $successMessage,
            'errorMessage' => $errorMessage,
            'bookingResponse' => $bookingResponse,
        ));

        return $view;
    }

    /**
     * This is the action for all other websites other than BusBooking.lk
     */
    public function pgresponseAction()
    {
        $reference = $this->params()->fromQuery('ref');
        //optional params
        $isInIframe = (boolean) $this->params()->fromQuery('frame', false);

        $successMessage = array();
        $errorMessage = array();
        $paymentSuccess = false;
        $bookingResponse = null;
        $view = new ViewModel();
        $view->setTemplate('application/payment/response');
        $config = $this->getServiceLocator()->get('Config');
        //IPG response
        $ipgResponseKeyName = 'IPG_RESPONSE_'. $reference;

        try {
            //hide layout if loaded in an iframe
            if($isInIframe){
                $this->layout('layout/iframe');
            }

            /**
             * Get booking
             * @var $bookingManager \Api\Manager\Booking
             */
            $bookingManager = $this->getServiceLocator()->get('Api\Manager\Booking');

            /* @var $oDataStore \Zend\Cache\Storage\StorageInterface */
            $oDataStore = $this->getServiceLocator()->get('DataStore');
            $success = false;
            $response = $oDataStore->getItem($ipgResponseKeyName, $success);
            if (!$success) {
                //cache retrieval failed.
                $oBooking = $bookingManager->getBookingRefById($reference);

                //check if booking is confirmed and booking time is within one day
                if($oBooking->status->code==Booking::STATUS_CODE_CONFIRM
                    && (time() < ExprDateTime::getDateFromServices($oBooking->bookingTime)
                            ->add(new \DateInterval('P1D'))->getTimestamp())){
                    $bookingResponse = $oBooking;
                    $paymentSuccess = true;
                }else{
                    //send error message. Booking is not confirmed and we don't have ipg data.
                    $errorMessage[] = "Your reference cannot be found. Did you reload the page? Please call our hotline if your payment was succesfull.";
                }

            } else {
                //load data
                /** @var Response $ipgResponse */
                $ipgResponse = $response['response'];

                $referenceNo = $ipgResponse->getBookingReference();
                //check status
                if($ipgResponse->isSuccess()){
                    //payment was succesfull
                    $paymentSuccess = true;
                    $successMessage[] = 'Your seat is booked. eTicket No: '. $referenceNo;
                    /**
                     * Book the ticket
                     */
                    try {
                        $bookingResponse = $this->executeBooking($referenceNo,
                            $ipgResponse->getIpgReference(), $ipgResponse->getPaymentMode(),$ipgResponse->getPaidAmount());
                    } catch (\Exception $e) {
                        $errorMessage[] = 'Payment was deducted but there was an error while processing your request. <b>Please call our hotline immediately. ('. $config['system']['hotlinePhone'] .')</b> ';
                        $errorMessage[] = $e->getMessage();
                        //alert admin
                        try {
							//log this as a critical exception
							(new Log())->emerg($e, array('Payment was deducted but there was an error while processing your request.'));
			
                            $bookingResponse = $bookingManager->getBookingRefById($referenceNo);
                            //alert only if not confirm status
                            if($bookingResponse->status->code != Booking::STATUS_CODE_CONFIRM){
                                $oAlert = $this->getServiceLocator()->get('Application\Util\Alert');
                                $oAlert->sendEmailToAdmin('
                                <h2>Payment success, but adding payment record failed. Please check!</h2><pre></pre>
                                <h2>Error Messages</h2><pre>' . implode(__LINE__, $errorMessage) . '</pre>
                            ', 'PaymentSuccess Booking Failed : ' . $referenceNo);
                            }

                        } catch (\Exception $e) {
							//log this as a critical exception
							(new Log())->emerg($e, array('Payment failed alert to admin failed.'));
                        }
                    }
                }else{
                    //payment has failed. Call cancellation to release seats
                    $this->cancelTicketDueToError($referenceNo);
                    //get all errors
                    $respErrors = $ipgResponse->getErrors();
                    $errorMessage = array_merge_recursive($errorMessage, $respErrors);
                }

                //clear saved response
                $oDataStore->removeItem($ipgResponseKeyName);

                //send email to admin about payment
                try {
                    $this->alertAdmin($ipgResponse->getPaymentMode(), $reference, $successMessage, $errorMessage, $bookingResponse);
                } catch (\Exception $e) {

                }
            }
        } catch (\Exception $e) {
            $errorMessage[] = "There was an error while processing the request. Please call ". $config['system']['hotlinePhone'] ." immediately if your payment was succesfull.";
			//log this as a critical exception
			(new Log())->emerg($e, array("There was an error while processing the request. Please call ". $config['system']['hotlinePhone'] ." immediately if your payment was succesfull."));
		}

        $view->setVariables(array(
            'paymentSuccess' => $paymentSuccess,
            'successMessage' => $successMessage,
            'errorMessage' => $errorMessage,
            'bookingResponse' => $bookingResponse,
        ));

        return $view;
    }

	public function peopleipgAction()
    {
        //$response = $_POST;
        $successMessage = array();
        $errorMessage = array();
        $paymentSuccess = false;
        $bookingResponse = null;
        //$reqId = $_GET['reqid'];

        /** @var \Application\Payment\Gateway\PeopleIPG $oPeopleIPG */
        $oPeopleIPG = $this->getServiceLocator()->get('Payment\PeopleIPG');

        try {
            $decryptedResponse = $oPeopleIPG->getDecryptedResponse($_POST);

                        // Add payment record before redirecting. 
                        // This is to fix 'payment deducted, booking is cancelled' due to redirection not happening properly.
                        $this->addIPGPayment($decryptedResponse->getBookingReference(),$decryptedResponse,$successMessage,$errorMessage,$paymentSuccess,$bookingResponse);
            //save response and redirect to proper domain. Set the page to load in an iframe
            //return $this->moveToProperDomain($decryptedResponse, array( 'frame' => 1 ));

        } catch (\Exception $e) {
            $errorMessage[] = $e->getMessage();
        }

        $view = new ViewModel();
        $view->setTemplate('application/payment/response');
        //$this->layout('layout/iframe');
        $view->setVariables(array(
            'paymentSuccess' => $paymentSuccess,
            'successMessage' => $successMessage,
            'errorMessage' => $errorMessage,
            'bookingResponse' => $bookingResponse,
        ));

        return $view;
    }



    /**
     * @param Response $paymentResponse
     * @param array $getParams
     * @return \Zend\Http\Response
     * @throws \Exception
     */
    private function moveToProperDomain($paymentResponse, $getParams = array())
    {
        $reference = $paymentResponse->getBookingReference();

        /* @var $oDataStore \Zend\Cache\Storage\StorageInterface */
        $oDataStore = $this->getServiceLocator()->get('DataStore');

        //save decrypted response
        $oDataStore->setItem('IPG_RESPONSE_'. $reference, array(
            'response' => $paymentResponse
        ));

        $success = false;
        $originatedDomain = $oDataStore->getItem('IPG_REF_DOMAIN_'. $reference, $success);
        if(!$success){
            throw new \Exception('Reference failed. Please call our hotline if your payment was succesfull.');
        }
        //get params
        $aParams = array( 'ref' => $reference );
        //merge if params exists
        if(is_array($getParams) && !empty($getParams)){
            $aParams = array_merge($getParams, $aParams);
        }
        //redirect to the proper domain
        $url = $originatedDomain .'/app/payment/pgresponse?'. http_build_query($aParams);
        return $this->redirect()->toUrl($url)
            ->setStatusCode(301);
    }
	
	private function addIPGPayment($reference,$ipgResponse,&$successMessage,&$errorMessage,&$paymentSuccess,&$bookingResponse){
		
		
		$bookingManager = $this->getServiceLocator()->get('Api\Manager\Booking');
		$oBooking = $bookingManager->getBookingRefById($reference);
		$currentDomainName = Domain::getDomain();
		
		if(!$ipgResponse->isSuccess()){
			//payment has failed. Call cancellation to release seats
			$this->cancelTicketDueToError($reference);
			//get all errors
			$respErrors = $ipgResponse->getErrors();
			$errorMessage = array_merge_recursive($errorMessage, $respErrors);
		}else{
			try{
				$IPG_paidAmount = $ipgResponse->getPaidAmount()->getPaymentAmount()/100;
				
				if($currentDomainName==\Application\Domain::NAME_SLTB){
					$IPG_paidAmount = $ipgResponse->getPaidAmount()/100;
				}else {
					$IPG_paidAmount = $ipgResponse->getPaidAmount()->getPaymentAmount()/100;
				}				
				$bookingResponse = $this->executeBooking($reference, $ipgResponse->getIpgReference(), $ipgResponse->getPaymentMode(),$ipgResponse->getActualCurrency(),$IPG_paidAmount);
				
				//$paymentAudit = $this->getServiceManager()->get('Data\Manager\PaymentAudit');
				$paymentAudit = $this->getServiceLocator()->get('Data\Manager\PaymentAudit');
                $bookingAudData = $paymentAudit->getBookingAudData($reference);
				
				error_log($bookingAudData);
				if(strpos($bookingAudData['our_reference'], 'NDB_CARD_PROMO') !== false){
				// $extraData = $ipgResponse->getExtraData();
				// error_log($extraData);
				// if($extraData['NDB_CARD_PROMO']===true){
					$amount = ($oBooking->chargeable - $IPG_paidAmount  );
					if( getenv('APP_ENV') =='development'){
						$amount = round($amount,2);
					}
					$actualCurrency = 'LKR';
					$bookingId = $oBooking->id;
					$mode = PaymentRefundMode::Card;
					$reference =  $reference.'_NDB_PROMO_10_OFF';
					$oPayment = $this->getServiceLocator()->get('Api\Manager\Booking\Payment');
					$bookingId = $oBooking->id;
					$paymentResponse = $oPayment->addPayment($amount, $bookingId, $actualCurrency, $mode, $reference);
				}
				
				
				//payment was succesfull
				$paymentSuccess = true;
				$successMessage[] = 'Your seat is booked. eTicket No: '. $reference;
			}catch(\Exception $e) {
				$errorMessage[] = 'Payment was deducted but there was an error while processing your request. <b>Please call our hotline immediately. ('. $config['system']['hotlinePhone'] .')</b> ';
				$errorMessage[] = $e->getMessage();
				//alert admin
				try {
					//log this as a critical exception
					(new Log())->emerg($e, array('Payment was deducted but there was an error while processing your request.'));
	
					$bookingResponse = $bookingManager->getBookingRefById($reference);
					//alert only if not confirm status
					if($bookingResponse->status->code != Booking::STATUS_CODE_CONFIRM){
						$oAlert = $this->getServiceLocator()->get('Application\Util\Alert');
						$oAlert->sendEmailToAdmin('
						<h2>Payment success, but adding payment record failed. Please check!</h2><pre></pre>
						<h2>Error Messages</h2><pre>' . implode(__LINE__, $errorMessage) . '</pre>
					', 'PaymentSuccess Booking Failed : ' . $reference);
					}

				} catch (\Exception $e) {
					//log this as a critical exception
					(new Log())->emerg($e, array('Payment failed alert to admin failed.'));
				}
			}
			try {
				$this->alertAdmin($ipgResponse->getPaymentMode(), $reference, $successMessage, $errorMessage, $bookingResponse);
			} catch (\Exception $e) {
				(new Log())->emerg($e, array('Payment  alert to admin failed.'));
			}
		}
		
	}
    /**
     * Set booking as done
     * @param $transactionId
     * @param $referenceId
     * @param $payMode
     * @param string $payCurrency
     * @return \Api\Client\Soap\Core\Booking
     * @throws \Exception
     */
    private function executeBooking($transactionId, $referenceId, $payMode, $payCurrency = 'LKR',$amountPaid)
    {
        //stip down unwanted chars
        $referenceId = preg_replace('/[\x00-\x1F\x7F]/', '', trim($referenceId));
        $sl = $this->getServiceLocator();

        //get booking details
        /**
         * @var $bookingManager \Api\Manager\Booking
         */
        $bookingManager = $this->getServiceLocator()->get('Api\Manager\Booking');
        $oBooking = $bookingManager->getBookingRefById($transactionId);

		// Below is a work arround to allow duplicate calls made by IPG during payment confirmation.
		// Check if the booking has a payment record with matching reference sent by IPG
		// If there's a match  , just return booking object. 
		
		
		if (isset($oBooking->payments)){
			foreach($oBooking->payments as $paymentObj){
				if($paymentObj->reference==$referenceId){
					return $oBooking; 
				}
			}
		}
		
		// $amount = $oBooking->chargeable;
		 
        //add payment
        /**
         * @var $oPayment \Api\Manager\Booking\Payment
         */
        $oPayment = $sl->get('Api\Manager\Booking\Payment');
        $bookingId = $oBooking->id;
        $paymentResponse = $oPayment->addPayment($amountPaid, $bookingId, $payCurrency, $payMode, $referenceId,$oBooking->chargeable);

        /**
         * Send email and Sms
         */
        try {
            $this->sendTicketEmailSms($oBooking);
        } catch (\Exception $e) {
            throw new \Exception("Email/Sms could not be sent due to an error. But your payment was processed. ". $e->getMessage());
        }

        return $oBooking;
    }

    /**
     * @param \Api\Client\Soap\Core\Booking $oconfirmResponse
     * @throws \Exception
     */
    private function sendTicketEmailSms($oconfirmResponse)
    {
        $bookingNo      = $oconfirmResponse->reference;
        $clientTelephone= $oconfirmResponse->client->mobileTelephone;
        $clientEmail    = $oconfirmResponse->client->email;
        $seatNumbers    = array();

        try {
            /**
             * get sms object
             * @var $oSms \Application\Alert\Sms
             */
            // $oSms = $this->getServiceLocator()->get('Alert\Sms');
            // $oSms->sendBookingSms($oconfirmResponse, Template::BOOKING_SUCCESS);
			
			// Add to SMS queue table 
			$smsQueue = $this->getServiceLocator()->get('Data\Manager\SmsQueue');
			$test = $smsQueue->addToSMSQueue($oconfirmResponse->reference);
			
			
        } catch (\Exception $e) {
            $alertBody = '
                <h2>Booking Reference</h2><pre>'. $bookingNo .'</pre>
                <h2>SMS failed due to</h2><pre>'. $e->getMessage() .'</pre>
                <h2>SMS sent to</h2><pre>'. $clientTelephone .'</pre>
                ';
            $oAlert = $this->getServiceLocator()->get('Application\Util\Alert');
            $oAlert->sendEmailToAdmin($alertBody, 'ExpressBot - Sending sms failed on booking');
        }

        try {
            /**
             * Send email
             * @var $oEmail \Application\Alert\Email
             */
            // $oEmail = $this->getServiceLocator()->get('Alert\Email');
            // $oEmail->sendBookingSuccessEmail($oconfirmResponse);
			
			// Add to EMAIL queue table 
			$emailQueue = $this->getServiceLocator()->get('Data\Manager\EmailQueue');
			$test = $emailQueue->addToEmailQueue($oconfirmResponse->reference); 
			
        } catch (\Exception $e) {
            throw $e;
        }

    }

    /**
     * Cancel booking due to error from PG
     * @param $referenceNo
     */
    private function cancelTicketDueToError($referenceNo) {
        try {
            //get booking details
            /**
             * @var $bookingManager \Api\Manager\Booking
             */
            $bookingManager = $this->getServiceLocator()->get('Api\Manager\Booking');
            $oBooking = $bookingManager->getBookingRefById($referenceNo);

            //do not cancel confirmed bookings
            if($oBooking->status->code==Booking::STATUS_CODE_CONFIRM){
                return false;
            }

            /**
             * get cancellation
             * @var $cancellation \Api\Manager\Booking\Cancellation
             */
            $cancellation = $this->getServiceLocator()->get('Api\Manager\Booking\Cancellation');
            $cancellation->cancelTicket($oBooking->id, false,
                CancellationCause::NonPayment, 'Cancellation due to payment failure by payment gateway.');
        } catch (\Exception $e) {
            //log error
//            $log = new Log();
//            $log->emerg($e, array(
//                'Booking_Cancel' => 'Exception in booking cancel in payment gateway return. Booking Ref '. $referenceNo
//            ));
        }
    }

    private function alertAdmin($paymentMode, $bookingReference, $successMessage, $errorMessage, $booking) {
        /** @var \Api\Client\Soap\Core\Booking $booking */
        //check if booking is not from admins
        if(is_null($booking) ||
            !in_array(strtoupper($booking->client->nic), Alert::$skipNICs)){
            $oAlert = $this->getServiceLocator()->get('Application\Util\Alert');
            $oAlert->sendEmailToAdmin('
                <h2>Success Messages</h2><pre>'. implode(__LINE__, $successMessage) .'</pre>
                <h2>Error Messages</h2><pre>'. implode(__LINE__, $errorMessage) .'</pre>
            ', 'ExpressBot : '. $paymentMode .' Payment - '. $bookingReference);
        }

    }

    public function testalertAction(){
//        $reference = $this->params()->fromQuery('ref');
//
//        /**
//         * @var $bookingManager \Api\Manager\Booking
//         */
//        $bookingManager = $this->getServiceLocator()->get('Api\Manager\Booking');
//        $oBooking = $bookingManager->getBookingRefById($reference);
//        try {
//            $this->sendTicketEmailSms($oBooking);
//        } catch (\Exception $e) {
//            echo $e->getMessage();
//        }
        die('Done');
    }
	
	public function payhereviewAction(){
		if(ISSET($_GET['order_id'])){
			$orderId = $_GET['order_id'];
			$paymentAudit = $this->getServiceManager()->get('Data\Manager\PaymentAudit');
            $booking_audit_data = $paymentAudit->getBookingAudData($orderId);
			 
			$paymentSuccess = ($booking_audit_data['status']==2);
			$successMessage ="";
			if($paymentSuccess){
				$successMessage = $booking_audit_data['message_from_provider'];
			}else{
				$errorMessage = $booking_audit_data['message_from_provider'];
			}
			
			$bookingManager = $this->getServiceLocator()->get('Api\Manager\Booking');
			$bookingResponse = $bookingManager->getBookingRefById($transactionId);
			
			$view = new ViewModel();
			$view->setTemplate('application/payment/response');
			$view->setVariables(array(
				'paymentSuccess' => $paymentSuccess,
				'successMessage' => $successMessage,
				'errorMessage' => $errorMessage,
				'bookingResponse' => $bookingResponse,
			));

			return $view;
		}
	}
	public function payhereAction(){
		//$response = $_POST;
        //$oPayHere = $this->getServiceLocator()->get('Payment\PayHere');
		
		$merchant_id = $_POST['merchant_id'];
		$order_id = $_POST['order_id'] ;
		$payment_id = $_POST['payment_id'] ;
		$payhere_amount = $_POST['payhere_amount'] ;
		$payhere_currency = $_POST['payhere_currency'];  
		$status_code = $_POST['status_code']; 
		$md5sig = $_POST['md5sig'] ;
		$custom_1 = $_POST['custom_1'] ;
		$custom_2 = $_POST['custom_2'];
		$method = $_POST['method']  ;
		$status_message = $_POST['status_message'];
		
		try{
            $paymentAudit = $this->getServiceManager()->get('Data\Manager\PaymentAudit');
            $paymentAudit->updatePaymentAudit($order_id,$payment_id,$status_code,$status_message,"BBK");
        }catch (\Exception $e){
        
		}
		if ( $status_code == 2)  {
			$bookingResponse = $this->executeBooking($order_id, $payment_id, $method,$payhere_currency,$payhere_amount);
		}
	}
}