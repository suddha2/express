<?php
namespace Legacy;

use Legacy\Ticketing\Availability;
use Legacy\Ticketing\Search;
use Legacy\Ticketing\Session;
use Zend\Mvc\MvcEvent;

class Module
{
    public function onBootstrap(MvcEvent $e)
    {
        $application = $e->getApplication();
        $sm = $application->getServiceManager();
        $sharedManager = $application->getEventManager();

        //attach plugins into dispatch process
        //These plugins will execute the related dependacies. This should execute before ACL plugin
        $sharedManager->attach(MvcEvent::EVENT_ROUTE,
            function ($e) use ($sm) {
                $sm->get('ControllerPluginManager')->get('Legacy\Extend\Mobitel\MobitelCGW')
                    ->init($e);
            }
        );
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
                //Search service
                'Legacy\Ticketing\Search' => function ($sm) {
                    return new Search($sm);
                },
                'Legacy\Ticketing\Session' => function ($sm) {
                    return new Session($sm);
                },
                'Legacy\Ticketing\Availability' => function ($sm) {
                    return new Availability($sm);
                },
            )
        );
    }
}
