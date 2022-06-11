<?php
/**
 * This global namsespace is due to Sampath IPG does not support namespaces.
 */
namespace {
    $currentDirectory = realpath(dirname(__FILE__));

    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client/GatewayClient.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.config/ClientConfig.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.component/RequestHeader.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.component/CreditCard.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.component/TransactionAmount.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.component/Redirect.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.facade/BaseFacade.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.facade/Payment.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.payment/PaymentInitRequest.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.payment/PaymentInitResponse.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.payment/PaymentCompleteRequest.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.payment/PaymentCompleteResponse.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.root/PaycorpRequest.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.root/PaycorpResponse.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.utils/IJsonHelper.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.helpers/PaymentInitJsonHelper.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.helpers/PaymentCompleteJsonHelper.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.utils/HmacUtils.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.utils/CommonUtils.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.utils/RestClient.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.enums/TransactionType.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.enums/Version.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.enums/Operation.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.facade/Vault.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.facade/Report.php';
    include_once $currentDirectory . '/SampathIPG/PayCorp/au.com.gateway.client.facade/AmexWallet.php';

}

namespace Application\Payment\Gateway{

    use Api\Client\Soap\Core\PaymentRefundMode;
    use Application\Payment\Gateway\IO\Request;
    use Application\Payment\Gateway\IO\Response;
    use Data\Manager\PaymentAudit;

    class SampathCcV2 extends Base
    {
        const CONFIG_NAME       = 'sampath-ipg';

        const PAYSTATUS_SUCCESS = '00'; //success code returned by IPG on payment success
        const PAYSTATUS_NA = '91';
        const PAYSTATUS_NF = '92'; //BANK NOT FOUND
        const PAYSTATUS_CE = 'A4'; //CONNECTION ERROR
        const PAYSTATUS_SE = 'C5'; //SYSTEM ERROR
        const PAYSTATUS_T3 = 'T3'; //TRANSACTION REJECTED
        const PAYSTATUS_T4 = 'T4'; //CONTACT ACQUIRING BANK
        const PAYSTATUS_U9 = 'U9'; //NO RESPONSE
        const PAYSTATUS_X1 = 'X1'; //GATEWAY UNAVAILABLE
        const PAYSTATUS_X3 = 'X3'; //NETWORK ERROR
        const PAYSTATUS_OT = '-1'; //<various>
        const PAYSTATUS_C0 = 'C0'; //CARTRIDGE ERROR
        const PAYSTATUS_A6 = 'A6'; //SERVER BUSY

        private $_payConfig = array();

        public function __construct($serviceManager)
        {
            parent::__construct($serviceManager);

            //set in class payment config
            $conf = $this->getConfig(self::CONFIG_NAME);
			$env = getenv('APP_ENV') ?: 'production';
			
			if ($env == 'development') {
				$this->_payConfig = $conf['v2-Test'];
				return ;
			}else{
				$this->_payConfig = $conf['v2'];
			}
			// IPG FOR SLTB WHITE LABEL SITE
			$currentDomainName = \Application\Domain::getDomain();
			if($currentDomainName==\Application\Domain::NAME_SLTB){
				$this->_payConfig = $conf['v2-SLTB'];
			}
        }

        /**
         * @param $transactionId
         * @param $amount
         * @param string $orderDescription
         * @return Request
         */
        public function getEncryptedRequest($transactionId, $amount, $orderDescription = '',$ndb_promo = false)
        {
			// Remove decimals in test environment.
			if(getenv('APP_ENV')=="development"){
				#error_log(print_r("env is development ", TRUE));
				$amount = (int)$amount;
			}
			
			#error_log(print_r("Total Amount : ".$amount, TRUE)); 
			#error_log(print_r("Rounded Amount : ".$amount, TRUE)); 
			
			//amount should be in cents
            $amount = $amount*100;
            //get scheme
            $scheme = $this->getServiceManager()->get('Request')->getUri()->getScheme();
            //build return url from what's in configs
            $returnUrl          = $scheme . '://' . $this->_config['system']['serverName'] . $this->_payConfig['returnUrl'];

            /*------------------------------------------------------------------------------
            STEP1: Build ClientConfig object
            ------------------------------------------------------------------------------*/
            $ClientConfig = new \ClientConfig();
            $ClientConfig->setServiceEndpoint($this->_payConfig['endPoint']);
            $ClientConfig->setAuthToken($this->_payConfig['authToken']);
            $ClientConfig->setHmacSecret($this->_payConfig['HMACSecret']);
            $ClientConfig->setValidateOnly(FALSE);
			
			
            /*------------------------------------------------------------------------------
            STEP2: Build Client object
            ------------------------------------------------------------------------------*/
            $Client = new \GatewayClient($ClientConfig);
            /*------------------------------------------------------------------------------
            STEP3: Build PaymentInitRequest object
            ------------------------------------------------------------------------------*/
			$totalAmount = $amount;
			
            $initRequest = new \PaymentInitRequest();
			// Set Client ID for Card Promotions
			if($ndb_promo && getenv('APP_ENV')=="production" ){  
				$conf = $this->getConfig(self::CONFIG_NAME);
				$this->_payConfig = $conf['v2-NDB-PROMO'];
			}
		 
			 
			
            $initRequest->setClientId($this->_payConfig['clientId']);
			
            $initRequest->setTransactionType(\TransactionType::$PURCHASE);
            $initRequest->setClientRef($transactionId);
            $initRequest->setComment($orderDescription);
            $initRequest->setTokenize(FALSE);
            //$initRequest->setExtraData(array("ADD-KEY-1" => "ADD-VALUE-1", "ADD-KEY-2" => "ADD-VALUE-2"));
            
			// sets transaction-amounts details (all amounts are in cents)
            $transactionAmount = new \TransactionAmount();
            $transactionAmount->setTotalAmount($totalAmount);
            $transactionAmount->setServiceFeeAmount(0);
            $transactionAmount->setPaymentAmount($amount);
            $transactionAmount->setCurrency("LKR");
            $transactionAmount->setWithholdingAmount(0);
            
			$initRequest->setTransactionAmount($transactionAmount);
			$ourRef = $transactionId;
			if($ndb_promo){
				//$initRequest->setExtraData(array("NDB_CARD_PROMO" => true));
				$ourRef .= "-NDB_CARD_PROMO";
				
			}
			// sets redirect settings
            $redirect = new \Redirect();
            $redirect->setReturnUrl($returnUrl);
            //$redirect->setCancelUrl($returnUrl);
            $redirect->setReturnMethod("GET");
            $initRequest->setRedirect($redirect);
			
			$initParams = array("ClientId"=>$initRequest->getClientId(),
								"TransactionType"=>$initRequest->getTransactionType(),
								"ClientRef"=>$initRequest->getClientRef(),
								"Comment"=>$initRequest->getComment(),
								"TotalAmount"=>$initRequest->getTransactionAmount()->getTotalAmount(),
								"PaymentAmount"=>$initRequest->getTransactionAmount()->getPaymentAmount(),
								"ReturnUrl"=>$initRequest->getRedirect()->getReturnUrl(),
								"ExtraData"=>$initRequest->getExtraData()
								);
			 
			$initParams = json_encode($initParams, JSON_FORCE_OBJECT);
			$paymentAudit = $this->getServiceManager()->get('Data\Manager\PaymentAudit');
			$paymentAudit->savePaymentAudit($transactionId,self::CONFIG_NAME,$ourRef,($transactionAmount->getTotalAmount())/100,"LKR",$orderDescription,$initParams,"BBK");
		
			
			
            /*------------------------------------------------------------------------------
            STEP4: Process PaymentInitRequest object
            ------------------------------------------------------------------------------*/
            $initResponse = $Client->payment()->init($initRequest);
			
            $request = new Request();
            $request->setRequestProcessType(Request::TYPE_IFRAMEURL)
                ->setRequestPayload($initResponse->getPaymentPageUrl());

            try{
                /** @var PaymentAudit $paymentAudit */
                // $paymentAudit = $this->getServiceManager()->get('Data\Manager\PaymentAudit');
                // $paymentAudit->savePaymentAudit($transactionId,self::CONFIG_NAME,$transactionId,($transactionAmount->getTotalAmount())/100,"LKR",$orderDescription,$request->getRequestPayload(),"BBK");
				$paymentAudit->updateProviderInvokeTime($transactionId,$request->getRequestPayload());
            }catch (\Exception $e){
            }

            return $request;
        }

        public function getDecryptedResponse($reqId)
        {
            /*------------------------------------------------------------------------------
            STEP1: Build ClientConfig object
            ------------------------------------------------------------------------------*/
            $ClientConfig = new \ClientConfig();
            $ClientConfig->setServiceEndpoint($this->_payConfig['endPoint']);
            $ClientConfig->setAuthToken($this->_payConfig['authToken']);
            $ClientConfig->setHmacSecret($this->_payConfig['HMACSecret']);
            $ClientConfig->setValidateOnly(FALSE);
            /*------------------------------------------------------------------------------
            STEP2: Build Client object
            ------------------------------------------------------------------------------*/
            $Client = new \GatewayClient($ClientConfig);
            /*------------------------------------------------------------------------------
            STEP3: Build PaymentCompleteRequest object
            ------------------------------------------------------------------------------*/
            $completeRequest = new \PaymentCompleteRequest();
            $completeRequest->setClientId($this->_payConfig['clientId']);
            $completeRequest->setReqid($reqId);
            /*------------------------------------------------------------------------------
            STEP4: Process PaymentCompleteRequest object
            ------------------------------------------------------------------------------*/
            /** @var \PaymentCompleteResponse $completeResponse */
            $completeResponse = $Client->payment()->complete($completeRequest);
            /*------------------------------------------------------------------------------
            STEP5: Process PaymentCompleteResponse object
            ------------------------------------------------------------------------------*/

            //build response object
            $oResponse = new Response();
            $oResponse->setPaymentMode(PaymentRefundMode::Card)
                ->setBookingReference($completeResponse->getClientRef())
                ->setIpgReference($completeResponse->getTxnReference());

            $responseCode = strval($completeResponse->getResponseCode());

            try{
                /** @var PaymentAudit $paymentAudit */
                $paymentAudit = $this->getServiceManager()->get('Data\Manager\PaymentAudit');
                $paymentAudit->updatePaymentAudit($completeResponse->getClientRef(),$completeResponse->getTxnReference(),$completeResponse->getResponseCode(),$completeResponse->getResponseText(),"BBK"
				,$completeResponse->getCreditCard(),$completeResponse->getAuthCode(),$completeResponse->getSettlementDate(),$completeResponse->getFeeReference());
            }catch (\Exception $e){
            }

            //if payment success
            if($responseCode==strval(self::PAYSTATUS_SUCCESS)){
                $oResponse->setSuccess( true );
                $oResponse->setPaidAmount($completeResponse->getTransactionAmount());
            }
            else{
                //failed. Set error
                $oResponse->setSuccess( false );

                if (in_array($responseCode,array('57','01','41','43','91'))){
					$oResponse->setError('contact_client_bank');
				}else if(in_array($responseCode, array(self::PAYSTATUS_A6, self::PAYSTATUS_C0, self::PAYSTATUS_CE,
                    self::PAYSTATUS_NF, self::PAYSTATUS_OT, self::PAYSTATUS_SE, self::PAYSTATUS_T3,
                    self::PAYSTATUS_T4, self::PAYSTATUS_U9, self::PAYSTATUS_X1, self::PAYSTATUS_X3))){ //self::PAYSTATUS_NA,
                    //bank offline
                    $oResponse->setError('Banking Network Temporarily Unavailable, Please try again later.');
                } 
                else{
                    $oResponse->setError('Payment Declined - Please try an alternative card.');
                }
            }

            return $oResponse;
        }
    }

}


