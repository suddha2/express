<?php
use Api\Client\Soap\Core\PaymentRefundMode;
use Application\Payment\Gateway\IO\Request;
use Application\Payment\Gateway\IO\Response;
use Data\Manager\PaymentAudit;


class PayHere extends Base {

    //configuration name in main config file
    const CONFIG_NAME = 'payhere';
	
	public function getEncryptedRequest($transactionId, $transactionAmount, $orderDescription = '',$additional_data = array())
    {
		$config = $this->getConfig(self::CONFIG_NAME);
		$payload=$this->generatePayLoad($config,$transactionId,$transactionAmount,$additional_data);
		
		// Audit entry before calling payhere session creation.
		$paymentAudit = $this->getServiceManager()->get('Data\Manager\PaymentAudit');
		$paymentAudit->savePaymentAudit($transactionId,self::CONFIG_NAME, $transactionId, transactionAmount,"LKR", $orderDescription, null, "BBK");

		$request = new Request();
        $request->setRequestProcessType(Request::TYPE_FORMSUBMIT)->setRequestPayload($payload);
		
		return $request;
	}
	
	private function generatePayLoad($config,$transactionId,$transactionAmount,$additional_data){
		$txt = '<html><body>
				<form method="post" action="'.$config["endPoint"].'">   
					<input type="hidden" name="merchant_id" value="'.$config["merchant_id"].'"> 
					<input type="hidden" name="return_url" value="'.$config["return_url"].'">
					<input type="hidden" name="cancel_url" value="'.$config["cancel_url"].'">
					<input type="hidden" name="notify_url" value="'.$config["notify_url"].'">  
					<input type="hidden" name="order_id" value="'.$transactionId.'">
					<input type="hidden" name="items" value="Bus booking"> 
					<input type="hidden" name="currency" value="LKR">
					<input type="hidden" name="amount" value="'.$transactionAmount.'">  
					<input type="hidden" name="first_name" value="">
					<input type="hidden" name="last_name" value=""> 
					<input type="hidden" name="email" value="">
					<input type="hidden" name="phone" value=""> 
					<input type="hidden" name="address" value="">
					<input type="hidden" name="city" value="">
					<input type="hidden" name="country" value="">  
				</form> 
				</body>
				</html>';
		return $txt;
	}
?>