<?php


namespace Data\Manager;


use Data\InjectorBase;
use Data\Storage\EmailQueueStorage;

class EmailQueue extends InjectorBase
{
    private $emailQueueStorage = null;

    
    public function addToEmailQueue($bookingReference)
    {
		return $this->getEmailQueueStorage()->addToEmailQueue($bookingReference);
    }

    public function updateEmailQueue($bookingReference, $status){
        return $this->getEmailQueueStorage()->update($bookingReference,$status);
    }

    private function getEmailQueueStorage()
    {
        if(is_null($this->emailQueueStorage)){
            $this->emailQueueStorage = new EmailQueueStorage($this->getServiceManager()->get('Zend\Db\Adapter\Adapter'));
        }
        return $this->emailQueueStorage;
    }
	
	public function getEmailQueueItems(){
		return $this->getEmailQueueStorage()->getEmailQueueItems();
	}
}