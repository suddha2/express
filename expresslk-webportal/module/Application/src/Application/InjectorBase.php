<?php


namespace Application;


use Zend\ServiceManager\ServiceManager;

class InjectorBase {

    private $_sm;

    public function __construct(ServiceManager $serviceManager)
    {
        $this->_sm = $serviceManager;
    }

    /**
     * @return ServiceManager
     */
    protected function getServiceLocator()
    {
        return $this->_sm;
    }
}