<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 1/12/15
 * Time: 7:53 AM
 */

namespace Application\Payment\Gateway;

use Api\Client\Soap\Core\PaymentRefundMode;
use Application\Payment\Gateway\IO\Request;
use Application\Payment\Gateway\IO\Response;
use Data\Manager\PaymentAudit;

class DialogEzCash extends Base{

    //configuration name in main config file
    const CONFIG_NAME       = 'dialog-ipg';
    /**
     * Payment return statuses
     */
    const STATUS_SUCCESS    = '2';
    const STATUS_FAILED     = '3';
    const STATUS_SYSTEM_ERROR = '4';
    const STATUS_CUSTOMER_BLOCKED = '15';
    const STATUS_SESSION_EXPIRED = '16';

    /**
     * Get encrypted data for IPG
     * @param $transactionId
     * @param $transactionAmount
     * @param string $orderDescription
     * @return Request
     * @throws \Exception
     */
    public function getEncryptedRequest($transactionId, $transactionAmount, $orderDescription = '')
    {
        $dConfig = $this->getConfig(self::CONFIG_NAME);

        //get scheme
        $scheme = $this->getServiceManager()->get('Request')->getUri()->getScheme();
        //build return url from what's in configs
        $returnUrl          = $scheme . '://' . $this->_config['system']['serverName'] . $dConfig['returnUrl'];
        $mcode              = $dConfig['merchantCode']; //merchant code
        $sensitiveData      = $mcode.'|'.$transactionId.'|'.$transactionAmount.'|'.$returnUrl; // query string
        $publicKey          = $dConfig['publicKey'];

        $encrypted = '';
        if (!openssl_public_encrypt($sensitiveData, $encrypted, $publicKey)){
            throw new \Exception('Failed to encrypt data');
        }
        $requestData = base64_encode($encrypted);

        $payload = '<form action="'. $dConfig['requestUrl'] .'" method="post">
                    <input type="hidden" name="merchantInvoice" value="'. $requestData .'">
                </form>';

		// Audit entry before calling ipg session creation.
		$paymentAudit = $this->getServiceManager()->get('Data\Manager\PaymentAudit');
        $paymentAudit->savePaymentAudit($transactionId,self::CONFIG_NAME, $transactionId, $transactionAmount,"LKR", $orderDescription, $requestData,"BBK");
		
        $request = new Request();
        $request->setRequestProcessType(Request::TYPE_FORMSUBMIT)
            ->setRequestPayload($payload);

        try{
            /** @var PaymentAudit $paymentAudit */
            //$paymentAudit = $this->getServiceManager()->get('Data\Manager\PaymentAudit');
            //$paymentAudit->savePaymentAudit($transactionId,self::CONFIG_NAME, $transactionId, $transactionAmount,"LKR", $orderDescription, $requestData,"BBK");
			$paymentAudit->updateProviderInvokeTime($transactionId,$requestData);
		}catch (\Exception $e){
//            throw new \Exception($e->getMessage());
        }

        return $request;
    }

    /**
     * Decrypt dialog response and get response object
     * @param $merchantReciept
     * @return Response
     * @throws \Exception
     */
    public function getDecryptedResponse($merchantReciept)
    {
        $dConfig = $this->getConfig(self::CONFIG_NAME);

        $decrypted = '';
        $privateKey = $dConfig['privateKey'];

        $encrypted = base64_decode($merchantReciept); // decode the encrypted query string
        if (!openssl_private_decrypt($encrypted, $decrypted, $privateKey)){
            throw new \Exception('Failed to decrypt data');
        }
        $responseData = explode('|', $decrypted);

        $transactionId     = $responseData[0];
        $statusCode        = $responseData[1];
        $statusDescription = $responseData[2];
        $amount            = $responseData[3];
        $merchantCode      = $responseData[4];
        $walletReferenceId = $responseData[5];

        //build response object
        $oResponse = new Response();
        $oResponse->setPaymentMode(PaymentRefundMode::eZCash)
            ->setBookingReference($transactionId)
            ->setIpgReference($walletReferenceId);

        try{
            /** @var PaymentAudit $paymentAudit */
            $paymentAudit = $this->getServiceManager()->get('Data\Manager\PaymentAudit');
            $paymentAudit->updatePaymentAudit($transactionId, $walletReferenceId, $statusCode, $statusDescription,"BBK");
        }catch (\Exception $e){
        }

        //if payment success
        if($statusCode==self::STATUS_SUCCESS){
            $oResponse->setSuccess( true );
            $oResponse->setPaidAmount($amount);
        }
        else{
            //failed. Set error
            $oResponse->setSuccess( false );
            $sysConfig = $this->getConfig('system');
            //pg error
            if(in_array($statusCode, array(DialogEzCash::STATUS_SESSION_EXPIRED, DialogEzCash::STATUS_SYSTEM_ERROR))){
                $oResponse->setError('The payment was failed by eZcash, due to a technical fault in the eZcash IPG.');
                $oResponse->setError('Sorry for the inconvenience caused! Seems like eZcash internet payment gateway is having a temporary technical fault. Please contact one of our passenger services executives to inform about this experience. They will help you to reserve your seats online with the next best alternative, while taking corrective actions with eZcash support team to get the things back on the right track. Thank you for using BusBooking.lk services! We are here to serve you.
                                        <br/>eZcash Hotline: 7111 eZcash USSD services: #111# Online Support: www.ezcash.lk '. $sysConfig['siteName'] .' Hotline: '. $sysConfig['hotlinePhone'] .'');
            }
            $oResponse->setError('Your payment has failed by Dialog. You have to try again.');
        }

        return $oResponse;
    }

}