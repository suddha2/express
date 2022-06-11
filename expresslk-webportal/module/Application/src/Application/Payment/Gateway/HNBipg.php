<?php
/**
 * Created by PhpStorm.
 * User: Udantha
 * Date: 3/4/17
 * Time: 10:36
 */

namespace Application\Payment\Gateway;

use Api\Client\Soap\Core\PaymentRefundMode;
use Application\Domain;
use Application\Payment\Gateway\IO\Request;
use Application\Payment\Gateway\IO\Response;

class HNBipg extends Base
{
//configuration name in main config file
    const CONFIG_NAME       = 'hnb-ipg';

    const PAYSTATUS_SUCCESS = '1';

    private $_payConfig = array();
    private $returnUrl = null;

    public function __construct($serviceManager)
    {
        parent::__construct($serviceManager);

        //set in class payment config
        $conf = $this->getConfig(self::CONFIG_NAME);
        $this->_payConfig = $conf['sentry'];
    }

    /**
     * @param $transactionId
     * @param $transactionAmount
     * @param string $orderDescription
     * @return Request
     */
    public function getEncryptedRequest($transactionId, $transactionAmount, $orderDescription = 'Payment for BusBooking')
    {
        $endPoint   = $this->_payConfig['endPoint'];
        $password     = $this->_payConfig['password'];
        $merchantNo     = $this->_payConfig['merchantNo'];
        $acquirerId     = $this->_payConfig['acquirerId'];
        $currency     = $this->_payConfig['currency'];
        $version     = $this->_payConfig['version'];
        /**
         * @important HNB IPG requires to have https enabled. So initial redirection happens to busbooking.lk website since
         * it has https. From there PaymentController will redirect to correct website
         */
        //build return url from what's in configs
        $returnUrl      = 'https://' . $this->_payConfig['domainName'] . $this->getReturnUrl();
        $amount         = str_pad(strval(floatval($transactionAmount)*100), 12, "0", STR_PAD_LEFT);

        $messageHash = $password . $merchantNo . $acquirerId . strval($transactionId) . $amount . $currency;
        $message_hash = base64_encode(pack('H*', sha1($messageHash)));

        $fields = array(
            'Version' => $version,
            'MerID' => $merchantNo,
            'AcqID' => $acquirerId,
            'MerRespURL' => $returnUrl,
            'PurchaseCurrency' => $currency,
            'PurchaseCurrencyExponent' => '2',
            'OrderID' => $transactionId,
            'SignatureMethod' => 'SHA1',
            'Signature' => $message_hash,
            'CaptureFlag' => 'M',
            'PurchaseAmt' => $amount,
            'ShipToFirstName' => Domain::getDomain(),
            'ShipToLastName' => 'BusBooking',
        );

        $html = '<form action="'. $endPoint .'" method="post" id="__form__">';
        foreach ($fields as $a => $b) {
            $html .= "<input type='hidden' name='".$a."' value='".$b."'>";
        }
        $html .= '</form>';

        $request = new Request();
        $request->setRequestProcessType(Request::TYPE_FORMSUBMIT)
            ->setRequestPayload($html);

        return $request;
    }

    /**
     * Decrypt and retrieve values
     * @param $post
     * @return Response
     */
    public function getDecryptedResponse($post)
    {
        $password       = $this->_payConfig['password'];
        $merchantNo     = $this->_payConfig['merchantNo'];
        $acquirerId     = $this->_payConfig['acquirerId'];
        $bookingReference = $post['OrderID'];
        $responseCode   = $post['ResponseCode'];
        $reasonCode     = $post['ReasonCode'];
        $reasonCodeDesc = $post['ReasonCodeDesc'];
        $signature      = $post['Signature'];
        $signatureMethod = $post['SignatureMethod'];
        //hnb doesnt return their ID, so we'll create one for ourselves
        $hnbTransactionId = $bookingReference;

        $messageHash = $password . $merchantNo . $acquirerId . strval($bookingReference);
        $ourHash = base64_encode(pack('H*', sha1($messageHash)));

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
            ->setIpgReference($hnbTransactionId);

        //if payment success
        if($hashMatch && $responseCode==self::PAYSTATUS_SUCCESS){
            //payment success. Get the amount from booking since hnb doesnt support it
            /** @var $bookingManager \Api\Manager\Booking */
            $bookingManager = $this->getServiceManager()->get('Api\Manager\Booking');
            $oBooking = $bookingManager->getBookingRefById($bookingReference);
            //set response object
            $oResponse->setSuccess( true );
            $oResponse->setPaidAmount($oBooking->chargeable);
        }
        else{
            //failed. Set error
            $oResponse->setSuccess( false );
            $oResponse->setError('Your payment has failed by Payment gateway. You have to try again. '. $responseCode);
            $oResponse->setError($reasonCodeDesc);
        }

        return $oResponse;
    }

    /**
     * Returns the IPG return URL
     * @return string
     */
    public function getReturnUrl()
    {
        return !empty($this->returnUrl)? $this->returnUrl : $this->_payConfig['returnUrl'];
    }

    /**
     * @param string $returnUrl
     */
    public function setReturnUrl($returnUrl)
    {
        $this->returnUrl = $returnUrl;
    }

}