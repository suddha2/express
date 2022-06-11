<?php
/**
 * User: Lakmal
 * Date: 11/22/17
 * Time: 21:56
 */

namespace Data\Storage;

use Zend\Db\Adapter\Adapter;

/**
 * Class BookingPaymentAudit
 * @package Data\Storage
 */
class BookingPaymentAudit
{
    const TABLE_NAME = 'aud_booking_payment';

    protected $adapter;
    protected $tblGW;

    public function __construct(Adapter $adapter)
    {
        $this->adapter = $adapter;
        $this->tblGW = new \Zend\Db\TableGateway\TableGateway(self::TABLE_NAME, $this->adapter);
    }

    /**
     * Create a new audit record for a booking pamyment initiated.
     * @param $bookingReference The booking reference generated for the booking
     * @param $provider The payment provider used for this payment
     * @param $ourReference The unique reference we have passed to the payment provider for the payment
     * @param $amount The amount of the transaction
     * @param $currency The currency used for the transaction
     * @param $descriptionToProvider Any descriptions/comments passed to the payment provider
     * @param $initParameters All initialization parameters combined together seperated by a pipe (|) sign
     * @param $providerInvokedTime The time initial invokation was done from the webapp
     * @return int
     */
    public function create($bookingReference, $provider, $ourReference, $amount, $currency, $descriptionToProvider, 
	$initParameters, $providerInvokedTime, $user,$session,$userAgent,$userIP,$reqHeader)
    {
		return $this->tblGW->insert([
            'booking_reference' => $bookingReference,
            'provider' => $provider,
            'our_reference' => $ourReference,
            'amount' => $amount,
            'currency' => $currency,
            'description_to_provider' => $descriptionToProvider,
            'init_parameters' => $initParameters,
            'provider_invoked_time' => $providerInvokedTime,
            'created_by' => $user,
			'session_id'=>$session,
			'user_agent'=>$userAgent,
			'user_ip'=>$userIP,
			'request_header'=>json_encode($reqHeader)
        ]);
    }

    /**
     * Update an  audit record when the payment provider respond back.
     * @param $ourReference The unique reference we have passed to the payment provider for the payment
     * @param $theirReference The reference sent by the payment provider
     * @param $status The status returned by the payment provider
     * @param $message The message returned back by the payment provider
     * @param $respondedTime The time we received the responce from payment provider
     * @param $user Pass the user that initiate this call. It could be the web-user
     * @return int
     */
    public function update($ourReference, $theirReference, $status, $message, $respondedTime, $user,$creditCard,$authCode,$settlementDate,$feeReference){
        return $this->tblGW->update([
            'their_reference' => $theirReference,
            'status' => $status,
            'message_from_provider' => $message,
            'provider_responded_time' => $respondedTime,
            'modified_by' => $user,
			'credit_card' =>$creditCard,
			'auth_code' =>$authCode,
			'settlement_date' => $settlementDate,
			'fee_reference' =>$feeReference
			
        ], [
            'our_reference' => $ourReference
        ]);
    }
	
	public function updateProviderInvokeTime($ourReference,$providerInvokedTime,$initParameters){
		return $this->tblGW->update(['provider_invoked_time' => $providerInvokedTime],
		
						[ 'our_reference' => $ourReference
        ]);
	}
	
	public function getBookingAudData($reference){
		return $this->tblGW->select(array("booking_reference"=>$reference))->current();
	}
}