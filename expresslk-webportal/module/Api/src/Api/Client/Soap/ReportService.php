<?php
namespace Api\Client\Soap;

use Api\Client\Soap\ExpressWebService;
use Api\Client\Soap\Core\ExpressLKReportManagerImplService;
use Zend\ServiceManager\ServiceManager;

/**
 * Class ReportService
 */
class ReportService extends ExpressLKReportManagerImplService {
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
		parent::__construct(array(), $this->_config['wsdl']['reports']);

		$this->setHeaders();
	}
}
