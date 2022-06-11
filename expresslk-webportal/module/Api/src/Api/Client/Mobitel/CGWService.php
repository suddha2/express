<?php


namespace Api\Client\Mobitel;


use Api\Client\Mobitel\CGW\MCAccMgr;
use Zend\ServiceManager\ServiceManager;

class CGWService extends MCAccMgr
{
    private $_config;

    protected $_serviceLocator;

    /**
     * @param ServiceManager $serviceManager
     */
    public function __construct(ServiceManager $serviceManager)
    {
        //save service manager
        $this->_serviceLocator = $serviceManager;
        //get config
        $this->_config = $serviceManager->get('Config');
        //call parent with wsdl and options
        parent::__construct(array(), $this->_config['client-api']['mobitel']['cgw']['wsdl']);

    }
}