<?php
/**
 * User: Lakmal
 * Date: 11/22/17
 * Time: 21:56
 */

namespace Data\Storage;

use Zend\Db\Adapter\Adapter;
use Zend\Db\Sql\Sql;


/**
 * Class SmsQueueStorage
 * @package Data\Storage
 */
class SmsQueueStorage
{
    const TABLE_NAME = 'booking_sms_queue';
	const SMS_STATUS_PENDING = 'PENDING';
    protected $adapter;
    protected $tblGW;

    public function __construct(Adapter $adapter)
    {
        $this->adapter = $adapter;
        $this->tblGW = new \Zend\Db\TableGateway\TableGateway(self::TABLE_NAME, $this->adapter);
    }

   public function addToSMSQueue($bookingReference)
    {
        return $this->tblGW->insert([
            'booking_reference' => $bookingReference,
            'created_date' => date('Y-m-d H:i:s'),
            'modified_date' => date('Y-m-d H:i:s'),
            'status' => self::SMS_STATUS_PENDING,
        ]);
    }

    public function update($bookingReference,$status)
    {
        return $this->tblGW->update([
            'status' => $status,
            'modified_date' => date('Y-m-d H:i:s')
			], [
				'booking_reference' => $bookingReference
			]);
    }
	
	public function getSMSQueueItems(){
		// return $this->tblGW->select(array('status' => self::SMS_STATUS_PENDING))->toArray();
		$resultSet = $this->adapter->query('SELECT * FROM '.self::TABLE_NAME.' WHERE `status` = ? limit 10', [self::SMS_STATUS_PENDING])->toArray();
		return $resultSet;
	}
	
}