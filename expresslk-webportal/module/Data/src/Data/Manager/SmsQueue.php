<?php


namespace Data\Manager;


use Data\InjectorBase;
use Data\Storage\SmsQueueStorage;

class SmsQueue extends InjectorBase
{
    private $smsQueueStorage = null;

    
    public function addToSMSQueue($bookingReference,$smsTemplate,$smsTo)
    {
		return $this->getSmsQueueStorage()->addToSMSQueue($bookingReference);
    }

    public function updateSMSQueue($bookingReference, $status){
        return $this->getSmsQueueStorage()->update($bookingReference,$status);
    }

    private function getSmsQueueStorage()
    {
        if(is_null($this->smsQueueStorage)){
            $this->smsQueueStorage = new SmsQueueStorage($this->getServiceManager()->get('Zend\Db\Adapter\Adapter'));
        }
        return $this->smsQueueStorage;
    }
	
	public function getSmsQueueItems(){
		return $this->getSmsQueueStorage()->getSMSQueueItems();
	}
}