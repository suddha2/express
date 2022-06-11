<?php
namespace Cron\Controller;

use Zend\Mvc\Controller\AbstractActionController;

use Data\Manager\EmailQueue;
use Api\Manager\Booking;

class EmailResendController extends AbstractActionController{
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
   public function resendEmailAction(){
	   
		// Get all pending entries
		$emailQueue = $this->getServiceLocator()->get('Data\Manager\EmailQueue');
		$queuedEmail = $emailQueue->getEmailQueueItems();
		
		
		// If no items in table, print message and exit process.
		if(count($queuedEmail)<1){
			echo date('Y-m-d H:i:s')." \t No queued items  to process \n";
			exit();
		}
		
		// SMS Service code
		$oEmail = $this->getServiceLocator()->get('Alert\Email');
		
		// GET Booking details from server 
		$bookingManager = $this->getServiceLocator()->get('Api\Manager\Booking');
		// Sign-in as CRON job user to have access rights to all bookings
		$this->signInCRONUser();
		
		// Loop through PENDING sms items
		foreach($queuedEmail as $emailItem){ 
			echo date('Y-m-d H:i:s')."\t Get booking data for ".$emailItem['booking_reference']." \n";
			try{
				$oBooking = $bookingManager->getBookingRefById($emailItem['booking_reference']);
				echo date('Y-m-d H:i:s')."\t Booking data received for ".$emailItem['booking_reference']." \n";
				 // SET BOOKING DOMAIN FOR CUSTOM EMAIL
				switch ($oBooking->division->id){
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
				// If booking status is CONFIRM, send email else skip.
				if($oBooking->status->code == Booking::STATUS_CODE_CONFIRM){	
					echo date('Y-m-d H:i:s')."\t Sending Email for ".$emailItem['booking_reference']." \n";
					$oEmail->sendBookingSuccessEmail($oBooking);
					echo date('Y-m-d H:i:s')."\t sent Email for ".$emailItem['booking_reference']." \n";
					$emailQueue->updateEmailQueue($emailItem['booking_reference'], 'SENT');
					echo date('Y-m-d H:i:s')."\t EMAIL Queue updated for ".$emailItem['booking_reference']." \n";
				} else if($oBooking->status->code == Booking::STATUS_CODE_CANCELLED){	
					echo date('Y-m-d H:i:s')."\t Booking Status is CANCEL for ".$emailItem['booking_reference']." \n";
					$emailQueue->updateEmailQueue($emailItem['booking_reference'], Booking::STATUS_CODE_CANCELLED);
					echo date('Y-m-d H:i:s')."\t EMAIL Queue updated for ".$emailItem['booking_reference']." \n";
				}else{
					continue;
				}
			}catch (\Exception $e) {
				echo date('Y-m-d H:i:s')."\t Error getting booking data for ".$emailItem['booking_reference']." \n";
				echo $e;
			}
			sleep(1);
		}
	}
}