<?php
namespace Data;


use Data\Manager\Session;
use Data\Manager\WaitingList;
use Data\Manager\PaymentAudit;
use Data\Manager\SmsQueue;
use Data\Manager\EmailQueue;
use Data\Manager\BusScheduleQueue;
use Data\Manager\BusScheduleBusStopQueue;
use Data\Manager\WebResultSearchResultLog;


class Module
{
    public function onBootstrap(\Zend\Mvc\MvcEvent $e)
    {
        /** @var Session $storage */
        $storage = $e->getApplication()->getServiceManager()->get('Data\Manager\SessionStorage');
        $storage->hookDbSession()->setSessionStorage();
    }

    public function getConfig()
    {
        return include __DIR__ . '/config/module.config.php';
    }

    public function getAutoloaderConfig()
    {
        return array(
            'Zend\Loader\StandardAutoloader' => array(
                'namespaces' => array(
                    __NAMESPACE__ => __DIR__ . '/src/' . __NAMESPACE__,
                ),
            ),
        );
    }

    public function getServiceConfig()
    {

        return array(
            'factories' => array(
                'Data\Manager\SessionStorage'  => function ($sm) {
                    return new Session($sm);
                },
                'Data\Manager\WaitingList'  => function ($sm) {
                    return new WaitingList($sm);
                },
                'Data\Manager\PaymentAudit'  => function ($sm) {
                    return new PaymentAudit($sm);
                },
				'Data\Manager\SmsQueue'  => function ($sm) {
                    return new SmsQueue($sm);
                },
				'Data\Manager\EmailQueue'  => function ($sm) {
                    return new EmailQueue($sm);
                },
				'Data\Manager\BusScheduleQueue'  => function ($sm) {
                    return new BusScheduleQueue($sm);
                },
				'Data\Manager\BusScheduleBusStopQueue'  => function ($sm) {
                    return new BusScheduleBusStopQueue($sm);
                },
				'Data\Manager\WebResultSearchResultLog'  => function ($sm) {
                    return new WebResultSearchResultLog($sm);
                },
            )
        );
    }
}
