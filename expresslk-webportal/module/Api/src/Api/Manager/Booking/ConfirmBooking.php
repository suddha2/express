<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 11/10/14
 * Time: 11:18 AM
 */

namespace Api\Manager\Booking;

use Api\Client\Soap\Core\AccountStatus;
use Api\Client\Soap\Core\allocation;
use Api\Client\Soap\Core\Client;
use Api\Client\Soap\Core\ClientDetail;
use Api\Client\Soap\Core\ConfirmationCriteria;
use Api\Client\Soap\Core\itemSeatAllocations;
use Api\Client\Soap\Core\PassengerDetail;
use Api\Client\Soap\Core\PassengerType;
use Api\Client\Soap\Core\SeatAllocations;
use Api\Client\Soap\Core\type;
use Api\Manager\Base;

class ConfirmBooking extends Base{

    protected $_session = null;

    private $_loggedInUserId = null;

    /**
     * set session variable
     * @param $session
     */
    public function setSession($session)
    {
        $this->_session = $session;
    }

    /**
     * Set user id
     * @param $userId
     */
    public function setLoggedInUserId($userId)
    {
        $this->_loggedInUserId = $userId;
    }

    /**
     * @param $heldItemIndex
     * @param $bookingData
     * @param null $paymentCriteria
     * @param null|\Api\Client\Soap\Core\PaymentMethodCriteria $paymentMethodCriteria
     * @return \Api\Client\Soap\Core\Booking
     * @throws \Application\Exception\SessionTimeoutException
     * @throws \Exception
     */
    public function confirm($heldItemIndex, $bookingData,
                            $paymentCriteria = null, $paymentMethodCriteria = null, $agentId = null)
    {
        $userId = $this->_loggedInUserId; //logged in user id

        $contactData    = $bookingData['contact'];
        $contactMobile  = (isset($contactData['mobileNo']) && !empty($contactData['mobileNo'])) ? $contactData['mobileNo'] : null;
        $contactNic     = (isset($contactData['nic']) && !empty($contactData['nic'])) ? $contactData['nic'] : null;
        $contactEmail   = (isset($contactData['email']) && !empty($contactData['email'])) ? $contactData['email'] : null;
        $contactName    = (isset($contactData['name']) && !empty($contactData['name'])) ? $contactData['name'] : null;

        $oClient = new ClientDetail();
        $oClient->nic = $contactNic;
        $oClient->firstName = $contactName;
        $oClient->mobile = $contactMobile;
        $oClient->email = $contactEmail;
        //$oClient->id = 0;

        /** @var ConfirmationCriteria $oConfirmationCrit */
        $oConfirmationCrit = Base::getEntityObject('ConfirmationCriteria');
		$oConfirmationCrit->paymentMethodCriteria=null;
		$oConfirmationCrit->paymentCriteria=null;
        $oConfirmationCrit->client = $oClient;
        //$oConfirmationCrit->userId = $userId;
        $oConfirmationCrit->agentId = $agentId;
        $oConfirmationCrit->remarks = ! empty($bookingData['remarks']) ? $bookingData['remarks'] : null;

        //set passengers
        $aPassengerDetail = array();
        $aAllocations = array();
        $passengerData  = $bookingData['passenger'];
        $i=0;
        foreach($passengerData as $iKey=>$passenger){
            $dob        = null;
            $firstName  = isset($passenger['name'])? $passenger['name'] : '';
            $lastName   = null;
            $middleName = null;
            $gender     = isset($passenger['gender']) ? $passenger['gender'] : null;
            $index      = $i;
            $mobile     = isset($passenger['mobileNo'])? $passenger['mobileNo'] : null;
            $nic        = isset($passenger['nic'])? $passenger['nic'] : null;
            $passengerType = isset($passenger['passengerType'])? $passenger['passengerType'] : PassengerType::Adult;
            $seatNo     = isset($passenger['seatNo'])? $passenger['seatNo'] : null;

            //set passenger detail array
            $oPD = new PassengerDetail($index, $passengerType);
            $oPD->mobile = $mobile;
            $oPD->nic = $nic;
            $oPD->firstName = $firstName;
            $oPD->gender = $gender;
            //$oPD->id = 0;

            $aPassengerDetail[] = $oPD;
            //set seat allocations array
            $aAllocations[] = new Allocation($index, $seatNo);
            $i++;
        }

        //item seat allocation
        $oItmSeatAlloc = new SeatAllocations($heldItemIndex);
        $oItmSeatAlloc->allocations = $aAllocations;

        $oConfirmationCrit->itemSeatAllocations = array($oItmSeatAlloc);
        //set passenger detail object
        $oConfirmationCrit->passengers = $aPassengerDetail; //list of passenger details

        //if payment is added while confirming
        if ($paymentCriteria) {
            $oConfirmationCrit->paymentCriteria = $paymentCriteria;
        }

        //if there's a payment method criteria available
        if(!is_null($paymentMethodCriteria)){
            $oConfirmationCrit->paymentMethodCriteria = $paymentMethodCriteria;
        }

        $oSearchService = $this->getSearchService();
        /**
         * confirm booking through service
         * @var $oConfirmResponse \Api\Client\Soap\Core\ConfirmResponse
         */
		 
		 
		// $myfile = fopen("C:\wamp64\logs\PlaceBooking_CONFIRM_CRITERIA_DATA.txt", "a") or die("Unable to open file!");
		// fwrite($myfile,"\n ==================================".date('Y-m-d H:i:sa'));
		// fwrite($myfile,"\n CONFIRMATION CRITERIA DATA\n");
		// fwrite($myfile,print_r($oConfirmationCrit,true));		
		// fclose($myfile);

        $oConfirmResponse = $oSearchService->confirm($this->_session, $oConfirmationCrit);
		
        //check if status is okay
        if($this->responseIsValid($oConfirmResponse)) {
            $data = $oConfirmResponse->data;

            return $data;
        }
    }
}