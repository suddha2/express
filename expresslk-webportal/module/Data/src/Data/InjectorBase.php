<?php


namespace Data;


use Zend\ServiceManager\ServiceManager;

class InjectorBase
{
    private $_sm;

    public function __construct(ServiceManager $serviceManager)
    {
        $this->_sm = $serviceManager;
    }

    /**
     * @return ServiceManager
     */
    protected function getServiceManager()
    {
        return $this->_sm;
    }
}