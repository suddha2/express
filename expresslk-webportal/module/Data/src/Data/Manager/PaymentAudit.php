<?php
/**
 * User: Lakmal
 * Date: 11/22/17
 * Time: 22:22
 */

namespace Data\Manager;


use Data\InjectorBase;
use Data\Storage\BookingPaymentAudit;

class PaymentAudit extends InjectorBase
{
    /** @var BookingPaymentAudit  */
    private $bookingPaymentAudit = null;

    /**
     * Create payment audit
     * @param $version
     * @param $bookingReference
     * @param $provider
     * @param $ourReference
     * @param $theirReference
     * @param $amount
     * @param $currency
     * @param $descriptionToProvider
     * @param $initParameters
     * @param $providerInvokedTime
     * @return int
     */
    public function savePaymentAudit($bookingReference, $provider, $ourReference, $amount, $currency, $descriptionToProvider, $initParameters, $user)
    {
		$date = new \DateTime();
        $userIP = $this->getUserIP();
		$headers = apache_request_headers();


		return $this->getBookingPaymentAudit()->create($bookingReference, $provider,
		$ourReference, $amount, $currency, $descriptionToProvider, $initParameters,null,$user,session_id(),$_SERVER['HTTP_USER_AGENT'],$userIP,$headers);
    }

	public function updateProviderInvokeTime($bookingReference,$initParams){
		$date = new \DateTime();
		$providerInvokedTime = $date->format('Y-m-d H:i:s');
		return $this->getBookingPaymentAudit()->updateProviderInvokeTime($bookingReference,$providerInvokedTime,$initParams);
	}
	
    // public function updatePaymentAudit($ourReference, $theirReference, $status, $message, $user){
        // $date = new \DateTime();
        // return $this->getBookingPaymentAudit()->update($ourReference, $theirReference, $status, $message, $date->format('Y-m-d H:i:s'), $user);
    // }
	
	public function updatePaymentAudit($ourReference, $theirReference, $status, $message, $user,$creditCard,$authCode,$settlementDate,$feeReference){
        $date = new \DateTime();
        return $this->getBookingPaymentAudit()->update($ourReference, $theirReference, $status, $message, $date->format('Y-m-d H:i:s'), $user,$creditCard,$authCode,$settlementDate,$feeReference);
    } 
	
    /**
     * @return BookingPaymentAudit|null
     */
    private function getBookingPaymentAudit()
    {
        if(is_null($this->bookingPaymentAudit)){
            $this->bookingPaymentAudit = new BookingPaymentAudit($this->getServiceManager()->get('Zend\Db\Adapter\Adapter'));
        }
        return $this->bookingPaymentAudit;
    }
	
	private function getUserIP() {
		if( array_key_exists('HTTP_X_FORWARDED_FOR', $_SERVER) && !empty($_SERVER['HTTP_X_FORWARDED_FOR']) ) {
			if (strpos($_SERVER['HTTP_X_FORWARDED_FOR'], ',')>0) {
				$addr = explode(",",$_SERVER['HTTP_X_FORWARDED_FOR']);
				return trim($addr[0]);
			} else {
				return $_SERVER['HTTP_X_FORWARDED_FOR'];
			}
		}
		else {
			return $_SERVER['REMOTE_ADDR'];
		}
	}
	public function getBookingAudData($bookingReference){
		return $this->getBookingPaymentAudit()->getBookingAudData($bookingReference);
	}
}