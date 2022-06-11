<?php


namespace App\Base;

use Zend\ServiceManager\ServiceManager;

class Injector {

    private $serviceManager;

    public function __construct(ServiceManager $serviceManager)
    {
        $this->serviceManager = $serviceManager;
    }

    /**
     * @return ServiceManager
     */
    protected function getServiceManager()
    {
        return $this->serviceManager;
    }
}