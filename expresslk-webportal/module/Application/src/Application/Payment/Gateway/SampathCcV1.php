<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 6/12/15
 * Time: 10:15 AM
 */

namespace Application\Payment\Gateway;


use Api\Client\Soap\Core\PaymentRefundMode;
use Application\Payment\Gateway\IO\Request;
use Application\Payment\Gateway\IO\Response;

class SampathCcV1 extends Base{

    //configuration name in main config file
    const CONFIG_NAME       = 'sampath-ipg';

    const PAYSTATUS_SUCCESS = '50020';

    /**
     * Error codes
     */
    const ERROR_CALL_ISSUER = 1;
    const ERROR_SERVER_ERROR = 10004;
    const ERROR_TRANSACTION_NOTALLOWED = 10040;
    const ERROR_CARD_DECLINED = 13004;
    const ERROR_CARD_INSUFFICIENT_FUNDS = 13006;

    private $_payConfig = array();

    public function __construct($serviceManager)
    {
        parent::__construct($serviceManager);

        //set in class payment config
        $conf = $this->getConfig(self::CONFIG_NAME);
        $this->_payConfig = $conf['v1'];
    }

    /**
     * @param $transactionId
     * @param $transactionAmount
     * @param string $orderDescription
     * @return Request
     */
    public function getEncryptedRequest($transactionId, $transactionAmount, $orderDescription = '')
    {
        $pgInstanceId   = $this->_payConfig['pgInstanceId'];
        $merchantId     = $this->_payConfig['merchantId'];
        $perform        = 'initiatePaymentCapture#sale';
        $currencyCode   = '144';
        $merchantReferenceNo = $transactionId;
        $hashKey        = $this->_payConfig['hashKey'];
        $amount         = floatval($transactionAmount)*100;

        $messageHash = $pgInstanceId."|".$merchantId."|".$perform."|".$currencyCode."|".$amount."|".$merchantReferenceNo."|".$hashKey."|";
        $message_hash = "CURRENCY:7:".base64_encode(sha1($messageHash, true));

        $fields = array(
            'pg_instance_id' => $pgInstanceId,
            'merchant_id' => $merchantId,
            'perform' => $perform,
            'currency_code' => $currencyCode,
            'amount' => $amount,
            'merchant_reference_no' => $merchantReferenceNo,
            'order_desc' => $orderDescription,
            'message_hash' => $message_hash,
        );

        $html = '<form action="'. $this->_payConfig['requestUrl'] .'" method="post" id="__form__">';
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
     * Decrypt and retrieve values
     * @param $post
     * @return Response
     */
    public function getDecryptedResponse($post)
    {
        $pgInstanceId   = $this->_payConfig['pgInstanceId'];
        $merchantId     = $this->_payConfig['merchantId'];
        $hashKey        = $this->_payConfig['hashKey'];

        $transactionTypeCode = $post["transaction_type_code"];
        $installments = $post["installments"];
        $transactionId = $post["transaction_id"];

        $amount = $post["amount"];
        $exponent = $post["exponent"];
        $currencyCode = $post["currency_code"];
        $merchantReferenceNo = $post["merchant_reference_no"];

        $status = strval($post["status"]);
        $eci = $post["3ds_eci"];
        $pgErrorCode = $post["pg_error_code"];

        $pgErrorDetail = $post["pg_error_detail"];
        $pgErrorMsg = $post["pg_error_msg"];

        $messageHash = $post["message_hash"];


        $messageHashBuf = $pgInstanceId."|".$merchantId."|".$transactionTypeCode."|".$installments."|".$transactionId."|".$amount."|".$exponent."|".$currencyCode."|".$merchantReferenceNo."|".$status."|".$eci."|".$pgErrorCode."|".$hashKey."|";

        $messageHashClient = "13:".base64_encode(sha1($messageHashBuf, true));

        $hashMatch = false;
        //check if payment was succesfull
        if ($messageHash==$messageHashClient){
            $hashMatch=true;
        } else {
            $hashMatch=false;
        }

        //build response object
        $oResponse = new Response();
        $oResponse->setPaymentMode(PaymentRefundMode::Card)
            ->setBookingReference($merchantReferenceNo)
            ->setIpgReference($transactionId);

        //if payment success
        if($hashMatch && $status==self::PAYSTATUS_SUCCESS){
            $oResponse->setSuccess( true );
            $oResponse->setPaidAmount($amount);
        }
        else{
            //failed. Set error
            $oResponse->setSuccess( false );
            //if insufficient funds
            if(in_array($pgErrorCode, array(SampathCcV1::ERROR_CARD_INSUFFICIENT_FUNDS))){
                $oResponse->setError('The payment was rejected due to Insufficient funds in card.');
            }
            //if card declined
            if(in_array($pgErrorCode, array(SampathCcV1::ERROR_CARD_DECLINED))){
                $oResponse->setError('The payment was rejected due to Card has been declined by the issuer.');
            }
            if(in_array($pgErrorCode, array(SampathCcV1::ERROR_CALL_ISSUER))){
                $oResponse->setError('The payment was rejected due to Card has been declined. Please call the card issuer bank.');
            }
            //technical issue
            if(in_array($pgErrorCode, array(SampathCcV1::ERROR_SERVER_ERROR))){
                $oResponse->setError('Looks like internet payment gateway is having a temporary technical fault. Please try again.');
            }
            $oResponse->setError('Your payment has failed by Payment gateway. You have to try again.');
        }

        return $oResponse;
    }
}