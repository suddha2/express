<?php include '../au.com.gateway.client/GatewayClient.php'; ?>
<?php include '../au.com.gateway.client.config/ClientConfig.php'; ?>
<?php include '../au.com.gateway.client.component/RequestHeader.php'; ?>
<?php include '../au.com.gateway.client.component/CreditCard.php'; ?>
<?php include '../au.com.gateway.client.component/TransactionAmount.php'; ?>
<?php include '../au.com.gateway.client.component/Redirect.php'; ?>
<?php include '../au.com.gateway.client.facade/BaseFacade.php'; ?>
<?php include '../au.com.gateway.client.facade/Payment.php'; ?>
<?php include '../au.com.gateway.client.payment/PaymentInitRequest.php'; ?>
<?php include '../au.com.gateway.client.payment/PaymentInitResponse.php'; ?>
<?php include '../au.com.gateway.client.root/PaycorpRequest.php'; ?>
<?php include '../au.com.gateway.client.utils/IJsonHelper.php'; ?>
<?php include '../au.com.gateway.client.helpers/PaymentInitJsonHelper.php'; ?>
<?php include '../au.com.gateway.client.utils/HmacUtils.php'; ?>
<?php include '../au.com.gateway.client.utils/CommonUtils.php'; ?>
<?php include '../au.com.gateway.client.utils/RestClient.php'; ?>
<?php include '../au.com.gateway.client.enums/TransactionType.php'; ?>
<?php include '../au.com.gateway.client.enums/Version.php'; ?>
<?php include '../au.com.gateway.client.enums/Operation.php'; ?>
<?php include '../au.com.gateway.client.facade/Vault.php'; ?>
<?php include '../au.com.gateway.client.facade/Report.php'; ?>
<?php include '../au.com.gateway.client.facade/AmexWallet.php'; ?>

<?php

function hostedPage($url) {
            
      header("Location: ".$url);
}

date_default_timezone_set('Asia/Colombo');
error_reporting(E_ALL);
ini_set('display_errors', 0);
/*------------------------------------------------------------------------------
STEP1: Build ClientConfig object
------------------------------------------------------------------------------*/
$ClientConfig = new ClientConfig();
$ClientConfig->setServiceEndpoint("https://sampath.paycorp.com.au/rest/service/proxy");
$ClientConfig->setAuthToken("346e3f69-2273-4043-b56c-fe8d8b1f994e");
$ClientConfig->setHmacSecret("346e3f69-2273-4043-b56c-fe8d8b1f994e");
$ClientConfig->setValidateOnly(FALSE);
/*------------------------------------------------------------------------------
STEP2: Build Client object
------------------------------------------------------------------------------*/
$Client = new GatewayClient($ClientConfig);
/*------------------------------------------------------------------------------
STEP3: Build PaymentInitRequest object
------------------------------------------------------------------------------*/
$initRequest = new PaymentInitRequest();
$initRequest->setClientId(14000227);
$initRequest->setTransactionType(TransactionType::$PURCHASE);
$initRequest->setClientRef("merchant_reference");
$initRequest->setComment("merchant_additional_data");
$initRequest->setTokenize(FALSE);
$initRequest->setExtraData(array("ADD-KEY-1" => "ADD-VALUE-1", "ADD-KEY-2" => "ADD-VALUE-2"));
// sets transaction-amounts details (all amounts are in cents)
$transactionAmount = new TransactionAmount();
$transactionAmount->setTotalAmount(0);
$transactionAmount->setServiceFeeAmount(0);
$transactionAmount->setPaymentAmount(1010);
$transactionAmount->setCurrency("AUD");
$initRequest->setTransactionAmount($transactionAmount);
// sets redirect settings
$redirect = new Redirect();
$redirect->setReturnUrl("http://localhost/paycorp-paycorp-client-php-demo-d4ad3b9e3486/au.com.gateway.IT/pcw_payment-complete_UT.php");
$redirect->setReturnMethod("GET");
$initRequest->setRedirect($redirect);

/*------------------------------------------------------------------------------
STEP4: Process PaymentInitRequest object
------------------------------------------------------------------------------*/
$initResponse = $Client->payment()->init($initRequest);

hostedPage($initResponse->getPaymentPageUrl());

/*------------------------------------------------------------------------------
STEP5: Extract PaymentInitResponse object
------------------------------------------------------------------------------*/
echo '<br><br>PCW Payment-Init Respopnse: --------------------------------------';
echo '<br>Req Id : ' . $initResponse->getReqid();
echo '<br>Payment Page Url : ' . $initResponse->getPaymentPageUrl();
echo '<br>Expire At : ' . $initResponse->getExpireAt();
echo '<br>------------------------------------------------------------------<br>';
?>

<html>
    <head></head>
    <body>
        <div style="float:left">
            <br><br><br><br>
           <!--            <iframe class="col-lg-12"  height="600px" width="600px" src="<?php echo $initResponse->getPaymentPageUrl(); ?>">-->
        </div>
    </body>
</html>
