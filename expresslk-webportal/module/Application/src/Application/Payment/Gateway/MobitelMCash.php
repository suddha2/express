<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 2/6/15
 * Time: 11:43 AM
 */

namespace Application\Payment\Gateway;

use Api\Client\Soap\Core\PaymentRefundMode;
use Application\Payment\Gateway\IO\Request;
use Application\Payment\Gateway\IO\Response;

class MobitelMCash extends Base{

    //configuration name in main config file
    const CONFIG_NAME       = 'mobitel-ipg';

    const STATUS_SUCCESS = 1000;

    /**
     * @param $transactionId
     * @param $transactionAmount
     * @param string $orderDescription
     * @return Request
     * @throws \Exception
     */
    public function getEncryptedRequest($transactionId, $transactionAmount, $orderDescription = '')
    {
        $dConfig = $this->getConfig(self::CONFIG_NAME);

        $token_url = $dConfig['tokenUrl'];
        $redirect_url = $dConfig['redirectUrl'];

        // Create map with request parameters
        $params = array (
            'merchant_id'           => $dConfig['merchantId'],
            'merchant_invoice_id'   => $transactionId,
            'merchant_mobile'       => $dConfig['merchantMobile'],
            'token_pwd'             => $dConfig['tokenPassword'],
            'customer_mobile'       => $transactionId,
            'amount'                => $transactionAmount
        );
        // Build Http query using params
        $query = http_build_query ($params);
        // Create Http context details
        $contextData = array (
            'method' => 'POST',
            'header' => "Content-Type: application/x-www-form-urlencoded\r\n"."Connection: close\r\n"."Content-Length: ".strlen($query)."\r\n",
            'content'=> $query
        );

        // Create context resource for our request
        $context = stream_context_create (array ( 'http' => $contextData ));
        // Read page rendered as result of your POST request
        $token = file_get_contents ($token_url,false,$context);
        //break if token fails
        if($token===false){
            throw new \Exception('Phone number could not be validated. Please try again.');
        }

        $fields = array(
            't_id' => ($token)
        );
        $html = '<form action="'. $redirect_url .'" method="post" id="form">';
        foreach ($fields as $a => $b) {
            $html .= "<input type='hidden' name='".htmlentities($a)."' value='".htmlentities($b)."'>";
        }
        $html .= '</form>';

        $request = new Request();
        $request->setRequestProcessType(Request::TYPE_FORMSUBMIT)
            ->setRequestPayload($html);

        return $request;
    }

    /**
     * Server callback handler
     * @param $invoiceId
     * @param $encrypted_verification_password
     */
    public function mobitelCallbackHandler($invoiceId, $encrypted_verification_password)
    {
        $mConfig = $this->getConfig(self::CONFIG_NAME);

        $merchant_id = $mConfig['merchantId'];
        $token_pwd = $mConfig['tokenPassword'];
        $s = $token_pwd . $merchant_id;

        $signature = base64_encode ( hash ( "sha256", $s, True ) );
        $password1 = substr ( $signature, 0, 32 );
        $iv = $mConfig['initializeVecto']; // initialization vecto

        $cipher = mcrypt_module_open ( MCRYPT_RIJNDAEL_128, '', 'cbc', '' );
        mcrypt_generic_init ( $cipher, $password1, $iv );

        //$plain_verification_password needs to be saved
        $plain_verification_password = mdecrypt_generic ( $cipher, base64_decode (
            $encrypted_verification_password ) );
        $oDataStore = $this->_sm->get('DataStore');
        $oDataStore->setItem($invoiceId, $plain_verification_password);

        mcrypt_generic_deinit ( $cipher );
    }

    /**
     * @param $ipgResponse
     * @return Response
     * @throws \Exception
     */
    public function getDecryptedResponse($ipgResponse)
    {
        $mConfig = $this->getConfig(self::CONFIG_NAME);

        $invoiceId = $ipgResponse['invoiceId'];
        $payment = $ipgResponse['payment'];

        $cipher = mcrypt_module_open ( MCRYPT_RIJNDAEL_128, '', 'cbc', '' );
        $oDataStore = $this->_sm->get('DataStore');
        $success = false;
        $plain_verification_password = $oDataStore->getItem($invoiceId, $success);
        if(!$success){
            throw new \Exception('Data decryption failed.');
        }

        //same as in callback
        $iv = $mConfig['initializeVecto'];

        // load the saved $plain_verification_password
        mcrypt_generic_init ( $cipher, $plain_verification_password, $iv );
        $payment_info = mdecrypt_generic ( $cipher, base64_decode ( $payment ) );
        mcrypt_generic_deinit ( $cipher );

        $responseData = explode('|', $payment_info);
//        return array(
//            'invoiceId'         => $responseData[0],
//            'amount'            => $responseData[1],
//            'customerMobile'    => $responseData[2],
//            'statusCode'        => $responseData[3],
//            'mCashReferenceId'  => $responseData[4],
//        );

        $invoiceId         = $responseData[0];
        $amount            = $responseData[1];
        $customerMobile    = $responseData[2];
        $statusCode        = $responseData[3];
        $mCashReferenceId  = $responseData[4];

        //build response object
        $oResponse = new Response();
        $oResponse->setPaymentMode(PaymentRefundMode::mCash)
            ->setBookingReference($invoiceId)
            ->setIpgReference($mCashReferenceId);

        //if payment success
        if($statusCode==self::STATUS_SUCCESS){
            $oResponse->setSuccess( true );
            $oResponse->setPaidAmount($amount);
        }
        else{
            //failed. Set error
            $oResponse->setSuccess( false );
            //pg error
            $oResponse->setError('Your payment has failed by Mobitel. You have to try again.');
        }

        return $oResponse;
    }
}