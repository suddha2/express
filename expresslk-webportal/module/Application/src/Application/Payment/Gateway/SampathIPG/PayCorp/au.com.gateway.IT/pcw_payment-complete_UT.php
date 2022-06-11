<?php include '../au.com.gateway.client/GatewayClient.php'; ?>
<?php include '../au.com.gateway.client.config/ClientConfig.php'; ?>
<?php include '../au.com.gateway.client.component/RequestHeader.php'; ?>
<?php include '../au.com.gateway.client.component/CreditCard.php'; ?>
<?php include '../au.com.gateway.client.component/TransactionAmount.php'; ?>
<?php include '../au.com.gateway.client.component/Redirect.php'; ?>
<?php include '../au.com.gateway.client.facade/BaseFacade.php'; ?>
<?php include '../au.com.gateway.client.facade/Payment.php'; ?>
<?php include '../au.com.gateway.client.root/PaycorpRequest.php'; ?>
<?php include '../au.com.gateway.client.root/PaycorpResponse.php'; ?>
<?php include '../au.com.gateway.client.payment/PaymentCompleteRequest.php'; ?>
<?php include '../au.com.gateway.client.payment/PaymentCompleteResponse.php'; ?>
<?php include '../au.com.gateway.client.utils/IJsonHelper.php'; ?>
<?php include '../au.com.gateway.client.helpers/PaymentCompleteJsonHelper.php'; ?>
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
date_default_timezone_set('Asia/Colombo');
error_reporting(E_ALL);
ini_set('display_errors', 0);
/*------------------------------------------------------------------------------
STEP1: Build ClientConfig object
------------------------------------------------------------------------------*/
$ClientConfig = new ClientConfig();
$ClientConfig->setServiceEndpoint("https://sampath.paycorp.com.au/rest/service/proxy");
$ClientConfig->setAuthToken("346e3f69-2273-4043-b56c-fe8d8b1f994e");
$ClientConfig->setHmacSecret("0E7L1171R2KmEmpM");
$ClientConfig->setValidateOnly(FALSE);
/*------------------------------------------------------------------------------
STEP2: Build Client object
------------------------------------------------------------------------------*/

$Client = new GatewayClient($ClientConfig);
/*------------------------------------------------------------------------------
STEP3: Build PaymentCompleteRequest object
------------------------------------------------------------------------------*/
$completeRequest = new PaymentCompleteRequest();
$completeRequest->setClientId(14000227);
$completeRequest->setReqid($_GET['reqid']);
/*------------------------------------------------------------------------------
STEP4: Process PaymentCompleteRequest object
------------------------------------------------------------------------------*/
$completeResponse = $Client->payment()->complete($completeRequest);
/*------------------------------------------------------------------------------
STEP5: Process PaymentCompleteResponse object
------------------------------------------------------------------------------*/
echo '<br><br>PCW Payment-Complete Respopnse: ----------------------------------';
echo '<br>Txn Reference : ' . $completeResponse->getTxnReference();
echo '<br>Response Code : ' . $completeResponse->getResponseCode();
echo '<br>Response Text : ' . $completeResponse->getResponseText();
echo '<br>Settlement Date : ' . $completeResponse->getSettlementDate();
echo '<br>Auth Code : ' . $completeResponse->getAuthCode();
echo '<br>Token : ' . $completeResponse->getToken();
echo '<br>Token Response Text: ' . $completeResponse->getTokenResponseText();
echo '<br>----------------------------------------------------------------------';
?>
