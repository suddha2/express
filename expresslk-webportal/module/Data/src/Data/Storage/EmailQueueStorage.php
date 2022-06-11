<?php


namespace Data\Storage;

use Zend\Db\Adapter\Adapter;

class EmailQueueStorage
{
    const TABLE_NAME = 'booking_email_queue';
	const EMAIL_STATUS_PENDING = 'PENDING';
    protected $adapter;
    protected $tblGW;

    public function __construct(Adapter $adapter)
    {
        $this->adapter = $adapter;
        $this->tblGW = new \Zend\Db\TableGateway\TableGateway(self::TABLE_NAME, $this->adapter);
    }

   public function addToEmailQueue($bookingReference)
    {
        return $this->tblGW->insert([
            'booking_reference' => $bookingReference,
            'created_date' => date('Y-m-d H:i:s'),
            'modified_date' => date('Y-m-d H:i:s'),
            'status' => self::EMAIL_STATUS_PENDING,
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
	
	public function getEmailQueueItems(){
		// return $this->tblGW->select(array('status' => self::EMAIL_STATUS_PENDING))->limit(10)->toArray();
		$resultSet = $this->adapter->query('SELECT * FROM '.self::TABLE_NAME.' WHERE `status` = ? limit 10', [self::EMAIL_STATUS_PENDING])->toArray();
		return $resultSet;
	}
}