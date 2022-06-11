<?php
namespace Cron\Controller;

use Zend\Mvc\Controller\AbstractActionController;

use Data\Manager\SmsQueue;
use Api\Manager\Booking;
use Application\Sms\Template;


class SmsResendController extends AbstractActionController{
	
	private function _getConfig()
    {
		$config = $this->getServiceLocator()->get('Config');
        //get username/password
        return $config['cron']['auth'];
    }
	private function signInCRONUser()
    {
        $config = $this->_getConfig();
        //get username/password
        $sUsername = $config['username'];
        $sPassword = $config['password'];
		
		$authService = $this->getServiceLocator()->get('AuthService');
		$adapter = $authService->getAdapter();
		$adapter->setIdentity($sUsername)->setCredential($sPassword);
		$result = $authService->authenticate();
	
    }
	public function resendSMSAction(){
		$smsQueue = $this->getServiceLocator()->get('Data\Manager\SmsQueue');
	   
		// Get all pending entries
		$queuedSms = $smsQueue->getSmsQueueItems();
		// exit();
		
		// If no items in table, print message and exit process.
		if(count($queuedSms)<1){
			echo date('Y-m-d H:i:s')." \t No queued items  to process \n";
			exit();
		}
		
		// SMS Service code
		$oSms = $this->getServiceLocator()->get('Alert\Sms');
		
		// GET Booking details from server 
		$bookingManager = $this->getServiceLocator()->get('Api\Manager\Booking');
		
		// Sign-in as CRON job user to have access rights to all bookings
		$this->signInCRONUser();
		
		
		//Valid Sri lankan Mobile number patters
		$re = '/^07[0125678]\\d{7}/m';
		
		

		$matches = null;
		
		// Loop through PENDING sms items
		foreach($queuedSms as $smsItem){ 
			echo date('Y-m-d H:i:s')."\t Get booking data for ".$smsItem['booking_reference']." \n";
			try{
				$oBooking = $bookingManager->getBookingRefById($smsItem['booking_reference']);
			
				echo date('Y-m-d H:i:s')."\t Booking data received for ".$smsItem['booking_reference']." \n";
				
				 // SET BOOKING DOMAIN FOR CUSTOM EMAIL
				switch ($oBooking->division->company->id){
						case 6 :
								\Application\Domain::setDomain(\Application\Domain::NAME_SLTB);
								break;
						case 4 :
								\Application\Domain::setDomain(\Application\Domain::NAME_SUPERLINE);
								break;
						default :
								\Application\Domain::setDomain(\Application\Domain::NAME_BUSBOOKING);
								break;
				}
				echo date('Y-m-d H:i:s')."\t Booking domain set to  ".\Application\Domain::getDomain()." \n";
				
				$template = '';
				// Only allow sms to sri lankan numbers 
				$returnValue = preg_match($re, $oBooking->client->mobileTelephone, $matches);
				
				
				
				$microD1 = sprintf("%06d",($oBooking->bookingTime - floor($oBooking->bookingTime)) * 1000000);
				$dt1 = new DateTime( date('Y-m-d H:i:s.'.$microD1, $oBooking->bookingTime) );

				$microD2 = sprintf("%06d",($oBooking->expiryTime - floor($oBooking->expiryTime)) * 1000000);
				$dt2 = new DateTime( date('Y-m-d H:i:s.'.$microD2, $oBooking->expiryTime) );

				$diff_date = $dt2->diff($dt1);

				$sendPayLaterSms = $diff_date->i > 15;

				
				if($returnValue==1){
					// If booking status is CONFIRM, send sms else skip.
					if($oBooking->status->code == Booking::STATUS_CODE_CONFIRM){
						$template = Template::BOOKING_SUCCESS;	
						echo date('Y-m-d H:i:s')."\t Sending success SMS for ".$smsItem['booking_reference']." \n";
						$oSms->sendBookingSms($oBooking, $template);
						echo date('Y-m-d H:i:s')."\t sent success SMS for ".$smsItem['booking_reference']." \n";
						$smsQueue->updateSMSQueue($smsItem['booking_reference'], 'SENT');
						echo date('Y-m-d H:i:s')."\t SMS Queue updated for ".$smsItem['booking_reference']." \n";
					} else if(($oBooking->status->code == Booking::STATUS_CODE_TENTATIVE) AND ($sendPayLaterSms) ){
						$template = Template::BOOKING_TENTETIVE;	
						echo date('Y-m-d H:i:s')."\t Sending success SMS for ".$smsItem['booking_reference']." \n";
						$oSms->sendBookingSms($oBooking, $template);
						echo date('Y-m-d H:i:s')."\t sent success SMS for ".$smsItem['booking_reference']." \n";
						$smsQueue->updateSMSQueue($smsItem['booking_reference'], 'SENT');
						echo date('Y-m-d H:i:s')."\t SMS Queue updated for ".$smsItem['booking_reference']." \n";
					} else if($oBooking->status->code == Booking::STATUS_CODE_CANCELLED){	
						echo date('Y-m-d H:i:s')."\t Booking Status is CANCEL for ".$smsItem['booking_reference']." \n";
						$smsQueue->updateSMSQueue($smsItem['booking_reference'], Booking::STATUS_CODE_CANCELLED);
						echo date('Y-m-d H:i:s')."\t SMS Queue updated for ".$smsItem['booking_reference']." \n";
					}
				}else {
					echo date('Y-m-d H:i:s')."\t Invalid mobile number ".$oBooking->client->mobileTelephone."  for Booking ".$smsItem['booking_reference']." \n";
					$smsQueue->updateSMSQueue($smsItem['booking_reference'], 'INVALID_NO');
				}
				
			}catch (\Exception $e) {
				echo date('Y-m-d H:i:s')."\t Error getting booking data for ".$smsItem['booking_reference']." \n";
				echo $e;
			}
			sleep(1);
		}
	}
}