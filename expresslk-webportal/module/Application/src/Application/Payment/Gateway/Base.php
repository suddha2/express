<?php
/**
 * Created by PhpStorm.
 * User: udantha
 * Date: 6/12/15
 * Time: 10:17 AM
 */

namespace Application\Payment\Gateway;

use Application\Payment\Gateway\IO\Response;
use Zend\ServiceManager\ServiceManager;

abstract class Base {

    protected $_sm;

    protected $_config;

    /**
     * @param ServiceManager $serviceManager
     */
    public function __construct(ServiceManager $serviceManager)
    {
        $this->_sm = $serviceManager;
        //set sms config
        $config = $this->_sm->get('Config');
        $this->_config = $config;
    }

    /**
     * get system config item
     * @param $configOption
     * @return mixed
     * @throws \Exception
     */
    protected function getConfig($configOption)
    {
        if(isset($this->_config[$configOption])){
            return $this->_config[$configOption];
        }else{
            throw new \Exception('Configuration key not available in payment');
        }
    }

    /**
     * @return ServiceManager
     */
    protected function getServiceManager()
    {
        return $this->_sm;
    }

    /**
     * @param int $transactionId
     * @param float $transactionAmount
     * @param string $orderDescription
     * @return Response
     */
    abstract public function getEncryptedRequest($transactionId, $transactionAmount, $orderDescription = '');

    /**
     * @param mixed $ipgResponse
     * @return Response
     */
    abstract public function getDecryptedResponse($ipgResponse);
}