<?php

namespace Application\Payment\Gateway;
 use Api\Client\Soap\Core\PaymentRefundMode;
    use Application\Payment\Gateway\IO\Request;
    use Application\Payment\Gateway\IO\Response;
    use Data\Manager\PaymentAudit;
    use Application\Domain;

    class PeopleIPG extends Base
    {
        const CONFIG_NAME       = 'people-ipg';
        const PAYSTATUS_SUCCESS = '1';
		private $_payConfig = array();
		private $returnUrl = null;


		public function __construct($serviceManager)
		{
				parent::__construct($serviceManager);

				//set in class payment config
				//$conf = $this->getConfig(self::CONFIG_NAME);
				$this->_payConfig = $this->getConfig(self::CONFIG_NAME);
		}

		public function getEncryptedRequest($transactionId, $transactionAmount, $orderDescription = 'Payment for SLTB')
		{
			$returnUrl      = 'https://' . Domain::getDomain() . $this->_payConfig['MerRespURL'];
			$amount         = str_pad(strval(floatval($transactionAmount)*100), 12, "0", STR_PAD_LEFT);
			$messageHash = $this->_payConfig['Password'] . $this->_payConfig['MerID'] . $this->_payConfig['AcqID']. strval($transactionId) . $amount . $this->_payConfig['PurchaseCurrency'];
			$message_hash= base64_encode(SHA1($messageHash,TRUE));
			$fields = array(
					'Version' => $this->_payConfig['Version'],
					'MerID' => $this->_payConfig['MerID'],
					'AcqID' => $this->_payConfig['AcqID'],
					'MerRespURL' => $returnUrl,
					'PurchaseCurrency' => $this->_payConfig['PurchaseCurrency'],
					'PurchaseCurrencyExponent' => $this->_payConfig['PurchaseCurrencyExponent'],
					'SignatureMethod' => $this->_payConfig['SignatureMethod'],
					'Signature' => $message_hash,
					'PurchaseAmt' => $amount,
					'OrderID' => $transactionId,
			);
			$html = '<form action="'. $this->_payConfig['endPoint'] .'" method="post" id="__form__">';
			foreach ($fields as $a => $b) {
					$html .= "<input type='hidden' name='".$a."' value='".$b."'>";
			}
			$html .= '</form>';
			$request = new Request();
			$request->setRequestProcessType(Request::TYPE_FORMSUBMIT)
					->setRequestPayload($html);

			return $request;
		}

		
		public function getDecryptedResponse($post)
		{
			$password       = $this->_payConfig['Password'];
			$merchantNo     = $this->_payConfig['MerID'];
			$acquirerId     = $this->_payConfig['AcqID'];
			$bookingReference = $post['OrderID'];
			$responseCode   = $post['ResponseCode'];
			$reasonCode     = $post['ReasonCode'];
			$reasonCodeDesc = $post['ReasonCodeDesc'];
			$signature      = $post['Signature'];
			$signatureMethod = $post['SignatureMethod'];
			//hnb doesnt return their ID, so we'll create one for ourselves
			$transactionId = $post['ReferenceNo'];//$bookingReference;

			$messageHash = $password . $merchantNo . $acquirerId . strval($bookingReference);
			$ourHash = base64_encode(sha1($messageHash));

			$hashMatch = false;
			//check if payment was succesfull
			if ($signature==$ourHash){
					$hashMatch=true;
			} else {
					$hashMatch=false;
			}

			//build response object
			$oResponse = new Response();
			$oResponse->setPaymentMode(PaymentRefundMode::Card)
					->setBookingReference($bookingReference)
					->setIpgReference($transactionId);


			//if payment success
			//if($hashMatch && $responseCode==self::PAYSTATUS_SUCCESS){
			if($responseCode==self::PAYSTATUS_SUCCESS){
				//payment success. Get the amount from booking since hnb doesnt support it
				/** @var $bookingManager \Api\Manager\Booking */
				$bookingManager = $this->getServiceManager()->get('Api\Manager\Booking');
				$oBooking = $bookingManager->getBookingRefById($bookingReference);
				//set response object
				$oResponse->setSuccess( true );
				$oResponse->setPaidAmount($oBooking->chargeable*100);
			}
			else{
				//failed. Set error
				$oResponse->setSuccess( false );
				$oResponse->setError('Your payment has failed by Payment gateway. You have to try again. '. $responseCode);
				$oResponse->setError($reasonCodeDesc);
			}

			return $oResponse;
		}
}

?>