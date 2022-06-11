<?php

namespace Application\Payment\Gateway;

use Api\Client\Soap\Core\PaymentRefundMode;
use Application\Payment\Gateway\IO\Request;
use Application\Payment\Gateway\IO\Response;
use Data\Manager\PaymentAudit;

class PayPalExpressCheckout extends Base {

    //configuration name in main config file
    const CONFIG_NAME = 'paypal-ec';

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
        $config = $this->getConfig(self::CONFIG_NAME);
        //get scheme
        $scheme = $this->getServiceManager()->get('Request')->getUri()->getScheme();
        //build return and cancel urls from what's in configs
        $returnUrl = $scheme . '://' . $this->_config['system']['serverName'] . $config['returnUrl']
            . '?ref=' . urlencode($transactionId);

        $USDtoLKR = $config['exchangeRate'];
        $amountInUSD = round($transactionAmount / $USDtoLKR, 2);
        $paymentDetails = new \SpeckPaypal\Element\PaymentDetails(array(
            'amt' => $amountInUSD,
            'currencyCode' => 'USD',
        ));
		// Audit entry before calling ipg session creation.
		$paymentAudit = $this->getServiceManager()->get('Data\Manager\PaymentAudit');
		$paymentAudit->savePaymentAudit($transactionId,self::CONFIG_NAME, $transactionId, $paymentDetails->getAmt(),$paymentDetails->getCurrencyCode(), $orderDescription, null, "BBK");
			
		
		
        $paymentDetails->setInvNum($transactionId);
        $express = new \SpeckPaypal\Request\SetExpressCheckout(array('paymentDetails' => $paymentDetails));
        $express->setNoShipping(1);
        $express->setReturnUrl($returnUrl);
        $express->setCancelUrl($returnUrl);

        $paypalRequest = $this->getPayPalRequest();
        $response = $paypalRequest->send($express);
        if ($response->isSuccess()) {
            $token = $response->getToken();
            /* @var $oDataStore \Zend\Cache\Storage\StorageInterface */
            $oDataStore = $this->_sm->get('DataStore');
            $oDataStore->setItem('PayPalPaymentDetails_' . $transactionId, $paymentDetails);
            $oDataStore->setItem('PayPalLKRAmount_' . $transactionId, $transactionAmount);

            $payload = '<form action="'. $config['redirectUrl'] .'" method="GET">
                    <input type="hidden" name="token" value="'. htmlspecialchars($token) .'">
                    <input type="hidden" name="useraction" value="commit">
                </form>';

				
			
			
            $request = new Request();
            $request->setRequestProcessType(Request::TYPE_FORMSUBMIT)
                ->setRequestPayload($payload);

            try{
                /** @var PaymentAudit $paymentAudit */
                //$paymentAudit = $this->getServiceManager()->get('Data\Manager\PaymentAudit');
                //$paymentAudit->savePaymentAudit($transactionId,self::CONFIG_NAME, $transactionId, $paymentDetails->getAmt(),$paymentDetails->getCurrencyCode(), $orderDescription, $token, "BBK");
				
				$paymentAudit->updateProviderInvokeTime($transactionId,$token);
            }catch (\Exception $e){
            }

            return $request;
        } else {
            throw new \Exception("Could not connect to PayPal services!");
        }
    }

    /**
     * @param $ipgResponse
     * @return Response
     * @throws \Exception
     */
    public function getDecryptedResponse($ipgResponse)
    {
        $bookingReference = $ipgResponse['ref'];
        $token = $ipgResponse['token'];
        $payerId = isset($ipgResponse['PayerID']) ? $ipgResponse['PayerID'] : '';
        /* @var $oDataStore \Zend\Cache\Storage\StorageInterface */
        $oDataStore = $this->_sm->get('DataStore');
        /* @var $paymentDetails \SpeckPaypal\Element\PaymentDetails */
        $paymentDetails = $oDataStore->getItem('PayPalPaymentDetails_' . $bookingReference);
        $transactionAmount = $oDataStore->getItem('PayPalLKRAmount_' . $bookingReference);
        /** @var PaymentAudit $paymentAudit */
        $paymentAudit = $this->getServiceManager()->get('Data\Manager\PaymentAudit');

        if (! empty($token) && ! empty($paymentDetails)) {

            //build response object
            $oResponse = new Response();
            $oResponse->setPaymentMode(PaymentRefundMode::PayPal)
                ->setBookingReference($bookingReference);

            if (! empty($payerId)) {
                $captureExpress = new \SpeckPaypal\Request\DoExpressCheckoutPayment(array(
                    'token' => $token,
                    'payerId' => $payerId,
                    'paymentDetails' => $paymentDetails
                ));

                $paypalRequest = $this->getPayPalRequest();
                /* @var $response \SpeckPaypal\Response\Response */
                $response = $paypalRequest->send($captureExpress);
                if ($response->isSuccess()) {
                    $responseData = $response->toArray();
                    $transactionId = $responseData['PAYMENTINFO'][0]['TRANSACTIONID'];
                    $amt = $responseData['PAYMENTINFO'][0]['AMT'];
                    $currencyCode = $responseData['PAYMENTINFO'][0]['CURRENCYCODE'];
                    $oResponse
                        ->setPaidAmount($transactionAmount)
                        ->setActualAmount($amt)
                        ->setActualCurrency($currencyCode)
                        ->setIpgReference($transactionId)
                        ->setSuccess(true);

                    $paymentAudit->updatePaymentAudit($bookingReference, $transactionId, "Success", "Success","BBK");
                } else {
                    $oResponse->setSuccess(false)
                        ->setError('Your payment has failed by PayPal. You have to try again.');
                    $paymentAudit->updatePaymentAudit($bookingReference, "", "Failed", "Your payment has failed by PayPal. You have to try again","BBK");
                }
            } else {
                $oResponse->setSuccess(false)
                    ->setError('Your payment has failed by PayPal. You have to try again.');
                $paymentAudit->updatePaymentAudit($bookingReference, "", "Failed", "Your payment has failed by PayPal. You have to try again","BBK");
            }
            return $oResponse;
        } else {
            throw new \Exception("Missing PayPal details!");
        }
    }

    /**
     * Creates a request object
     *
     * @return \SpeckPaypal\Service\Request
     */
    protected function getPayPalRequest()
    {
        $config = $this->getConfig(self::CONFIG_NAME);
        $paypalConfig = new \SpeckPaypal\Element\Config(array(
            'username' => $config['username'],
            'password' => $config['password'],
            'signature' => $config['signature'],
            'endpoint' => $config['endpoint'],
        ));

        //set up http client
        $client = new \Zend\Http\Client;
        $client->setMethod('POST');
        $client->setAdapter(new \Zend\Http\Client\Adapter\Curl);
        // $client->getAdapter()->setOptions(['sslverifypeer' => false]);
        $paypalRequest = new \SpeckPaypal\Service\Request;
        $paypalRequest->setClient($client);
        $paypalRequest->setConfig($paypalConfig);

        return $paypalRequest;
    }
}