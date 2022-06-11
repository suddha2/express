<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 3/19/15
 * Time: 10:41 AM
 */

namespace Api\Client\Soap;
use Api\Client\Soap\ExpressWebService;
use Api\Client\Soap\Core\ExpressLKSearchImplService;
use Zend\ServiceManager\ServiceManager;

/**
 * Class ReservationService
 */
class ReservationService extends ExpressLKSearchImplService {
    use ExpressWebService;

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
        parent::__construct(array('cache_wsdl' => WSDL_CACHE_NONE), $this->_config['wsdl']['search']);

        $this->setHeaders();
    }
}