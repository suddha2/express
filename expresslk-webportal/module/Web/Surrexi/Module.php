<?php
namespace Web\Surrexi;

use Zend\ModuleManager\Feature\AutoloaderProviderInterface;
use Zend\Mvc\MvcEvent;

class Module implements AutoloaderProviderInterface
{
    public function onBootstrap(MvcEvent $e)
    {

        $application   = $e->getApplication();

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
                    //change namespace to load sub module
                    __NAMESPACE__ => __DIR__ . '/src/' . str_replace("\\", "/", __NAMESPACE__),
                ),
            ),
        );
    }

    public function getServiceConfig()
    {

        return array(
            'factories' => array(

            )
        );
    }
}
